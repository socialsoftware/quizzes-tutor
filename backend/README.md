# Microservices with Cadence

According to its creator Uber, 
> Cadence is a distributed, scalable, durable, and highly available orchestration engine to execute asynchronous long-running business logic in a scalable and resilient way.

Put in a simple way, it is a tool that aims to reduce complexity of distributed systems.

It offers a lot of [usecases](https://cadenceworkflow.io/docs/use-cases/) (periodic execution, orchestration/saga, event-driven application) but this project is only interested in the [Saga usecase](https://cadenceworkflow.io/docs/use-cases/orchestration/).


### References
- [Cadence's Github](https://github.com/uber/cadence)
- [Cadence's official website](https://cadenceworkflow.io)

### Credits
- [cadence-spring-boot](https://github.com/frtelg/cadence-spring-boot)
- [cadence-saga](https://github.com/anarsultanov/examples/tree/master/cadence-saga)

# How does it work ? 

As all the Cadence's concepts are explained in the [documentation](https://cadenceworkflow.io/docs/concepts/), we will not explain the concepts deeply but focus on how to actually implement the Saga pattern which is not covered in the documenation.

Basically, the workflow has a **single** `@WorkflowMethod` where the saga is described and the activities are the saga's steps.

# How do we make it work ?

Okay it is nicely said but how do we make it work? Let's see an example with the `CreateTournamentSaga`.

We are interesting in these files:
- [CreateTournamentWorkflow](tournament/src/main/java/pt/ulisboa/tecnico/socialsoftware/tournament/workflow/createTournament/CreateTournamentWorkflow.java)
- [CreateTournamentWorkflowImpl](tournament/src/main/java/pt/ulisboa/tecnico/socialsoftware/tournament/workflow/createTournament/CreateTournamentWorkflowImpl.java)
- [CreateTournamentActivities](tournament/src/main/java/pt/ulisboa/tecnico/socialsoftware/tournament/activity/createTournamentActivities/CreateTournamentActivities.java)
- [CreateTournamentActivitiesImpl](tournament/src/main/java/pt/ulisboa/tecnico/socialsoftware/tournament/activity/createTournamentActivities/CreateTournamentActivitiesImpl.java)

We need to create an interface and then its implementation because it is a Cadence's requirement.

## Workflow
```java
public interface CreateTournamentWorkflow {
    
    @WorkflowMethod(executionStartToCloseTimeoutSeconds = 60, taskList = Constants.TOURNAMENT_TASK_LIST)
    Integer createTournament(Integer tournamentId, Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto externalStatementCreationDto, TopicListDto topicListDto);

}
```
### @WorkflowMethod
    
We use the `@WorkflowMethod` annotation to set up the `executionStartToCloseTimeoutSeconds` and the taskList but it could also be done with a WorkflowOptions when creating a workflow in the configuration. You can find further information [here](https://cadenceworkflow.io/docs/java-client/workflow-interface/). In this project, the `executionStartToCloseTimeoutSeconds` is always set to 60 seconds just because it is a good looking value.


### Workflow implementation

In the workflow implementation, we create the Activities needed. Here we needed 5 but showed only 2 for simplication. Then we implement the `@WorkflowMethod`. We instantiate a Saga with 
```java
Saga.Options sagaOptions = new Saga.Options.Builder()
            .setParallelCompensation(false)
            .build();
 Saga saga = new Saga(sagaOptions);
```

and then call the activities methods like this `createTournamentActivities.storeCourseExecution(tournamentId, tournamentCourseExecution);` or create a compensation this way `saga.addCompensation(createTournamentActivities::undoCreate, tournamentId);` in case an exception is catch and we need to compensate.

```java
public class CreateTournamentWorkflowImpl implements CreateTournamentWorkflow {

    private final CreateTournamentActivities createTournamentActivities = Workflow
            .newActivityStub(CreateTournamentActivities.class);

    private final CourseExecutionActivities courseExecutionActivities = Workflow
            .newActivityStub(CourseExecutionActivities.class);

    //code omitted for simplication

    @Override
    public Integer createTournament(Integer tournamentId, Integer creatorId, Integer courseExecutionId,
            ExternalStatementCreationDto externalStatementCreationDto, TopicListDto topicListDto) {
        Saga.Options sagaOptions = new Saga.Options.Builder()
                .setParallelCompensation(false)
                .build();
        Saga saga = new Saga(sagaOptions);
        try {
            // Step 1
            saga.addCompensation(createTournamentActivities::undoCreate, tournamentId);

            // Step 2
            CourseExecutionDto courseExecutionDto = courseExecutionActivities
                .getCourseExecution(courseExecutionId);

            TournamentCourseExecution tournamentCourseExecution = new TournamentCourseExecution(
                    courseExecutionDto.getCourseExecutionId(),
                    courseExecutionDto.getCourseId(),
                    CourseExecutionStatus.valueOf(courseExecutionDto.getStatus().toString()),
                    courseExecutionDto.getAcronym());

            // Step 3
            createTournamentActivities.storeCourseExecution(tournamentId, tournamentCourseExecution);

            //code omitted

        } catch (ActivityException e) {
            saga.compensate();
            throw e;
        }
        return tournamentId;
    }
}
```

## Activities
```java
public interface CreateTournamentActivities {

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TOURNAMENT_TASK_LIST)
    void undoCreate(Integer tournamentId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TOURNAMENT_TASK_LIST)
    void confirmCreate(Integer tournamentId, Integer quizId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TOURNAMENT_TASK_LIST)
    void storeCourseExecution(Integer tournamentId, TournamentCourseExecution tournamentCourseExecution);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TOURNAMENT_TASK_LIST)
    void storeTopics(Integer tournamentId, Set<TournamentTopic> tournamentTopics);

}
```

### Activity method
We use the `@ActivityMethod` annotation to set up the `scheduleToCloseTimeoutSeconds` and the taskList but it could also be done with a ActivityOptions when creating an Activity in the workflow implementation. Or you can set up both `ScheduleToStart` and `StartToClose`. More information on the timeouts [here](https://cadenceworkflow.io/docs/concepts/activities/#timeouts). Same as for the workflow method, `scheduleToCloseTimeoutSeconds` is always set to 60 seconds just because it is a good looking value.

### Activity implementation

In the implementation, we need to have the service and call its methods in the ActivityMethods.

```java
public class CreateTournamentActivitiesImpl implements CreateTournamentActivities {

    private final TournamentProvidedService tournamentService;

    public CreateTournamentActivitiesImpl(TournamentProvidedService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @Override
    public void undoCreate(Integer tournamentId) {
        tournamentService.rejectCreate(tournamentId);
    }

    // other functions omitted for simplication

}
```

## Start the saga

Finally, we can start the workflow method in a `@Service`. Here in the [TournamentProvidedService](tournament/src/main/java/pt/ulisboa/tecnico/socialsoftware/tournament/services/local/TournamentProvidedService.java), in the `createTournament()` function we create the `CreateTournamentWorkflow` described above and start its own method `createTournament()`.

```java
CreateTournamentWorkflow workflow = workflowClient.newWorkflowStub(CreateTournamentWorkflow.class);
WorkflowExecution workflowExecution = WorkflowClient.start(workflow::createTournament, tournament.getId(),
                tournament.getCreator().getId(), executionId, tournament.getExternalStatementCreationDto(),
                new TopicListDto(topicsId));
```

## Architecture of the project or where to look

If you understood the `CreateTournamentSaga` example and are not afraid to dive deeper, you can look up the folders showed in the below Folder Tree.

```
backend  
│
└───common
│   │
│   └───activity
│
└───tournament
│   │
│   └───activity
│   │
│   └───workflow
│   │
│   └───config
│   
└───auth
│   │
│   └───activity
│   │
│   └───workflow
│   │
│   └───config
│
└───tutor
    │
    └───config
    │
    └───answer|execution|question|quiz|user/activity
```

 The Tutor activities are described in the [common package](common/src/main/java/pt/ulisboa/tecnico/socialsoftware/common/activity) so that the Tournament and Auth microservices can use them but they are implemented in the tutor package so that they can use the related services.

In each microservice's config folder, there's a `CadenceConfiguration` file where the connection to the Cadence server is set up and a `CadenceWorkerStarter` file where the workflows and activities are set up.

```java
private void createWorkers() {
        Worker worker = workerFactory.newWorker(CadenceConstants.TOURNAMENT_TASK_LIST, workerOptions);

        worker.registerWorkflowImplementationTypes(CreateTournamentWorkflowImpl.class,
                RemoveTournamentWorkflowImpl.class, UpdateTournamentWorkflowImpl.class);

        worker.registerActivitiesImplementations(new CreateTournamentActivitiesImpl(tournamentProvidedService),
                new RemoveTournamentActivitiesImpl(tournamentProvidedService),
                new UpdateTournamentActivitiesImpl(tournamentProvidedService));
}
```

# Comparison with Eventuate Tram

[Eventuate](https://eventuate.io) is
> an open-source platform for developing microservices, designed to solve distributed data management problems in a microservice architecture

and was the original framework used to implement the Saga Pattern in this project.

So how does Cadence compare to it? 

By comparing with [the original implementation](https://github.com/socialsoftware/quizzes-tutor/tree/microservices/backend), it can be noted that Cadence requires less boilerplate to create a saga. Also, it offers a good UI interface to monitor the sagas.

But in addition to sagas, Eventuate Tram supports events. To work with events with Cadence, it is not straight forward and has not be done in this project by lack of time and documenation. Therefore, you can still find files with Eventuate like this [one](tournament/src/main/java/pt/ulisboa/tecnico/socialsoftware/tournament/subscriptions/TournamentCourseExecutionSubscriptions.java).

Last but not least, Eventuate's documentation is more complete than Cadence's one with a lot of code examples and explanation which is an important thing to consider when learning how to use a piece of technology.

Overall, both these solutions provide a good way to implement the Saga Pattern and could be both chosen for this project if a way to handle events with Cadence is found.

# Temporal: Cadence's fork

If you start working with Cadence, you might found out about [Temporal](https://temporal.io).

In a [StackOverflow question](https://stackoverflow.com/questions/61157400/temporal-workflow-vs-cadence-workflow) on the difference between Cadence and Temporal, the co-founder of the Cadence project explains that Temporal is the fork of Cadence because "the Cadence project has potential which goes far beyond a single company". 

It can be noted that the Java's SDK has been improved for working closer with Spring.

> Temporal implemented the following improvements over Cadence Java client:
> 
>- Workflow and activity annotations to allow activity and workflow implementation objects to implement non-workflow and activity interfaces. This is important to play nice with AOP frameworks like Spring.

Therefore, we could consider to move to Temporal with very little changes to have a better developer experience, a better documentation and a strong support.

# What can be improved

- Switch to Temporal
- Find a way to handle events with Cadence