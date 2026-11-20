package org.example.gun6.basic_creat.src.main.java.com.example.basic_creat.Controller;

import com.example.basic_creat.DTO.CustomerResponseDto;
import com.example.basic_creat.DTO.CustomerUpdateDto;
import com.example.basic_creat.Service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/customer")
public class CustomerController {

   private final AccountService accountService;

    public CustomerController(final AccountService accountService) {this.accountService = accountService;}

    @GetMapping("/{accountId}")
    public ResponseEntity<CustomerResponseDto> getCustomer(@PathVariable Long accountId){
        CustomerResponseDto customerResponseDto = accountService.getCustomer(accountId);
        return ResponseEntity.ok(customerResponseDto);
    }


    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody @Valid CustomerUpdateDto request) {
        CustomerResponseDto response = accountService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
