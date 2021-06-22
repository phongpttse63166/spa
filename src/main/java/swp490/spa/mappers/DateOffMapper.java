package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.DateOffResponse;
import swp490.spa.entities.DateOff;

@Mapper
public interface DateOffMapper {
    DateOffResponse changToDateOffResponse(DateOff dateOff);
}
