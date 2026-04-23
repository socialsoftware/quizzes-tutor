import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class scratch {
    public static class TestDto {
        public String name;
        
        public TestDto() {}

        @JsonIgnore
        public TestDto(String name, boolean deepCopy) {
            this.name = name;
        }
    }

    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TestDto dto = mapper.readValue("{\"name\":\"hello\"}", TestDto.class);
            System.out.println("Success: " + dto.name);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
