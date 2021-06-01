package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.SpaAddressResponse;
import swp490.spa.entities.SpaAddress;

@Mapper
public interface SpaAddressMapper {
    SpaAddressResponse changeToSpaAddressResponse(SpaAddress spaAddress);
}
