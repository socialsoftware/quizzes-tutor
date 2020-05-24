package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;

import javax.persistence.Column;

public abstract class CodeQuestion extends Question {
    public enum Language {
        JAVA,
    }

    private Language language;

    public CodeQuestion() {

    }

    // todo add code question dto.
    public CodeQuestion(Course course, QuestionDto questionDto) {
        super(course, questionDto);
        //setOptions(questionDto.getOptions());
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
