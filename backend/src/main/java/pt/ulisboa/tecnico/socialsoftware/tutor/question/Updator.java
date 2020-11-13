package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;

public interface Updator {
    default void update(MultipleChoiceQuestion question) {}
}
