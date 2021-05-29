package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.UserResponse;
import swp490.spa.entities.User;

@Mapper
public interface UserMapper {
    UserResponse changeToUserResponse(User user);
}
