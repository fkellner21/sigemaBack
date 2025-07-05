package com.example.sigema.utilidades;

import com.example.sigema.models.Usuario;
import com.example.sigema.models.enums.Rol;
import com.example.sigema.repositories.IRepositoryUsuario;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final IRepositoryUsuario usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(IRepositoryUsuario usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String cedulaAdmin = "12345678";

        boolean exists = usuarioRepository.findByCedula(cedulaAdmin).isPresent();

        if (!exists) {
            Usuario admin = new Usuario();
            admin.setCedula(cedulaAdmin);
            admin.setPassword(passwordEncoder.encode("123"));
            admin.setNombreCompleto("Admin");
            admin.setRol(Rol.ADMINISTRADOR);
            admin.setActivo(true);
            usuarioRepository.save(admin);
            System.out.println("Usuario administrador creado con cédula '12345678' y contraseña '123'");
        }
    }
}