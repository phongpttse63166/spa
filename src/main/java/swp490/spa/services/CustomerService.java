package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Customer;
import swp490.spa.repositories.CustomerRepository;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public Customer findByUserId(Integer userId){
        return customerRepository.findByUserId(userId);
    }
}
