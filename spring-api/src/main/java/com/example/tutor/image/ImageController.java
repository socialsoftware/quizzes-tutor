package com.example.tutor.image;

import com.example.tutor.ResourceNotFoundException;
import com.example.tutor.question.QuestionRepository;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/questions/{question_id}/images")
    public Image getImageByQuestionId(@PathVariable Integer question_id) {
        return imageRepository.find(question_id);
    }

    @PostMapping("/questions/{question_id}/images")
    public Image addImage(@PathVariable Integer question_id,
                            @Valid @RequestBody Image image) {
        return questionRepository.findById(question_id)
                .map(question -> {
                    image.setQuestion(question);
                    return imageRepository.save(image);
                }).orElseThrow(() -> new ResourceNotFoundException("Question not found with id " + question_id));
    }

    @PutMapping("/questions/{question_id}/images/{imageID}")
    public Image updateImage(@PathVariable Integer question_id,
                               @PathVariable Integer imageID,
                               @Valid @RequestBody Image imageRequest) {
        Image image = findImage(question_id);
        image.setUrl(imageRequest.getUrl());
        image.setWidth(imageRequest.getWidth());
        return imageRepository.save(image);
    }

    @DeleteMapping("/images/{question_id}")
    public ResponseEntity<?> deleteImage(@PathVariable Integer question_id) {

        imageRepository.delete(findImage(question_id));
        return ResponseEntity.ok().build();
    }

    private Image findImage(@PathVariable Integer question_id) {
        if(!questionRepository.existsById(question_id)) {
            throw new ResourceNotFoundException("Question not found with id " + question_id);
        }

        Image image = imageRepository.find(question_id);
        if (image == null) {
            throw new ResourceNotFoundException("Image not found with id " + question_id);
        }
        return image;
    }

    /*@RequestMapping(value = "/img/{question_id}", method = RequestMethod.GET)
    public void getImageAsByteArray(@PathVariable Integer question_id, HttpServletResponse response) throws IOException {
        InputStream in = servletContext.getResourceAsStream(imageRepository.findByquestion_id(question_id).getUrl());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }

    @GetMapping(
            value = "/get-image-with-media-type",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody byte[] getImageWithMediaType() throws IOException {
        InputStream in = getClass()
                .getResourceAsStream("/com/baeldung/produceimage/image.jpg");
        return IOUtils.toByteArray(in);
    }*/
}