package tableOrder.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tableOrder.users.dto.security.CustomUserDetails;
import tableOrder.users.entity.Users;
import tableOrder.users.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        Users usersData = userRepository.findByUserId(userId);
        if (usersData != null) {

            return new CustomUserDetails(usersData);
        }

        return null;
    }
}
