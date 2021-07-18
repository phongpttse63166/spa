package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Consultant;
import swp490.spa.entities.Status;
import swp490.spa.repositories.ConsultantRepository;

import java.net.ContentHandler;
import java.util.List;

@Service
public class ConsultantService {
    @Autowired
    ConsultantRepository consultantRepository;

    public Consultant findByConsultantId(Integer consultantId){
        return this.consultantRepository.findConsultantByUserId(consultantId);
    }

    public List<Consultant> findBySpaIdAndStatusAvailable(Integer spaId) {
        return this.consultantRepository.findBySpa_IdAndStatus(spaId, Status.AVAILABLE);
    }

    public Consultant editConsultant(Consultant consultant) {
        return this.consultantRepository.save(consultant);
    }

    public List<Consultant> findBySpaIdAndNameLike(Integer spaId, String search) {
        return this.consultantRepository.findConsultantBySpaIdAndNameLike(spaId, search);
    }

    public Consultant insertNewConsultant(Consultant consultant) {
        return this.consultantRepository.save(consultant);
    }
}
