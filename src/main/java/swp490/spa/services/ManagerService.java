package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Customer;
import swp490.spa.entities.Manager;
import swp490.spa.entities.Status;
import swp490.spa.repositories.ManagerRepository;

import java.util.List;

@Service
public class ManagerService {
    @Autowired
    private ManagerRepository managerRepository;

    public Manager findManagerById(Integer userId){
        return this.managerRepository.findByUserId(userId);
    }

    public Manager editManager(Manager manager) {
        return this.managerRepository.save(manager);
    }

    public List<Manager> findManagerBySpaAndStatusAvailable(Integer spaId) {
        return this.managerRepository.findBySpa_IdAndStatusOrderByIdAsc(spaId, Status.AVAILABLE);
    }
}
