package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.ConsultantResponse;
import swp490.spa.entities.Consultant;

@Mapper
public interface ConsultantMapper {
    ConsultantResponse changeToConsultResponse(Consultant consultant);
}
