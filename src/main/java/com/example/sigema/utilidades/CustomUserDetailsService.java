package com.example.sigema.utilidades;

import com.example.sigema.models.CustomUserDetails;
import com.example.sigema.models.Usuario;
import com.example.sigema.repositories.IRepositoryUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IRepositoryUsuario usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String cedula) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCedula(cedula).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        Long idUnidad = null;

        if(usuario.getUnidad() != null){
            idUnidad = usuario.getUnidad().getId();
        }

        return new CustomUserDetails(
                usuario.getCedula(),
                usuario.getPassword(),
                usuario.getRol(),
                idUnidad,
                usuario.getId(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
        );
    }
}