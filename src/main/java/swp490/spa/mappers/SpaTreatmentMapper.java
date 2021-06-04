package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.SpaTreatmentResponse;
import swp490.spa.entities.SpaTreatment;

@Mapper
public interface SpaTreatmentMapper {
    SpaTreatmentResponse changeToSpaTreatmentResponse(SpaTreatment spaTreatment);
}
