package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.BookingDetailStepResponse;
import swp490.spa.entities.BookingDetailStep;

@Mapper
public interface BookingDetailStepMapper {
    BookingDetailStepResponse changeToBookingDetailStepResponse(BookingDetailStep bookingDetailStep);
}
