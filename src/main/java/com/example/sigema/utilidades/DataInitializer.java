package com.example.sigema.utilidades;

import com.example.sigema.models.Marca;
import com.example.sigema.models.TipoEquipo;
import com.example.sigema.models.Usuario;
import com.example.sigema.models.enums.Rol;
import com.example.sigema.models.enums.TareaEquipo;
import com.example.sigema.repositories.IMarcaRepository;
import com.example.sigema.repositories.IRepositoryUsuario;
import com.example.sigema.repositories.ITiposEquiposRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final IRepositoryUsuario usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ITiposEquiposRepository tiposEquiposRepository;
    private final IMarcaRepository marcaRepository;

    public DataInitializer(IRepositoryUsuario usuarioRepository, PasswordEncoder passwordEncoder,
                           ITiposEquiposRepository tiposEquiposRepository1, IMarcaRepository marcaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tiposEquiposRepository = tiposEquiposRepository1;
        this.marcaRepository = marcaRepository;
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

        boolean iniciarDatos = false; //completar a mano la 1a vez

        if(iniciarDatos) {

        TipoEquipo te= new TipoEquipo();
        te.setActivo(true);
        te.setCodigo("TRABA");
        te.setNombre("Tractor de bandas");
        te.setTarea(TareaEquipo.Corte);
        tiposEquiposRepository.save(te);

        TipoEquipo te2= new TipoEquipo();
        te2.setActivo(true);
        te2.setCodigo("TRAPA");
        te2.setNombre("Tractor pala");
        te2.setTarea(TareaEquipo.Carga);
        tiposEquiposRepository.save(te2);

        TipoEquipo te3= new TipoEquipo();
        te3.setActivo(true);
        te3.setCodigo("TRAPARE");
        te3.setNombre("Tractor pala con retroexcavadora");
        te3.setTarea(TareaEquipo.Carga);
        tiposEquiposRepository.save(te3);

        TipoEquipo te4= new TipoEquipo();
        te4.setActivo(true);
        te4.setCodigo("COMVI");
        te4.setNombre("Compactador vibratorio");
        te4.setTarea(TareaEquipo.Compactacion);
        tiposEquiposRepository.save(te4);

        TipoEquipo te5= new TipoEquipo();
        te5.setActivo(true);
        te5.setCodigo("CAVOL");
        te5.setNombre("Camion volcador");
        te5.setTarea(TareaEquipo.Acarreo);
        tiposEquiposRepository.save(te5);

        Marca m = new Marca();
        m.setNombre("JCB");
        marcaRepository.save(m);

        Marca m2 = new Marca();
        m2.setNombre("Zoomlion");
        marcaRepository.save(m2);

        Marca m3 = new Marca();
        m3.setNombre("Komatsu");
        marcaRepository.save(m3);

        Marca m4 = new Marca();
        m4.setNombre("XCMG");
        marcaRepository.save(m4);

        Marca m5 = new Marca();
        m5.setNombre("Sany");
        marcaRepository.save(m5);

            System.out.println("se cargaron 5  marcas y 5 tipos de equipo");
        }
    }
}
