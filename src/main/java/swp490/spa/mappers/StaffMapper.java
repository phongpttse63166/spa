package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.StaffResponse;
import swp490.spa.entities.Staff;

@Mapper
public interface StaffMapper {
    StaffResponse changeToStaffResponse(Staff staff);
}
