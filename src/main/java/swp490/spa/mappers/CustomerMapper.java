package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.CustomerResponse;
import swp490.spa.entities.Customer;

@Mapper
public interface CustomerMapper {
    CustomerResponse changeToCustomerResponse(Customer customer);
}
