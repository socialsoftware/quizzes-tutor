package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.util.Comparator;
import java.util.stream.Collectors;

public abstract class LatexVisitor implements Visitor {
    protected String result = "";
    protected String questionContent;

    @Override
    public void visitQuiz(Quiz quiz) {
        this.result = this.result +
                "% Title: "  + quiz.getTitle() + "\n" +
                "% Available date: " + quiz.getAvailableDate() + "\n" +
                "% Conclusion date: " + quiz.getConclusionDate() + "\n" +
                "% Type: " + quiz.getType()  + "\n" +
                "% Scramble: " + quiz.getScramble()  + "\n" +
                "% OneWay: " + quiz.isOneWay()  + "\n" +
                "% QrCodeOnly: " + quiz.isQrCodeOnly()  + "\n\n";
    }

    @Override
    public void visitQuizQuestion(QuizQuestion quizQuestion) {
        this.result = this.result + "\\q" + quizQuestion.getQuestion().getTitle().replaceAll("\\s+","") + "\n\n";
    }

    @Override
    public void visitQuestion(Question question) {
        this.result = this.result + "\\newcommand{\\q" + question.getTitle().replaceAll("\\s+","") + "}{\n" + "\\begin{ClosedQuestion}\n";

        this.questionContent = question.getContent();

        if (question.getImage() != null)
            question.getImage().accept(this);

        this.result = this.result + "\t" + this.questionContent + "\n\n";

        for (Option option: question.getOptions().stream().sorted(Comparator.comparing(Option::getSequence)).collect(Collectors.toList())) {
            option.accept(this);
        }
        this.result = this.result + "\\putOptions\n";

        this.result = this.result + "% Answer: " +
            convertSequenceToLetter(question.getOptions().stream().filter(Option::getCorrect).map(Option::getSequence).findAny().orElse(null)) + "\n";

        this.result = this.result + "\\end{ClosedQuestion}\n}\n\n";
    }

    @Override
    public void visitImage(Image image) {
        String imageString = "\n\t\\begin{center}\n";
        imageString = imageString + "\t\t\\includegraphics[width=" + image.getWidth() + "cm]{" + image.getUrl() + "}\n";
		imageString = imageString + "\t\\end{center}\n\t";

        this.questionContent = this.questionContent.replaceAll("!\\[image\\]\\[image\\]", imageString);
    }

    @Override
    public void visitOption(Option option) {
        this.result = this.result + "\t\\option" + convertSequenceToLetter(option.getSequence()) + "{" + option.getContent() + "}\n";
    }

}
