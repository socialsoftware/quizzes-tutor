package com.example.tutor.option;

import com.example.tutor.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OptionController {

    private OptionRepository optionRepository;

    OptionController(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @GetMapping("/questions/{questionId}/options")
    public List<OptionDTO> getOptionsByquestionId(@PathVariable Integer questionId) {
        return optionRepository.findAllById(questionId).stream().map(OptionDTO::new).collect(Collectors.toList());
    }

    @PostMapping("/options")
    public OptionDTO addOption(@PathVariable Integer questionId,
                            @Valid @RequestBody OptionDTO option) {
            optionRepository.save(new Option(option));
            return option;
        }


    @PutMapping("/questions/{questionId}/options/{optionNumber}")
    public OptionDTO updateOption(@PathVariable Integer questionId,
                               @PathVariable Integer optionNumber,
                               @Valid @RequestBody OptionDTO optionRequest) {
        Option option = optionRepository.findById(questionId, optionNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Option " + optionNumber + " not found for question with id " + questionId));
        option.setCorrect(optionRequest.getCorrect());
        option.setOption(optionRequest.getOption());
        option.setContent(optionRequest.getContent());
        optionRepository.save(option);
        return optionRequest;
    }

    @DeleteMapping("/questions/{questionId}/options/{optionNumber}")
    public ResponseEntity<?> deleteOption(@PathVariable Integer questionId,
                                          @PathVariable Integer optionNumber) {
        Option option = optionRepository.findById(questionId, optionNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Option " + optionNumber + " not found for question with id " + questionId));

        optionRepository.delete(option);
        return ResponseEntity.ok().build();
    }
}