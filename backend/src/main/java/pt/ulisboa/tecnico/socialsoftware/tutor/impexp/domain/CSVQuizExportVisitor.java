package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVQuizExportVisitor implements Visitor {
    private int numberOfQuestions;
    private String[] line;
    private int column;
    private List<String[]> table = new ArrayList<>();

    public String export(Quiz quiz) throws IOException {
        numberOfQuestions = quiz.getQuizQuestions().size();

        line = new String[numberOfQuestions + 4];
        Arrays.fill(line, "");
        line[0] = "Username";
        line[1] = "Name";
        line[2] = "Start";
        line[3] = "Finish";
        table.add(line);

        for (QuizAnswer quizAnswer : quiz.getQuizAnswers()) {
            line = new String[numberOfQuestions + 4];
            Arrays.fill(line, "");
            column = 0;
            quizAnswer.getUser().accept(this);

            quizAnswer.accept(this);

            quizAnswer.getQuestionAnswers().stream()
                    .sorted(Comparator.comparing(questionAnswer -> questionAnswer.getQuizQuestion().getSequence()))
                    .collect(Collectors.toList())
                    .forEach(questionAnswer -> {
                        questionAnswer.accept(this);
                    });

            table.add(line);
        }

        line = new String[numberOfQuestions + 4];
        Arrays.fill(line, "");
        line[3] = "KEYS";
        column = 4;
        quiz.getQuizQuestions().stream()
                .sorted(Comparator.comparing(QuizQuestion::getSequence))
                .forEach(quizQuestion -> {
                    quizQuestion.accept(this);
                });
        table.add(line);

        String result = table.stream().map(this::convertToCSV).collect(Collectors.joining("\n"));

        System.out.println(result);
        return result;
    }

    @Override
    public void visitUser(User user) {
        line[column++] = user.getUsername() != null ? user.getUsername() : "";
        line[column++] = user.getName() != null ? user.getName() : "";
    }

    @Override
    public void visitQuizAnswer(QuizAnswer quizAnswer) {
        DateTimeFormatter formatter = Course.formatter;

        line[column++] = quizAnswer.getCreationDate() != null ? quizAnswer.getCreationDate().format(formatter) : "";
        line[column++] = quizAnswer.getAnswerDate() != null ? quizAnswer.getAnswerDate().format(formatter) : "";
    }

    @Override
    public void visitQuestionAnswer(QuestionAnswer questionAnswer) {
        line[column++] = questionAnswer.getOption() != null ? convertSequenceToLetter(questionAnswer.getOption().getSequence()) : "X";
    }

    @Override
    public void visitQuizQuestion(QuizQuestion quizQuestion) {
        line[column++] = quizQuestion.getQuestion().getOptions().stream()
                .filter(Option::getCorrect)
                .findAny()
                .map(option -> convertSequenceToLetter(option.getSequence())).orElse("");
    }

    private String convertToCSV(String[] data) {
        return Stream.of(data)
                .collect(Collectors.joining(","));
    }


}
