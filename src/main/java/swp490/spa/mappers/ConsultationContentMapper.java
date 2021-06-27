package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.ConsultationContentResponse;
import swp490.spa.entities.ConsultationContent;

@Mapper
public interface ConsultationContentMapper {
    ConsultationContentResponse changToConsultationContentResponse(ConsultationContent consultationContent);
}
