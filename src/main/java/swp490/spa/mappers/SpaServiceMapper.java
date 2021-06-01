package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.SpaServiceResponse;
import swp490.spa.entities.SpaService;

@Mapper
public interface SpaServiceMapper {
    SpaServiceResponse changeToSpaServiceResponse(SpaService spaService);
}
