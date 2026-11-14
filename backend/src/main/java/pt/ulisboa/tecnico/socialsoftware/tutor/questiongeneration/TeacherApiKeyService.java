package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.TeacherApiKey;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelChoiceDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelTestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmSettingsViewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.TeacherApiKeysDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.repository.TeacherApiKeyRepository;

import java.util.Comparator;
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
    /** The owner of the keys of the whole site, which the administrator gives: no user has this id. */
    public static final Integer SITE = 0;
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 500;
    private static final String OLLAMA = "ollama";

    @Autowired
    private TeacherApiKeyRepository repository;

    @Autowired
    private ApiKeyCipher cipher;

    @Autowired
    private AqgClient aqgClient;

    /** The providers a key can be given for (every one the generation service can use, except Ollama, which has none). */
    public List<TeacherApiKeysDto.Provider> keyProviders() {
        LlmSettingsViewDto view = aqgClient.getLlmSettings();
        Map<String, String> names = view.providerNames() == null ? Map.of() : view.providerNames();
        return (view.providers() == null ? List.<String>of() : view.providers()).stream()
                .filter(provider -> !OLLAMA.equals(provider))
                .map(provider -> new TeacherApiKeysDto.Provider(provider, names.getOrDefault(provider, provider)))
                .collect(Collectors.toList());
    }

    /** Whether a key can be given for the provider. */
    public boolean isKeyProvider(String provider) {
        return provider != null && keyProviders().stream().anyMatch(known -> known.id().equals(provider));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherApiKeysDto getKeys(Integer ownerId) {
        List<TeacherApiKeysDto.Provider> providers = keyProviders();
        Map<String, String> names = providers.stream().collect(Collectors.toMap(TeacherApiKeysDto.Provider::id, TeacherApiKeysDto.Provider::name));
        List<TeacherApiKeysDto.Key> keys = repository.findByUserId(ownerId).stream()
                .sorted(Comparator.comparing(key -> names.getOrDefault(key.getProvider(), key.getProvider()).toLowerCase()))
                .map(key -> new TeacherApiKeysDto.Key(key.getProvider(), names.getOrDefault(key.getProvider(), key.getProvider()),
                        key.getLastFour(), key.getUpdatedAt() == null ? null : key.getUpdatedAt().toString()))
                .collect(Collectors.toList());
        return new TeacherApiKeysDto(cipher.isConfigured(), keys, providers);
    }

    public TeacherApiKeysDto saveKey(Integer ownerId, String provider, String rawKey) {
        return saveKey(ownerId, provider, rawKey, null);
    }

    /**
     * Checks the key with the provider and, if it is accepted, keeps it encrypted in place of the previous one.
     * `model` is the model to check it with; without one, the provider's usual one.
     */
    public TeacherApiKeysDto saveKey(Integer ownerId, String provider, String rawKey, String model) {
        if (!cipher.isConfigured())
            throw new TutorException(TEACHER_API_KEYS_NOT_CONFIGURED);
        checkProvider(provider);
        String key = rawKey == null ? "" : rawKey.trim();
        if (key.length() < MIN_LENGTH || key.length() > MAX_LENGTH || !key.chars().allMatch(c -> c > 32 && c < 127))
            throw new TutorException(TEACHER_API_KEY_INVALID);
        String checkWith = model == null || model.isBlank() ? aqgClient.getLlmSettings().defaultModels().get(provider) : model.trim();
        if (checkWith == null || !checkWith.matches("[A-Za-z0-9._:/@+\\-]{1,200}"))
            throw new TutorException(TEACHER_API_KEY_MODEL);

        // Outside the transaction: a call to a provider can take a minute
        LlmModelTestDto test = aqgClient.testModel(new LlmModelChoiceDto(provider, checkWith), key);
        if (!Boolean.TRUE.equals(test.ok()))
            throw new TutorException(TEACHER_API_KEY_REJECTED, shorten(test.error()));

        store(ownerId, provider, key);
        return getKeys(ownerId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void store(Integer ownerId, String provider, String key) {
        TeacherApiKey saved = repository.findByUserIdAndProvider(ownerId, provider).orElseGet(() -> new TeacherApiKey(ownerId, provider));
        saved.replace(cipher.encrypt(ownerId, provider, key), key.substring(key.length() - 4));
        repository.save(saved);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherApiKeysDto removeKey(Integer ownerId, String provider) {
        if (provider == null || provider.isBlank())
            throw new TutorException(TEACHER_API_KEY_PROVIDER, "");
        repository.findByUserIdAndProvider(ownerId, provider).ifPresent(repository::delete);
        return getKeys(ownerId);
    }

    /**
     * The keys that go with one request to the generation service, by provider, decrypted: the site's, and
     * the teacher's own over them for the providers they gave one for. Empty when there are none, or the
     * server cannot keep keys.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, String> keysOf(Integer userId) {
        Map<String, String> keys = new LinkedHashMap<>(keysOwnedBy(SITE));
        keys.putAll(keysOwnedBy(userId));
        return keys;
    }

    /** The keys the administrator gave for the whole site, decrypted. */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, String> siteKeys() {
        return keysOwnedBy(SITE);
    }

    private Map<String, String> keysOwnedBy(Integer ownerId) {
        Map<String, String> keys = new LinkedHashMap<>();
        if (ownerId == null || !cipher.isConfigured())
            return keys;
        for (TeacherApiKey saved : repository.findByUserId(ownerId))
            keys.put(saved.getProvider(), cipher.decrypt(ownerId, saved.getProvider(), saved.getEncryptedKey()));
        return keys;
    }

    /** The providers the site has a key for, without reading any key. */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<String> siteProviders() {
        return providersWithKey(SITE);
    }

    /** The providers the teacher has their own key for, without reading any key. */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<String> providersWithKey(Integer userId) {
        if (userId == null || !cipher.isConfigured())
            return List.of();
        return repository.findByUserId(userId).stream().map(TeacherApiKey::getProvider).collect(Collectors.toList());
    }

    private void checkProvider(String provider) {
        if (!isKeyProvider(provider))
            throw new TutorException(TEACHER_API_KEY_PROVIDER, provider == null ? "" : provider);
    }

    private String shorten(String text) {
        if (text == null)
            return "no answer";
        return text.length() > 200 ? text.substring(0, 200) : text;
    }
}
