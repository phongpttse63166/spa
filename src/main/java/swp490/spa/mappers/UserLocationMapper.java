package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.UserLocationResponse;
import swp490.spa.entities.UserLocation;

@Mapper
public interface UserLocationMapper {
    UserLocationResponse changeToUserLocationResponse(UserLocation userLocation);
}
