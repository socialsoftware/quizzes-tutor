package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.GenerationUsageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelChoiceDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelTestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmSettingsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmSettingsViewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.OllamaModelsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.ProviderModelsDto;

/** Administrators only: which models the question generation service uses. */
@RestController
public class LlmSettingsController {
    @Autowired
    private LlmSettingsService llmSettingsService;

    @Autowired
    private QuestionGenerationService questionGenerationService;

    @GetMapping("/admin/llm-settings")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public LlmSettingsViewDto getSettings() {
        return llmSettingsService.getSettings();
    }

    @PutMapping("/admin/llm-settings")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public LlmSettingsViewDto saveSettings(@RequestBody LlmSettingsDto settings) {
        return llmSettingsService.saveSettings(settings);
    }

    /** How many requests and questions each course asked for in the last days. */
    @GetMapping("/admin/generation/usage")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public GenerationUsageDto getUsage(@RequestParam(defaultValue = "10") int days) {
        return questionGenerationService.getUsage(days);
    }

    @PostMapping("/admin/llm-settings/test")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public LlmModelTestDto testModel(@RequestBody LlmModelChoiceDto model) {
        return llmSettingsService.testModel(model);
    }

    @GetMapping("/admin/llm-settings/models")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ProviderModelsDto getProviderModels(@RequestParam String provider,
                                               @RequestParam(defaultValue = "false") boolean refresh) {
        return llmSettingsService.getProviderModels(provider, refresh);
    }

    @GetMapping("/admin/llm-settings/ollama")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public OllamaModelsDto getOllamaModels() {
        return llmSettingsService.getOllamaModels();
    }

    @PostMapping("/admin/llm-settings/ollama/pull")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public OllamaModelsDto pullOllamaModel(@RequestBody LlmModelChoiceDto model) {
        return llmSettingsService.pullOllamaModel(model);
    }
}
