package swp490.spa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.Customer;
import swp490.spa.services.CustomerService;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    private Conversion conversion;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
        this.conversion = new Conversion();
    }

    @GetMapping("/search/{userId}")
    public Response findCustomerById(@PathVariable Integer userId){
        Customer customer = customerService.findByUserId(userId);
        return ResponseHelper.ok(customer);
    }
}
