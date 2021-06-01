package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.SpaResponse;
import swp490.spa.entities.Spa;

@Mapper
public interface SpaMapper {
    SpaResponse changeToSpaResponse(Spa spa);
}
