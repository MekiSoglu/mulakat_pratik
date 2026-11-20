package org.example.gun6.basic_creat.src.main.java.com.example.basic_creat.Repository;

import com.example.basic_creat.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

    boolean existsByEmail(String email);
}
