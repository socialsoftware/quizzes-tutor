package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.time.LocalDateTime;

/**
 * The API key a teacher gave for their own question generation requests, for one provider. Only the
 * encrypted key is kept, and the last four characters to tell which key it is.
 */
@Entity
@Table(name = "teacher_api_keys", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "provider"}))
public class TeacherApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false, length = 32)
    private String provider;

    @Column(name = "encrypted_key", nullable = false, length = 2000)
    private String encryptedKey;

    @Column(name = "last_four", length = 4)
    private String lastFour;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = DateHandler.now();

    public TeacherApiKey() {
    }

    public TeacherApiKey(Integer userId, String provider) {
        this.userId = userId;
        this.provider = provider;
    }

    public void replace(String encryptedKey, String lastFour) {
        this.encryptedKey = encryptedKey;
        this.lastFour = lastFour;
        this.updatedAt = DateHandler.now();
    }

    public Integer getId() { return id; }

    public Integer getUserId() { return userId; }

    public String getProvider() { return provider; }

    public String getEncryptedKey() { return encryptedKey; }

    public String getLastFour() { return lastFour; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // never printed: not even the encrypted form belongs in a log
    @Override
    public String toString() {
        return "TeacherApiKey{id=" + id + ", provider=" + provider + "}";
    }
}
