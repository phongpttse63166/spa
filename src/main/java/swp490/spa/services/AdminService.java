package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Admin;
import swp490.spa.repositories.AdminRepository;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    public Admin findByUserId(Integer userId){
        return this.adminRepository.findByUserId(userId);
    }
}
