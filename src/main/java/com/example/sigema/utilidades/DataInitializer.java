package com.example.sigema.utilidades;

import com.example.sigema.models.*;
import com.example.sigema.models.enums.Rol;
import com.example.sigema.models.enums.TareaEquipo;
import com.example.sigema.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final IRepositoryUsuario usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ITiposEquiposRepository tiposEquiposRepository;
    private final IMarcaRepository marcaRepository;
    private final IUnidadRepository unidadRepository;
    private final IRepositoryGrado gradoRepository;

    public DataInitializer(IRepositoryUsuario usuarioRepository, PasswordEncoder passwordEncoder,
                           ITiposEquiposRepository tiposEquiposRepository1, IMarcaRepository marcaRepository,
                           IUnidadRepository unidadRepository, IRepositoryGrado gradoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tiposEquiposRepository = tiposEquiposRepository1;
        this.marcaRepository = marcaRepository;
        this.unidadRepository = unidadRepository;
        this.gradoRepository = gradoRepository;
    }

    @Override
    public void run(String... args) throws Exception {

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
        te5.setNombre("Camión volcador");
        te5.setTarea(TareaEquipo.Acarreo);
        tiposEquiposRepository.save(te5);

        TipoEquipo te6= new TipoEquipo();
        te6.setActivo(true);
        te6.setCodigo("RETRO");
        te6.setNombre("Retroexcavadora");
        te6.setTarea(TareaEquipo.Corte);
        tiposEquiposRepository.save(te6);

        TipoEquipo te7= new TipoEquipo();
        te7.setActivo(true);
        te7.setCodigo("MINI");
        te7.setNombre("Minicargador");
        te7.setTarea(TareaEquipo.Carga);
        tiposEquiposRepository.save(te7);

        TipoEquipo te8= new TipoEquipo();
        te8.setActivo(true);
        te8.setCodigo("GRUA");
        te8.setNombre("Grua");
        te8.setTarea(TareaEquipo.Carga);
        tiposEquiposRepository.save(te8);

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

        Marca m6 = new Marca();
        m6.setNombre("Caterpillar");
        marcaRepository.save(m6);

        Unidad u = new Unidad();
        u.setNombre("Area Logistica");
        u.setEsGranUnidad(true);
        u.setLatitud((float) -34.850071);
        u.setLongitud((float) -56.036356);
        unidadRepository.save(u);

        Unidad u1 = new Unidad();
        u1.setNombre("B. Ing. No.1");
        u1.setEsGranUnidad(false);
        u1.setLatitud((float) -34.850071);
        u1.setLongitud((float) -56.036356);
        unidadRepository.save(u1);

        Unidad u2 = new Unidad();
        u2.setNombre("Bn. Ing. Cbte. No.1");
        u2.setEsGranUnidad(false);
        u2.setLatitud((float) -34.847771);
        u2.setLongitud((float) -56.167278);
        unidadRepository.save(u2);

        Unidad u3 = new Unidad();
        u3.setNombre("Bn. Ing. Cbte. No.2");
        u3.setEsGranUnidad(false);
        u3.setLatitud((float) -34.097490);
        u3.setLongitud((float) -56.189173);
        unidadRepository.save(u3);

        Unidad u4 = new Unidad();
        u4.setNombre("Bn. Ing. Cbte. No.3");
        u4.setEsGranUnidad(false);
        u4.setLatitud((float) -32.802695);
        u4.setLongitud((float) -56.507765);
        unidadRepository.save(u4);

        Unidad u5 = new Unidad();
        u5.setNombre("Bn. Ing. Cbte. No.4");
        u5.setEsGranUnidad(false);
        u5.setLatitud((float) -34.863825);
        u5.setLongitud((float) -55.085943);
        unidadRepository.save(u5);

        Unidad u6 = new Unidad();
        u6.setNombre("Bn. Ing. Const. No.5");
        u6.setEsGranUnidad(false);
        u6.setLatitud((float) -34.849323);
        u6.setLongitud((float) -56.036750);
        unidadRepository.save(u6);

        Unidad u7 = new Unidad();
        u7.setNombre("Bn. Ing. Cbte. No.6");
        u7.setEsGranUnidad(false);
        u7.setLatitud((float) -34.850541);
        u7.setLongitud((float) -56.037640);
        unidadRepository.save(u7);

        Grado g = new Grado();
        g.setNombre("Sdo. 1º.");
        gradoRepository.save(g);

        Grado g1 = new Grado();
        g1.setNombre("Cbo. 2º.");
        gradoRepository.save(g1);

        Grado g2 = new Grado();
        g2.setNombre("Cbo. 1º.");
        gradoRepository.save(g2);

        Grado g3 = new Grado();
        g3.setNombre("Sgto.");
        gradoRepository.save(g3);

        Grado g4 = new Grado();
        g4.setNombre("Sgto. 1º.");
        gradoRepository.save(g4);

        Grado g5 = new Grado();
        g5.setNombre("S.O.M.");
        gradoRepository.save(g5);

        Grado g6 = new Grado();
        g6.setNombre("Alf.");
        gradoRepository.save(g6);

        Grado g7 = new Grado();
        g7.setNombre("Tte. 2º.");
        gradoRepository.save(g7);

        Grado g8 = new Grado();
        g8.setNombre("Tte. 1º.");
        gradoRepository.save(g8);

        Grado g9 = new Grado();
        g9.setNombre("Cap.");
        gradoRepository.save(g9);

        Grado g10 = new Grado();
        g10.setNombre("May.");
        gradoRepository.save(g10);

        Grado g11 = new Grado();
        g11.setNombre("Tte.Cnel.");
        gradoRepository.save(g11);

        Grado g12 = new Grado();
        g12.setNombre("Cnel.");
        gradoRepository.save(g12);

        Grado g13 = new Grado();
        g13.setNombre("Gral.");
        gradoRepository.save(g13);

        Usuario adm = new Usuario();
        adm.setCedula("12345678");
        adm.setPassword(passwordEncoder.encode("123admin"));
        adm.setNombreCompleto("Admin");
        adm.setRol(Rol.ADMINISTRADOR);
        adm.setActivo(true);
        adm.setIdUnidad(1L);
        adm.setUnidad(unidadRepository.getReferenceById(1L));
        usuarioRepository.save(adm);

//        Usuario bri = new Usuario();
//        bri.setCedula("23456789");
//        bri.setPassword(passwordEncoder.encode("123"));
//        bri.setNombreCompleto("Brigada");
//        bri.setRol(Rol.BRIGADA);
//        bri.setActivo(true);
//        bri.setIdUnidad(2L);
//        bri.setUnidad(unidadRepository.getReferenceById(2L));
//        bri.setIdGrado(4L);
//        bri.setGrado(gradoRepository.getReferenceById(4L));
//        usuarioRepository.save(bri);
//
//        Usuario uni = new Usuario();
//        uni.setCedula("34567890");
//        uni.setPassword(passwordEncoder.encode("123"));
//        uni.setNombreCompleto("Unidad 1");
//        uni.setRol(Rol.UNIDAD);
//        uni.setActivo(true);
//        uni.setIdUnidad(3L);
//        uni.setUnidad(unidadRepository.getReferenceById(3L));
//        uni.setIdGrado(2L);
//        uni.setGrado(gradoRepository.getReferenceById(2L));
//        usuarioRepository.save(uni);

        System.out.println("se cargaron los datos");
    }

//    String cedulaAdmin = "12345678";
//    boolean exists = usuarioRepository.findByCedula(cedulaAdmin).isPresent();
//    if (!exists) {
//        Usuario admin = new Usuario();
//        admin.setCedula(cedulaAdmin);
//        admin.setPassword(passwordEncoder.encode("123"));
//        admin.setNombreCompleto("Admin");
//        admin.setRol(Rol.ADMINISTRADOR);
//        admin.setActivo(true);
//        usuarioRepository.save(admin);
//        System.out.println("Usuario administrador creado con cédula '12345678' y contraseña '123'");
//    }
    }
}
