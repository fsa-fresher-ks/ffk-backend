package fsa.fresher.pos.api.dto;

public class LoginResponseDto {
    private String accessToken;
    private String tokenType = "Bearer";
    private Integer expiresIn;

    public LoginResponseDto() {}
    public LoginResponseDto(String accessToken, Integer expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public Integer getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Integer expiresIn) { this.expiresIn = expiresIn; }
}
