package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import javax.persistence.Column;

public abstract class CodeQuestion extends Question {
    public enum Language {
        JAVA,
    }

    protected Language language;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
