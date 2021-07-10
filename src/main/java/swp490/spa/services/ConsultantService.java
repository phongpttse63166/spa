package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Consultant;
import swp490.spa.repositories.ConsultantRepository;

import java.util.List;

@Service
public class ConsultantService {
    @Autowired
    ConsultantRepository consultantRepository;

    public Consultant findByConsultantId(Integer consultanId){
        return this.consultantRepository.findConsultantByUserId(consultanId);
    }

    public List<Consultant> findBySpaId(Integer spaId) {
        return this.consultantRepository.findBySpa_Id(spaId);
    }

    public Consultant editConsultant(Consultant consultant) {
        return this.consultantRepository.save(consultant);
    }
}
