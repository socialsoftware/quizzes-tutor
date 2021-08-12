package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CSVQuizExportVisitor implements Visitor {
    private String[] line;
    private int column;
    private final List<String[]> table = new ArrayList<>();

    public String export(Quiz quiz, List<QuestionAnswerItem> questionAnswerItems) {
        int numberOfQuestions = quiz.getQuizQuestionsNumber();
        List<QuizQuestion> quizQuestionsList = new ArrayList<>(quiz.getQuizQuestions());
        int lineSize = numberOfQuestions + 5;
        Map<Integer, QuizQuestion> quizQuestions = quizQuestionsList.stream()
                .collect(Collectors.toMap(QuizQuestion::getId, Function.identity()));

        addHeader(quizQuestionsList, lineSize);

        addStudentAnswer(quiz, lineSize);

        addKey(quizQuestionsList, lineSize);

        addFraudMessage(lineSize);

        addLogOfQuestionAnswers(questionAnswerItems, lineSize, quizQuestions);

        return table.stream().map(this::convertToCSV).collect(Collectors.joining("\n"));
    }

    private void addLogOfQuestionAnswers(List<QuestionAnswerItem> questionAnswerItems, int lineSize, Map<Integer, QuizQuestion> quizQuestions) {
        line = new String[lineSize];
        Arrays.fill(line, "");
        table.add(line);
        line = new String[lineSize];
        Arrays.fill(line, "");
        table.add(line);

        line = new String[lineSize];
        Arrays.fill(line, "");
        line[0] = "Username";
        line[1] = "Question";
        line[2] = "Option";
        line[3] = "Answer Date";
        line[4] = "Time To Submission";
        line[5] = "Time Taken";
        table.add(line);

        Comparator<QuestionAnswerItem> comparator = Comparator.comparing(QuestionAnswerItem::getUsername)
                .thenComparing(Comparator.comparing(QuestionAnswerItem::getTimeToSubmission).reversed());
        questionAnswerItems.sort(comparator);

        for (QuestionAnswerItem questionAnswerItem : questionAnswerItems) {
            line = new String[lineSize];
            Arrays.fill(line, "");
            line[0] = questionAnswerItem.getUsername();
            line[1] = String.valueOf(quizQuestions.get(questionAnswerItem.getQuizQuestionId()).getSequence()+1);
            line[2] = questionAnswerItem.getAnswerRepresentation(quizQuestions.get(questionAnswerItem.getQuizQuestionId()).getQuestion().getQuestionDetails());
            line[3] = DateHandler.toHumanReadableString(questionAnswerItem.getAnswerDate());
            line[4] = questionAnswerItem.getTimeToSubmission() != null ? convertMiliseconds(questionAnswerItem.getTimeToSubmission()) : "";
            line[5] = convertMiliseconds(questionAnswerItem.getTimeTaken());
            table.add(line);
        }
    }

    private void addFraudMessage(int lineSize) {
        line = new String[lineSize];
        Arrays.fill(line, "");
        table.add(line);
        line = new String[lineSize];
        Arrays.fill(line, "");
        line[2] = "(*) Fraud Suspicion: students with (*) may not follow predefined order of OneWay quiz please check log";
        table.add(line);
    }

    private void addKey(List<QuizQuestion> quizQuestionsList, int lineSize) {
        line = new String[lineSize];
        Arrays.fill(line, "");
        line[4] = "KEYS";
        column = 5;
        quizQuestionsList.stream()
                .forEach(quizQuestion -> quizQuestion.accept(this));
        table.add(line);
    }

    private void addStudentAnswer(Quiz quiz, int lineSize) {
        for (QuizAnswer quizAnswer : quiz.getQuizAnswers()) {
            line = new String[lineSize];
            Arrays.fill(line, "");
            column = 0;
            quizAnswer.getStudent().accept(this);

            quizAnswer.accept(this);

            quizAnswer.getQuestionAnswers().stream()
                    .sorted(Comparator.comparing(questionAnswer -> questionAnswer.getQuizQuestion().getSequence()))
                    .collect(Collectors.toList())
                    .forEach(questionAnswer -> questionAnswer.accept(this));

            table.add(line);
        }
    }

    private void addHeader(List<QuizQuestion> quizQuestionsList, int lineSize) {
        line = new String[lineSize];
        Arrays.fill(line, "");
        line[0] = "Username";
        line[1] = "Name";
        line[2] = "Start";
        line[3] = "Delivered";
        line[4] = "Delay in Seconds";
        column = 4;

        quizQuestionsList.stream()
                .forEach(quizQuestion ->
                    line[++column] = String.valueOf(quizQuestion.getSequence()+1)
                );

        table.add(line);
    }

    @Override
    public void visitUser(User user) {
        line[column++] = user.getUsername() != null ? user.getUsername() : "";
        line[column++] = user.getName() != null ? user.getName() : "";
    }

    @Override
    public void visitQuizAnswer(QuizAnswer quizAnswer) {
        line[column++] = (quizAnswer.isFraud() ? "(*) " : "") + (quizAnswer.getCreationDate() != null ? DateHandler.toHumanReadableString(quizAnswer.getCreationDate()) : "");
        line[column++] = quizAnswer.getAnswerDate() != null ? DateHandler.toHumanReadableString(quizAnswer.getAnswerDate()) : "";
        if (quizAnswer.getAnswerDate() != null &&
                quizAnswer.getQuiz().getConclusionDate() != null &&
                quizAnswer.getAnswerDate().isAfter(quizAnswer.getQuiz().getConclusionDate())) {
            Duration duration = Duration.between(quizAnswer.getAnswerDate(), quizAnswer.getQuiz().getConclusionDate());
            line[column++] = String.valueOf(Math.abs(duration.toSeconds()));
        } else
            line[column++] = "";
    }

    @Override
    public void visitQuestionAnswer(QuestionAnswer questionAnswer) {
        if (questionAnswer.getAnswerDetails() != null) {
            questionAnswer.getAnswerDetails().accept(this);
        }
    }

    @Override
    public void visitAnswerDetails(MultipleChoiceAnswer answer) {
        line[column++] = answer.getAnswerRepresentation();
    }

    @Override
    public void visitAnswerDetails(CodeFillInAnswer answer) {
        line[column++] = answer.getAnswerRepresentation();
    }

    @Override
    public void visitAnswerDetails(CodeOrderAnswer answer){
        line[column++] = answer.getAnswerRepresentation();
    }

    @Override
    public void visitQuizQuestion(QuizQuestion quizQuestion) {
        quizQuestion.getQuestion().accept(this);
    }

    @Override
    public void visitQuestion(Question question) {
        question.getQuestionDetails().accept(this);
    }

    @Override
    public void visitQuestionDetails(MultipleChoiceQuestion question) {
        line[column++] = question.getCorrectAnswerRepresentation();
    }

    @Override
    public void visitQuestionDetails(CodeFillInQuestion question) {
        line[column++] = question.getCorrectAnswerRepresentation();
    }

    @Override
    public void visitQuestionDetails(CodeOrderQuestion question) {
        line[column++] = question.getCorrectAnswerRepresentation();
    }

    private String convertToCSV(String[] data) {
        return String.join(",", data);
    }

    private String convertMiliseconds(int millis) {
       return  String.format("%02d:%02d:%02d",
               TimeUnit.MILLISECONDS.toHours(millis),
               TimeUnit.MILLISECONDS.toMinutes(millis)  -
                       TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }


}
