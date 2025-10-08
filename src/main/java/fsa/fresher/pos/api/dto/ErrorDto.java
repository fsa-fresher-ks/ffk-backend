package fsa.fresher.pos.api.dto;

import java.util.Map;

public class ErrorDto {
    private String code;
    private String message;
    private Map<String, Object> details;

    public ErrorDto() {}
    public ErrorDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
