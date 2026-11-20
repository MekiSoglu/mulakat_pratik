package org.example.gun6.basic_creat.src.main.java.com.example.basic_creat.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerUpdateDto(
        @NotBlank(message = "isim boş olamaz")
        String name,

        @NotBlank(message = "mail boş olamaz ")
        @Email(message = "geçerli bir formatta girin")
                String email
) {

}
