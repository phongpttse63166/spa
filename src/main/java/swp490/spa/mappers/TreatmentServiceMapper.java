package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.TreatmentServiceResponse;
import swp490.spa.entities.TreatmentService;

@Mapper
public interface TreatmentServiceMapper {
    TreatmentServiceResponse changToTreatmentServiceResponse(TreatmentService treatmentService);
}
