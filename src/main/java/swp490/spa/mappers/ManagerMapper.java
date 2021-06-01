package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.ManagerResponse;
import swp490.spa.entities.Manager;

@Mapper
public interface ManagerMapper {
    ManagerResponse changeToManagerResponse(Manager manager);
}
