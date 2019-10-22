package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import java.io.Serializable;

public class FenixAuthenticationDto implements Serializable {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "FenixAuthenticationDto{" +
                "code='" + code + '\'' +
                '}';
    }
}
