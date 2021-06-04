package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.SpaPackageResponse;
import swp490.spa.entities.SpaPackage;

@Mapper
public interface SpaPackageMapper {
    SpaPackageResponse changeToSpaPackageResponse(SpaPackage spaPackage);
}
