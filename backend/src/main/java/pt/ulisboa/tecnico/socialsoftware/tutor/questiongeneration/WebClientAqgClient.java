package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgDiscussionRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgDiscussionSuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgGenerateRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgJobDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgOutlineEditDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgOutlineEditResultDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgOutlineNodeDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgParagraphDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgMaterialDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgSectionDto;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AQG_SERVICE_ERROR;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AQG_SERVICE_UNAVAILABLE;

@Component
public class WebClientAqgClient implements AqgClient {
    private static final Duration TIMEOUT = Duration.ofSeconds(60);

    // Bodies are read as text and parsed here so the wire format does not depend on which
    // JSON codec the WebClient was built with
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final WebClient client;

    public WebClientAqgClient(@Value("${aqg.service.url:http://aqg-service:8000}") String baseUrl) {
        this.client = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public AqgMaterialDto uploadMaterial(int courseId, String filename, byte[] content) {
        MultipartBodyBuilder body = new MultipartBodyBuilder();
        body.part("course_id", String.valueOf(courseId));
        body.part("file", new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return filename;
            }
        });

        return read(call(() -> client.post().uri("/materials")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body.build()))
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<AqgMaterialDto>() {});
    }

    @Override
    public List<AqgMaterialDto> listMaterials(int courseId) {
        return read(call(() -> client.get().uri("/courses/{courseId}/materials", courseId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<List<AqgMaterialDto>>() {});
    }

    @Override
    public List<AqgSectionDto> getSections(String materialId) {
        return read(call(() -> client.get().uri("/materials/{materialId}/sections", materialId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<List<AqgSectionDto>>() {});
    }

    @Override
    public AqgMaterialDto reprocessMaterial(String materialId) {
        return read(call(() -> client.post().uri("/materials/{materialId}/reprocess", materialId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<AqgMaterialDto>() {});
    }

    @Override
    public List<AqgOutlineNodeDto> getOutline(String materialId) {
        return read(call(() -> client.get().uri("/materials/{materialId}/outline", materialId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<List<AqgOutlineNodeDto>>() {});
    }

    @Override
    public List<AqgParagraphDto> getSectionText(String materialId, String path) {
        return read(call(() -> client.get()
                .uri(uri -> uri.path("/materials/{materialId}/section-text").queryParam("path", "{path}").build(materialId, path))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<List<AqgParagraphDto>>() {});
    }

    @Override
    public AqgOutlineEditResultDto editOutline(String materialId, AqgOutlineEditDto edit) {
        String json;
        try {
            json = objectMapper.writeValueAsString(edit);
        } catch (JsonProcessingException e) {
            throw new TutorException(AQG_SERVICE_ERROR, e.getMessage());
        }

        return read(call(() -> client.post().uri("/materials/{materialId}/outline/edit", materialId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<AqgOutlineEditResultDto>() {});
    }

    @Override
    public AqgDiscussionSuggestionDto suggestReply(AqgDiscussionRequest request) {
        String json;
        try {
            json = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            // the message could hold what the student wrote
            throw new TutorException(AQG_SERVICE_ERROR, "unreadable request");
        }

        return read(call(() -> client.post().uri("/discussion/suggest")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<AqgDiscussionSuggestionDto>() {});
    }

    @Override
    public AqgJobDto generate(AqgGenerateRequest request) {
        String json;
        try {
            json = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new TutorException(AQG_SERVICE_ERROR, e.getMessage());
        }

        return read(call(() -> client.post().uri("/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<AqgJobDto>() {});
    }

    @Override
    public AqgJobDto getJob(String aqgJobId) {
        return read(call(() -> client.get().uri("/jobs/{jobId}", aqgJobId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<AqgJobDto>() {});
    }

    private String call(Supplier<String> request) {
        try {
            return request.get();
        } catch (WebClientResponseException e) {
            throw new TutorException(AQG_SERVICE_ERROR, e.getResponseBodyAsString());
        } catch (WebClientRequestException | IllegalStateException e) {
            // IllegalStateException is what block(timeout) throws when the service does not answer in time
            throw new TutorException(AQG_SERVICE_UNAVAILABLE);
        }
    }

    private <T> T read(String json, TypeReference<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new TutorException(AQG_SERVICE_ERROR, "unreadable reply");
        }
    }
}
