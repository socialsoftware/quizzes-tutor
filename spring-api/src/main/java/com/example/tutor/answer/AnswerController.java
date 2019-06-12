package com.example.tutor.answer;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.question.Question;
import com.example.tutor.question.QuestionDTO;
import com.example.tutor.question.QuestionRepository;
import com.example.tutor.quiz.QuizRepository;
import com.example.tutor.user.User;
import com.example.tutor.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
public class AnswerController {

    private AnswerRepository answerRepository;
    private QuizRepository quizRepository;
    private QuestionRepository questionRepository;
    private UserRepository userRepository;

    AnswerController(AnswerRepository answerRepository, QuizRepository quizRepository, QuestionRepository questionRepository, UserRepository userRepository) {
        this.answerRepository = answerRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/answers")
    public Page<Answer> getAnswers(Pageable pageable) {
        return answerRepository.findAll(pageable);
    }

    @GetMapping("/answers/{answer_id}")
    public AnswerDTO getAnswer(@PathVariable Integer answer_id) {
        return new AnswerDTO(answerRepository.findById(answer_id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id " + answer_id)));
    }

    @PostMapping("/answers")
    public Answer createAnswer(@Valid @RequestBody Answer answer) {
        return answerRepository.save(answer);
    }

    @PutMapping("/answers/{answer_id}")
    public AnswerDTO updateAnswer(@PathVariable Integer answer_id,
                                      @Valid @RequestBody Answer answerRequest) {

        return new AnswerDTO(answerRepository.findById(answer_id)
                .map(answer -> {
                    answer.setOption(answerRequest.getOption());
                    answer.setQuiz_id(answerRequest.getQuiz_id());
                    answer.setTime_taken(answerRequest.getTime_taken());
                    return answerRepository.save(answer);
                }).orElseThrow(() -> new ResourceNotFoundException("Answer not found with id " + answer_id)));
    }


    @DeleteMapping("/answers/{answer_id}")
    public ResponseEntity<?> deleteAnswer(@PathVariable Integer answer_id) {
        return answerRepository.findById(answer_id)
                .map(answer -> {
                    answerRepository.delete(answer);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Answer not found with id " + answer_id));
    }

    @PostMapping("/quiz-answers")
    public Result createAnswer(@Valid @RequestBody Answers answers) {
        quizRepository.findById(answers.getQuiz_id())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + answers.getQuiz_id()));
        User s = this.userRepository.findById(answers.getUser_id()).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + answers.getUser_id()));;


        return new Result(answers.getAnswers().stream().map(answer -> {
            Question q = this.questionRepository.findById(answer.getQuestion_id()).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + answer.getQuestion_id()));;


            answerRepository.save(new Answer(answer, s, q));
                return new QuestionDTO(questionRepository.findById(answer.getQuestion_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id " + answers.getQuiz_id())), true);
        }).collect(Collectors.toList()));
    }
}