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

    @GetMapping("/questions/{questionID}/options")
    public List<Option> getOptionsByQuestionID(@PathVariable Integer questionID) {
        return optionRepository.findByQuestionID(questionID);
    }

    @PostMapping("/questions/{questionID}/options")
    public Option addOption(@PathVariable Integer questionID,
                            @Valid @RequestBody Option option) {
        return questionRepository.findById(questionID)
                .map(question -> {
                    option.setQuestionID(question.getId());
                    return optionRepository.save(option);
                }).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + questionID));
    }

    @PutMapping("/questions/{questionID}/options/{optionID}")
    public Option updateOption(@PathVariable Integer questionID,
                               @PathVariable Integer optionID,
                               @Valid @RequestBody Option optionRequest) {
        Option option = findOption(questionID, optionID);
        option.setContent(optionRequest.getContent());
        return optionRepository.save(option);
    }

    @DeleteMapping("/questions/{questionID}/options/{optionID}")
    public ResponseEntity<?> deleteOption(@PathVariable Integer questionID,
                                          @PathVariable Integer optionID) {
        Option option = findOption(questionID, optionID);
        optionRepository.delete(option);
        return ResponseEntity.ok().build();
    }

    private Option findOption(@PathVariable Integer questionID, @PathVariable Integer optionID) {
        if(!questionRepository.existsById(questionID)) {
            throw new ResourceNotFoundException("Question not found with id " + questionID);
        }

        Option option = optionRepository.findById(questionID, optionID);
        if (option == null) {
            throw new ResourceNotFoundException("Option not found with id " + optionID);
        }
        return option;
    }
}