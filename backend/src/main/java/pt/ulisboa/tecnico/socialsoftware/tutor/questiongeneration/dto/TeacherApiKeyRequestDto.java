package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

/** The key a teacher gives. It is not printed, so it does not end up in a log. */
public class TeacherApiKeyRequestDto {
    private String key;

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }

    @Override
    public String toString() {
        return "TeacherApiKeyRequestDto{key=<hidden>}";
    }
}
