package org.example.gun6.basic_creat.src.main.java.com.example.basic_creat.Service;

import com.example.basic_creat.DTO.CustomerResponseDto;
import com.example.basic_creat.DTO.CustomerUpdateDto;
import com.example.basic_creat.Entity.Customer;
import com.example.basic_creat.Exception.CustomerNotFountException;
import com.example.basic_creat.Repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    private final CustomerRepository customerRepository;

    public AccountService(final CustomerRepository customerRepository) {this.customerRepository = customerRepository;}

    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomer(Long customerId){

        Customer customer = findCustomer(customerId);
        return new CustomerResponseDto(customer.getName(),customer.getEmail());
    }


    @Transactional
    public CustomerResponseDto updateCustomer(Long customerId , CustomerUpdateDto customerUpdateDto){


        Customer customer = findCustomer(customerId);
        customer.setEmail(customerUpdateDto.email());
        customer.setName(customerUpdateDto.name());
        customerRepository.save(customer);
        return new CustomerResponseDto(customer.getName(),customer.getEmail());

    }

    @Transactional(readOnly = true)
    public Page<CustomerResponseDto> getAllCustomer(Pageable pageable){
        return customerRepository.findAll(pageable)
                .map(customer -> new CustomerResponseDto(customer.getName(), customer.getEmail()));

    }

    @Transactional
    public CustomerResponseDto createCustomer(CustomerUpdateDto customerCreateDto) {
        if (customerRepository.existsByEmail(customerCreateDto.email())) {
            throw new IllegalStateException("Bu email adresi zaten kullanımda: " + customerCreateDto.email());
        }

        Customer customer = new Customer();
        customer.setName(customerCreateDto.name());
        customer.setEmail(customerCreateDto.email());

        Customer savedCustomer = customerRepository.save(customer);

        return new CustomerResponseDto(savedCustomer.getName(), savedCustomer.getEmail());
    }



    private Customer findCustomer(Long customerId){
        return customerRepository.findById(customerId)
                                 .orElseThrow(() -> new CustomerNotFountException("customer bulunamadı"));
    }
}
