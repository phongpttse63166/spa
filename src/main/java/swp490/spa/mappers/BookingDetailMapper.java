package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.BookingDetailResponse;
import swp490.spa.entities.BookingDetail;

@Mapper
public interface BookingDetailMapper {
    BookingDetailResponse changeToBookingDetailResponse(BookingDetail bookingDetail);
}
