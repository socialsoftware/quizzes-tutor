package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgChunkDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgDiscussionRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgDiscussionSuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgGenerateRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgJobDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgMaterialDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelChoiceDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelTestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmSettingsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmSettingsViewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.OllamaModelsDto;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AQG_SERVICE_ERROR;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AQG_SERVICE_UNAVAILABLE;

@Component
public class WebClientAqgClient implements AqgClient {
    private static final Duration TIMEOUT = Duration.ofSeconds(60);
    // The service gives a model up to 60 s to answer a test
    private static final Duration MODEL_TEST_TIMEOUT = Duration.ofSeconds(90);
    // The pieces of a whole book come back in one reply (~600 KB for 300 pages); Spring's default is 256 KB
    private static final int MAX_REPLY_BYTES = 64 * 1024 * 1024;

    // Bodies are read as text and parsed here so the wire format does not depend on which
    // JSON codec the WebClient was built with
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final WebClient client;

    public WebClientAqgClient(@Value("${aqg.service.url:http://aqg-service:8000}") String baseUrl) {
        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(MAX_REPLY_BYTES))
                .build();
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
    public List<AqgChunkDto> getChunks(String materialId) {
        return read(call(() -> client.get().uri("/materials/{materialId}/chunks", materialId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<List<AqgChunkDto>>() {});
    }

    @Override
    public AqgMaterialDto reprocessMaterial(String materialId) {
        return read(call(() -> client.post().uri("/materials/{materialId}/reprocess", materialId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<AqgMaterialDto>() {});
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

    @Override
    public LlmSettingsViewDto getLlmSettings() {
        return read(call(() -> client.get().uri("/settings/llm")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<LlmSettingsViewDto>() {});
    }

    @Override
    public LlmSettingsViewDto saveLlmSettings(LlmSettingsDto settings) {
        String json = write(settings);
        return read(call(() -> client.put().uri("/settings/llm")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<LlmSettingsViewDto>() {});
    }

    @Override
    public LlmModelTestDto testModel(LlmModelChoiceDto model) {
        String json = write(model);
        return read(call(() -> client.post().uri("/settings/llm/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .retrieve().bodyToMono(String.class).block(MODEL_TEST_TIMEOUT)), new TypeReference<LlmModelTestDto>() {});
    }

    @Override
    public OllamaModelsDto getOllamaModels() {
        return read(call(() -> client.get().uri("/settings/llm/ollama")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<OllamaModelsDto>() {});
    }

    @Override
    public OllamaModelsDto pullOllamaModel(LlmModelChoiceDto model) {
        String json = write(model);
        return read(call(() -> client.post().uri("/settings/llm/ollama/pull")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .retrieve().bodyToMono(String.class).block(TIMEOUT)), new TypeReference<OllamaModelsDto>() {});
    }

    private String write(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new TutorException(AQG_SERVICE_ERROR, e.getMessage());
        }
    }

    /** What the service said was wrong: FastAPI puts it in "detail", as text or as a list of field errors. */
    private String reasonOf(String body) {
        try {
            JsonNode detail = objectMapper.readTree(body).get("detail");
            if (detail == null)
                return body;
            if (detail.isTextual())
                return detail.asText();
            List<String> reasons = new ArrayList<>();
            detail.forEach(error -> {
                JsonNode location = error.get("loc");
                String field = location != null && location.size() > 0 ? location.get(location.size() - 1).asText() + ": " : "";
                reasons.add(field + error.path("msg").asText());
            });
            return String.join("; ", reasons);
        } catch (JsonProcessingException e) {
            return body;
        }
    }

    private String call(Supplier<String> request) {
        try {
            return request.get();
        } catch (WebClientResponseException e) {
            throw new TutorException(AQG_SERVICE_ERROR, reasonOf(e.getResponseBodyAsString()));
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
