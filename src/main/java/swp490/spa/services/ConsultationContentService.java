package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.ConsultationContent;
import swp490.spa.repositories.ConsultationContentRepository;

@Service
public class ConsultationContentService {
    @Autowired
    ConsultationContentRepository consultationContentRepository;

    public ConsultationContent insertNewConsultationContent(ConsultationContent consultationContent) {
        return this.consultationContentRepository.save(consultationContent);
    }

    public ConsultationContent editByConsultationContent(ConsultationContent consultationContent) {
        return this.consultationContentRepository.save(consultationContent);
    }

    public ConsultationContent findByConsultationContentId(Integer consultationContentId) {
        return this.consultationContentRepository.findById(consultationContentId).get();
    }

    public void removeDB(Integer consultationContentId) {
        this.consultationContentRepository.deleteById(consultationContentId);
    }
}
