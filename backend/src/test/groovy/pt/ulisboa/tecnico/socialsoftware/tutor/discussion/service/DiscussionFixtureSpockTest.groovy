package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ReplyDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

class DiscussionFixtureSpockTest extends SpockTest {
    def student
    def question1
    def question2
    def quizAnswer
    def questionAnswer
    def questionAnswer2
    def courseExecution
    def createdQuiz

    def defineBaseFixture() {
        createExternalCourseAndExecution()

        student = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        userRepository.save(student)
        courseExecution = courseExecutionRepository.findAll().get(0)

        question1 = new Question()
        question1.setCourse(externalCourse)
        question1.setTitle(QUESTION_1_TITLE)
        question1.setContent(QUESTION_1_CONTENT)
        def questionDetails1 = new MultipleChoiceQuestion()
        question1.setQuestionDetails(questionDetails1)


        question2 = new Question()
        question2.setCourse(externalCourse)
        question2.setTitle(QUESTION_2_TITLE)
        question2.setContent(QUESTION_2_CONTENT)
        def questionDetails2 = new MultipleChoiceQuestion()
        question2.setQuestionDetails(questionDetails2)


        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_2_CONTENT)
        optionDto2.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        options.add(optionDto2)

        questionDetails1.setOptions(options)
        questionDetailsRepository.save(questionDetails1)
        questionRepository.save(question1)

        questionDetails2.setOptions(options)
        questionDetailsRepository.save(questionDetails2)
        questionRepository.save(question2)

        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.IN_CLASS.toString())
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)
        createdQuiz = quizRepository.findAll().get(0)

        def quizanswer = new QuizAnswer()
        quizanswer.setUser(student)
        quizanswer.setQuiz(quiz)
        quizanswer.setQuiz(createdQuiz)
        quizAnswerRepository.save(quizanswer)
        quizAnswer = quizAnswerRepository.findAll().get(0)

        def quizquestion = new QuizQuestion(createdQuiz, question1, 0)
        def quizquestion2 = new QuizQuestion(createdQuiz, question2, 1)
        quizQuestionRepository.save(quizquestion)
        quizQuestionRepository.save(quizquestion2)

        def questionanswer = new QuestionAnswer()
        questionanswer.setTimeTaken(1)
        questionanswer.setQuizAnswer(quizAnswer)
        questionanswer.setQuizQuestion(quizquestion)

        def answerDetails = new MultipleChoiceAnswer(questionanswer, optionRepository.findAll().get(0));
        questionanswer.setAnswerDetails(answerDetails);

        answerDetailsRepository.save(answerDetails)
        questionAnswerRepository.save(questionanswer)
        questionAnswer = questionAnswerRepository.findAll().get(0)
        quizquestion.addQuestionAnswer(questionAnswer)
        quizAnswer.addQuestionAnswer(questionAnswer)

        def questionanswer2 = new QuestionAnswer()
        questionanswer2.setTimeTaken(0)
        questionanswer2.setQuizAnswer(quizAnswer)
        questionanswer2.setQuizQuestion(quizquestion)

        def answerDetails2 = new MultipleChoiceAnswer(questionanswer, optionRepository.findAll().get(0));
        questionanswer2.setAnswerDetails(answerDetails2);

        answerDetailsRepository.save(answerDetails2)
        questionAnswerRepository.save(questionanswer2)
        questionAnswer2 = questionAnswerRepository.findAll().get(1)
        quizquestion.addQuestionAnswer(questionAnswer2)
        quizAnswer.addQuestionAnswer(questionAnswer2)

        quiz.addQuizAnswer(quizAnswer)
        quiz.addQuizQuestion(quizQuestionRepository.findAll().get(0))
        quiz.setCourseExecution(courseExecution)

        student.addQuizAnswer(quizAnswer)
    }

    def createDiscussion(QuestionAnswer questionAnswerForDiscussion) {
        def discussionDto = new DiscussionDto()
        discussionDto.setMessage(DISCUSSION_MESSAGE)
        discussionDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        discussionDto.setUsername(student.getUsername())
        discussionDto.setName(student.getName())

        def discussion = new Discussion(questionAnswerForDiscussion, discussionDto)
        discussionRepository.save(discussion)

        return discussion;
    }

    def addReplyToDiscussion(User user, Discussion discussion, Boolean isPublic) {
        def replyDto = new ReplyDto()
        replyDto.setMessage(DISCUSSION_REPLY)
        replyDto.setDate(DateHandler.toISOString(LOCAL_DATE_TODAY))
        replyDto.setPublic(isPublic)

        def reply = new Reply(user, replyDto, discussion)
        replyRepository.save(reply)

        return reply
    }
}
