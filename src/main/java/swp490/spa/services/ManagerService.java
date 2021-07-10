package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Customer;
import swp490.spa.entities.Manager;
import swp490.spa.repositories.ManagerRepository;

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
}
