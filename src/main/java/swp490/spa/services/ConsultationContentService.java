package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.repositories.ConsultationContentRepository;

@Service
public class ConsultationContentService {
    @Autowired
    ConsultationContentRepository consultationContentRepository;
}
