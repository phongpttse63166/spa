//package swp490.spa.jwt;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import swp490.spa.repositories.UserRepository;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class CustomerUserDetailService implements UserDetailsService {
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
//        swp490.spa.entities.User account = userRepository.findByPhone(phoneNumber);
//        List<GrantedAuthority> listGrantedAuthorities = new ArrayList<>();
//        listGrantedAuthorities.add(new SimpleGrantedAuthority(account.getRole().name()));
//        return new User(account.getPhone(), account.getPassword(),listGrantedAuthorities );
//    }
//}
