package com.umg.roboteducativo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String type = "Bearer";
    private String username;
    private String nombre;
    private String email;

    public LoginResponseDTO(String token, String username, String nombre, String email) {
        this.token = token;
        this.username = username;
        this.nombre = nombre;
        this.email = email;
    }
}