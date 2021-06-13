package swp490.spa.mappers;

import swp490.spa.dto.responses.TreatmentServiceResponse;
import swp490.spa.entities.TreatmentService;

public interface TreatmentServiceMapper {
    TreatmentServiceResponse changeToTreatmentServiceResponse(TreatmentService treatmentService);
}
