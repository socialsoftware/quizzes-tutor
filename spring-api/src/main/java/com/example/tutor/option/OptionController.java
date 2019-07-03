package com.example.tutor.option;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.question.QuestionRepository;

import javax.validation.Valid;
import java.util.List;

@RestController
public class OptionController {

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/questions/{question_id}/options")
    public List<Option> getOptionsByquestionId(@PathVariable Integer question_id) {
        return optionRepository.findByquestion_id(question_id);
    }

    @PostMapping("/questions/{question_id}/options")
    public Option addOption(@PathVariable Integer question_id,
                            @Valid @RequestBody Option option) {
        return questionRepository.findById(question_id)
                .map(question -> {
                    option.setQuestionId(question.getId());
                    return optionRepository.save(option);
                }).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + question_id));
    }

    @PutMapping("/questions/{question_id}/options/{optionID}")
    public Option updateOption(@PathVariable Integer question_id,
                               @PathVariable Integer optionID,
                               @Valid @RequestBody Option optionRequest) {
        Option option = findOption(question_id, optionID);
        option.setContent(optionRequest.getContent());
        return optionRepository.save(option);
    }

    @DeleteMapping("/questions/{question_id}/options/{optionID}")
    public ResponseEntity<?> deleteOption(@PathVariable Integer question_id,
                                          @PathVariable Integer optionID) {
        Option option = findOption(question_id, optionID);
        optionRepository.delete(option);
        return ResponseEntity.ok().build();
    }

    private Option findOption(@PathVariable Integer question_id, @PathVariable Integer optionID) {
        if(!questionRepository.existsById(question_id)) {
            throw new ResourceNotFoundException("Question not found with id " + question_id);
        }

        Option option = optionRepository.findById(question_id, optionID);
        if (option == null) {
            throw new ResourceNotFoundException("Option not found with id " + optionID);
        }
        return option;
    }
}