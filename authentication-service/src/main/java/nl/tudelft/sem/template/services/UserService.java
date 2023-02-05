package nl.tudelft.sem.template.services;

import nl.tudelft.sem.template.entities.User;
import nl.tudelft.sem.template.entities.UserPrincipal;
import nl.tudelft.sem.template.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private transient UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //load the user by username depending on where you have your user saved
        try {
            User user = this.userRepository.findByUsername(username).get(0);
            UserPrincipal userPrincipal = new UserPrincipal(user);

            return userPrincipal;
        } catch (IndexOutOfBoundsException ex) {
            throw new UsernameNotFoundException("Username not found");
        }

    }
}


