package swp490.spa.services;

import org.springframework.stereotype.Service;
import swp490.spa.entities.User;
import swp490.spa.repositories.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService (UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User findByPhoneAndPassword(String phone, String password, String role){
        return this.userRepository.findByPhoneAndPasswordAndRole(phone, password, role);
    }

    public User findByPhone(String phone){
        return this.userRepository.findByPhone(phone);
    }
}
