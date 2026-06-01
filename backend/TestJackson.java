import com.fasterxml.jackson.databind.ObjectMapper;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto;
public class TestJackson {
    public static void main(String[] args) throws Exception {
        String json = "{\"message\":\"DISCUSSAO\",\"courseExecutionId\":11,\"date\":\"2026-06-01T23:14:00.000Z\"}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            DiscussionDto dto = mapper.readValue(json, DiscussionDto.class);
            System.out.println("Success! " + dto.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
