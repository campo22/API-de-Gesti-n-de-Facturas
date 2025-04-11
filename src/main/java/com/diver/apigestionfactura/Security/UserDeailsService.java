package com.diver.apigestionfactura.Security;

import com.diver.apigestionfactura.dao.UserRepository;
import com.diver.apigestionfactura.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j // Se utiliza para registrar información de depuración
@Service
public class UserDeailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private User userDetails;

    // metodo para cargar un usuario por su nombre de usuario
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("Cargando usuario por nombre de usuario: {}", username);
        userDetails= userRepository.findByEmail(username);

        if (!Objects.isNull(userDetails)) {
            return new org.springframework.security.core.userdetails.User(
                    userDetails.getEmail(),
                    userDetails.getPassword(),
                    new ArrayList<>()
            );
        } else {
            log.error("Usuario no encontrado: {}", username);
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
    }
    public User getUserDetails() {
        return userDetails;
    }
}
