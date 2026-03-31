package com.haiilo.interview.haiilosupermarketcheckout.api.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JWTResponseDTO {
    private String token;
    private String type = "Bearer";

    public JWTResponseDTO(String accessToken) {
        this.token = accessToken;
    }
}
