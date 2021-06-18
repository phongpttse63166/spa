package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.BookingResponse;
import swp490.spa.entities.Booking;

@Mapper
public interface BookingMapper {
    BookingResponse changToBookingResponse(Booking booking);
}
