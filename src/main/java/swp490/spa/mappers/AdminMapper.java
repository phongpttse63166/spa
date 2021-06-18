package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.AdminResponse;
import swp490.spa.entities.Admin;

@Mapper
public interface AdminMapper {
    AdminResponse changeToAdminResponse(Admin admin);
}
