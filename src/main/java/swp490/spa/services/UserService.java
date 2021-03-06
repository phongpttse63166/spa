package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.User;
import swp490.spa.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User findByPhone(String phone){
        return this.userRepository.findByPhone(phone);
    }

    public User insertNewUser(User user){
        return this.userRepository.save(user);
    }

    public User editUser(User user){
        return this.userRepository.save(user);
    }

    public User findByUserId(Integer userId) {
        return this.userRepository.findById(userId).get();
    }
}
