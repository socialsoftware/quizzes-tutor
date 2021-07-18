package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class LatexVisitor implements Visitor {
    protected String result = "";
    protected String questionContent;

    @Override
    public void visitQuiz(Quiz quiz) {
        this.result =
                "% Title: " + quiz.getTitle() + "\n" +
                "% Available date: " + quiz.getAvailableDate() + "\n" +
                "% Conclusion date: " + quiz.getConclusionDate() + "\n" +
                "% Type: " + quiz.getType() + "\n" +
                "% Scramble: " + quiz.getScramble() + "\n" +
                "% OneWay: " + quiz.isOneWay() + "\n" +
                "% QrCodeOnly: " + quiz.isQrCodeOnly() + "\n\n";

        this.result = this.result +
                "\\documentclass{sty/docist}\n" +
                "\\usepackage[answers]{sty/exameIST}\n" +
                "\\usepackage{listings}\n" +
                "\\usepackage{subfig}\n\n" +
                "\\input{sty/macros}\n\n" +
                "\\title{{\\Huge Course Name}\\\\\n" +
                "{\\large LEIC/LETI, 3\\ro{} Ano, 2\\ro{} Semestre}\\\\\n" +
                "{\\Large Exame, 8 de julho de 2021}\\\\\n" +
                "{\\Large Duração: XX minutos}\n" +
                "}\n\n" +
                "\\date{}\n" +
                "\\author{}\n\n" +
                "\\showAnswers\n\n" +
                "\\begin{document}\n" +
                "\\input{sty/exame}\n\n" +
                "\\section*{Multiple Choice Questions}\n\n" +
                "\\input{questions}\n\n";

        List<QuizQuestion> quizQuestions = new ArrayList<>(quiz.getQuizQuestions());

        quizQuestions.forEach(quizQuestion -> quizQuestion.accept(this));

        this.result = this.result +
                "\\end{document}";
    }

    @Override
    public void visitQuizQuestion(QuizQuestion quizQuestion) {
        this.result = this.result
                + "\\q" + quizQuestion.getQuestion().getTitle().replaceAll("\\s+", "")
                + convertToAlphabet(quizQuestion.getQuestion().getKey())
                + "\n\n";
    }

    @Override
    public void visitQuestion(Question question) {
        this.result =
                "\\newcommand{\\q"
                + question.getTitle().replaceAll("\\s+", "")
                + convertToAlphabet(question.getKey())
                + "}{\n"
                + "\\begin{ClosedQuestion}\n";

        this.questionContent = question.getContent();

        if (question.getImage() != null) {
            question.getImage().accept(this);
        }

        this.result = this.result + "\t" + this.questionContent + "\n\n";

        question.getQuestionDetails().accept(this);
    }

    @Override
    public void visitQuestionDetails(MultipleChoiceQuestion question) {
        this.result = this.result + "\t\\begin{options}\n";
        question.visitOptions(this);
        this.result = this.result + "\t\\end{options}\n";

        this.result = this.result + "% Answer: " + question.getCorrectAnswerRepresentation() + "\n";

        this.result = this.result + "\\end{ClosedQuestion}\n}\n\n";
    }

    @Override
    public void visitQuestionDetails(CodeFillInQuestion question) {
        this.result += "% Code snippet language: " + question.getLanguage() + "\n\n" + question.getCode() + "\\\\\n\n";

        question.visitFillInSpots(this);

        this.result = this.result + "% Answer: " +
                question.getFillInSpots()
                        .stream()
                        .sorted(Comparator.comparing(CodeFillInSpot::getSequence))
                        .map(spot -> spot.getOptions()
                                .stream()
                                .filter(CodeFillInOption::isCorrect)
                                .map(x -> String.format("{%s}", x.getContent()))
                                .findAny()
                                .orElse("")
                        ).collect(Collectors.joining("; ")) + "\n";

        this.result = this.result + "\\end{ClosedQuestion}\n}\n\n";
    }

    @Override
    public void visitQuestionDetails(CodeOrderQuestion question) {
        this.result += "% Code snippet language: " + question.getLanguage() + "\n\n";
        this.result += "\\begin{enumerate}\n";
        question.visitCodeOrderSlots(this);
        this.result += "\\end{enumerate}\n";

        this.result = this.result + "% Answer: " +
                question.getCodeOrderSlots()
                        .stream()
                        .filter(x -> x.getOrder() != null)
                        .sorted(Comparator.comparing(CodeOrderSlot::getOrder))
                        .map(CodeOrderSlot::getContent
                        ).collect(Collectors.joining(" ")) + "\n";

        this.result = this.result + "\\end{ClosedQuestion}\n}\n\n";
    }

    @Override
    public void visitImage(Image image) {
        String imageString = "\n\t\\begin{center}\n";
        String width = image.getWidth() != null ? "[width=" + image.getWidth() + "cm]" : "[width=8cm]";
        imageString = imageString + "\t\t\\includegraphics" + width + "{" + image.getUrl() + "}\n";
        imageString = imageString + "\t\\end{center}\n\t";

        this.questionContent = this.questionContent.replace("![image][image]", imageString);
    }

    @Override
    public void visitCodeOrderSlot(CodeOrderSlot slot) {
        this.result = this.result + "\\item " + slot.getContent();
    }

    @Override
    public void visitFillInSpot(CodeFillInSpot spot) {
        this.result += String.format("Spot -> {{slot-%d}} \\\\\n", spot.getSequence());
        spot.visitOptions(this);
    }

    @Override
    public void visitFillInOption(CodeFillInOption option) {
        this.result += option.getContent() + "\\\\\n";
    }

    @Override
    public void visitOption(Option option) {
        var optionItem = option.isCorrect() ? "\t\t\\rightOption " : "\t\t\\option ";

        this.result = this.result + optionItem + option.getContent() + "\n";
    }

    private String convertToAlphabet(int number) {
        String alphabet = "ABCDEFGHIJ";
        String numberString = String.valueOf(number);
        StringBuilder bld = new StringBuilder();
        for (int i = 0; i < numberString.length(); i++) {
            int position = Character.getNumericValue(numberString.charAt(i));
            bld.append(alphabet.charAt(position));
        }

        return bld.toString();
    }

}
