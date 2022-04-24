package pt.ulisboa.tecnico.socialsoftware.tutor

class SpockTestIT extends SpockTest {

    def deleteAll() {
        replyRepository.deleteAll();
        discussionRepository.deleteAll();
        answerDetailsRepository.deleteAll();
        questionAnswerRepository.deleteAll();
        quizAnswerRepository.deleteAll();
        quizQuestionRepository.deleteAll();
        quizRepository.deleteAll();
        topicConjunctionRepository.deleteAll();
        assessmentRepository.deleteAll();
        optionRepository.deleteAll();
        questionDetailsRepository.deleteAll();
        questionRepository.deleteAll();
        topicRepository.deleteAll();
    }

}
