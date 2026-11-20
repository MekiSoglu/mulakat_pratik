package org.example.gun6.basic_creat.src.main.java.com.example.basic_creat.Exception;


import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String message,
        LocalDateTime localDateTime
) {
}
