package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.AccountRegisterResponse;
import swp490.spa.entities.AccountRegister;

@Mapper
public interface AccountRegisterMapper {
    AccountRegisterResponse changeToAccountRegisterResponse(AccountRegister accountRegister);
}
