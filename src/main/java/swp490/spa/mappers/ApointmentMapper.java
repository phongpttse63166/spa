package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.ApointmentResponse;
import swp490.spa.entities.Apointment;

@Mapper
public interface ApointmentMapper {
    ApointmentResponse changToApointmentResponse(Apointment apointment);
}
