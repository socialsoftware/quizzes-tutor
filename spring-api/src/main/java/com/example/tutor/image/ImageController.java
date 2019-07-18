package com.example.tutor.image;

import com.example.tutor.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class ImageController {

    private ImageRepository imageRepository;

    ImageController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @GetMapping("/questions/{questionId}/images")
    public ImageDTO getImageByQuestionId(@PathVariable Integer questionId) {
        return new ImageDTO(imageRepository.findById(questionId).orElseThrow(() -> new ResourceNotFoundException("Image not found for question with id " + questionId)));
    }

    @PostMapping("/images")
    public ImageDTO addImage(@Valid @RequestBody ImageDTO image) {
        return new ImageDTO(imageRepository.save(new Image(image)));
    }

    @PutMapping("/questions/{questionId}/images")
    public ImageDTO updateImage(@PathVariable Integer questionId,
                               @Valid @RequestBody ImageDTO imageRequest) {
        imageRepository.findById(questionId)
                .map(image -> {
                    image.setWidth(imageRequest.getWidth());
                    image.setUrl(imageRequest.getUrl());
                    return imageRepository.save(image);
                }).orElseThrow(() -> new ResourceNotFoundException("Image not found for question with id " + questionId));
        return imageRequest;
    }

    @DeleteMapping("/questions/{questionId}/images")
    public ResponseEntity<?> deleteImage(@PathVariable Integer questionId) {
        return imageRepository.findById(questionId)
                .map(image -> {
                    imageRepository.delete(image);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Image not found for question with id " + questionId));
    }
}