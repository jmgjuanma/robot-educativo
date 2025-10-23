package com.umg.roboteducativo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO<T> {

    private boolean success;
    
    private String message;
    
    private T data;

    /**
     * Constructor para respuestas exitosas con datos
     */
    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return new ApiResponseDTO<>(true, message, data);
    }

    /**
     * Constructor para respuestas exitosas sin datos
     */
    public static <T> ApiResponseDTO<T> success(String message) {
        return new ApiResponseDTO<>(true, message, null);
    }

    /**
     * Constructor para respuestas de error
     */
    public static <T> ApiResponseDTO<T> error(String message) {
        return new ApiResponseDTO<>(false, message, null);
    }

    /**
     * Constructor para respuestas de error con datos adicionales
     */
    public static <T> ApiResponseDTO<T> error(String message, T data) {
        return new ApiResponseDTO<>(false, message, data);
    }
}