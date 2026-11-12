package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.TeacherApiKey;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelChoiceDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelTestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.TeacherApiKeysDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.repository.TeacherApiKeyRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

/**
 * The API keys teachers give to use their own account of a model provider. A key is checked with
 * the provider before it is kept, kept encrypted, never sent back to anyone, and only sent to the
 * generation service with the requests of the teacher who owns it.
 */
@Service
public class TeacherApiKeyService {
    /** The providers with a key to give: Ollama has none. */
    static final List<String> PROVIDERS = List.of("nvidia_nim", "openai", "anthropic");
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 500;

    @Autowired
    private TeacherApiKeyRepository repository;

    @Autowired
    private ApiKeyCipher cipher;

    @Autowired
    private AqgClient aqgClient;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherApiKeysDto getKeys(Integer userId) {
        Map<String, TeacherApiKey> saved = repository.findByUserId(userId).stream()
                .collect(Collectors.toMap(TeacherApiKey::getProvider, key -> key, (a, b) -> a));
        List<TeacherApiKeysDto.Key> keys = PROVIDERS.stream().map(provider -> {
            TeacherApiKey key = saved.get(provider);
            return new TeacherApiKeysDto.Key(provider, key != null, key == null ? null : key.getLastFour(),
                    key == null || key.getUpdatedAt() == null ? null : key.getUpdatedAt().toString());
        }).collect(Collectors.toList());
        return new TeacherApiKeysDto(cipher.isConfigured(), keys);
    }

    /** Checks the key with the provider and, if it is accepted, keeps it encrypted in place of the previous one. */
    public TeacherApiKeysDto saveKey(Integer userId, String provider, String rawKey) {
        if (!cipher.isConfigured())
            throw new TutorException(TEACHER_API_KEYS_NOT_CONFIGURED);
        checkProvider(provider);
        String key = rawKey == null ? "" : rawKey.trim();
        if (key.length() < MIN_LENGTH || key.length() > MAX_LENGTH || !key.chars().allMatch(c -> c > 32 && c < 127))
            throw new TutorException(TEACHER_API_KEY_INVALID);

        // Outside the transaction: a call to a provider can take a minute
        String model = aqgClient.getLlmSettings().defaultModels().get(provider);
        LlmModelTestDto test = aqgClient.testModel(new LlmModelChoiceDto(provider, model), key);
        if (!Boolean.TRUE.equals(test.ok()))
            throw new TutorException(TEACHER_API_KEY_REJECTED, shorten(test.error()));

        store(userId, provider, key);
        return getKeys(userId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void store(Integer userId, String provider, String key) {
        TeacherApiKey saved = repository.findByUserIdAndProvider(userId, provider).orElseGet(() -> new TeacherApiKey(userId, provider));
        saved.replace(cipher.encrypt(userId, provider, key), key.substring(key.length() - 4));
        repository.save(saved);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherApiKeysDto removeKey(Integer userId, String provider) {
        checkProvider(provider);
        repository.findByUserIdAndProvider(userId, provider).ifPresent(repository::delete);
        return getKeys(userId);
    }

    /**
     * The teacher's keys by provider, decrypted, to go with one request to the generation service.
     * Empty when the teacher has none, or the server cannot keep keys.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, String> keysOf(Integer userId) {
        Map<String, String> keys = new LinkedHashMap<>();
        if (userId == null || !cipher.isConfigured())
            return keys;
        for (TeacherApiKey saved : repository.findByUserId(userId))
            keys.put(saved.getProvider(), cipher.decrypt(userId, saved.getProvider(), saved.getEncryptedKey()));
        return keys;
    }

    /** The providers the teacher has a key for, without reading any key. */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<String> providersWithKey(Integer userId) {
        if (userId == null || !cipher.isConfigured())
            return List.of();
        return repository.findByUserId(userId).stream().map(TeacherApiKey::getProvider).collect(Collectors.toList());
    }

    private void checkProvider(String provider) {
        if (!PROVIDERS.contains(provider))
            throw new TutorException(TEACHER_API_KEY_PROVIDER, provider == null ? "" : provider);
    }

    private String shorten(String text) {
        if (text == null)
            return "no answer";
        return text.length() > 200 ? text.substring(0, 200) : text;
    }
}
