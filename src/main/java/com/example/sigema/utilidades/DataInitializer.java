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
        String cedulaAdmin = "admin";

        boolean exists = usuarioRepository.findByCedula(cedulaAdmin).isPresent();

        if (!exists) {
            Usuario admin = new Usuario();
            admin.setCedula(cedulaAdmin);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombreCompleto("Admin");
            admin.setRol(Rol.ADMINISTRADOR);
            usuarioRepository.save(admin);
            System.out.println("Usuario administrador creado con cédula 'admin' y contraseña 'admin123'");
        }
    }
}