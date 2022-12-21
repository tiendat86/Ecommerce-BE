package com.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO<T> {
    private String nameCode;
    private String message;
    private T body;

    public static ResponseDTO successResponse(Object body) {
        return ResponseDTO.builder()
                .nameCode(HttpStatus.OK.value() + " - " + HttpStatus.OK.name())
                .message("Successful")
                .body(body)
                .build();
    }

    public static ResponseDTO failedResponse(String message, HttpStatus httpStatus) {
        return ResponseDTO.builder()
                .nameCode(httpStatus.value() + " " + httpStatus.name())
                .message(message)
                .build();
    }
}
