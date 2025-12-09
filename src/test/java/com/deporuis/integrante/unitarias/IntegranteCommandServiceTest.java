package com.deporuis.integrante.unitarias;

import com.deporuis.integrante.aplicacion.IntegranteCommandService;
import com.deporuis.integrante.aplicacion.helper.IntegranteRelacionService;
import com.deporuis.integrante.aplicacion.helper.IntegranteVerificarExistenciaService;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.dominio.IntegrantePosicion;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.integrante.infraestructura.dto.IntegranteRequest;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.deporte.dominio.Deporte;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class IntegranteCommandServiceTest {

    @InjectMocks private IntegranteCommandService service;
    @Mock private IntegranteVerificarExistenciaService verificar;
    @Mock private FotoCommandService fotoCmd;
    @Mock private IntegranteRelacionService relacion;
    @Mock private IntegranteRepository repo;

    AutoCloseable mocks;

    @BeforeEach void open() { mocks = MockitoAnnotations.openMocks(this); }
    @AfterEach  void close() throws Exception { mocks.close(); }

    private IntegranteRequest req() {
        IntegranteRequest r = new IntegranteRequest();
        r.setCodigoUniversitario("123");
        r.setNombres("Ana");
        r.setApellidos("Gomez");
        r.setFechaNacimiento(LocalDate.of(2000,1,1));
        r.setAltura(1.7f);
        r.setPeso(60f);
        r.setDorsal(10);
        r.setCorreoInstitucional("ana@correo.uis.edu.co");
        r.setIdRol(3);
        r.setIdSeleccion(1);
        r.setFotos(List.of(new FotoRequest()));
        r.setIdPosiciones(List.of(1,2));
        return r;
    }

    private Integrante buildIntegrante(int id) {
        Integrante i = new Integrante("123","Ana","Gomez", LocalDate.of(2000,1,1),1.7f,60f,10,"ana@correo.uis.edu.co");
        i.setIdIntegrante(id);
        i.setPosiciones(new ArrayList<>());
        i.setVisibilidad(true);
        return i;
    }

    @Test
    void crearIntegrante_ok() {
        doNothing().when(verificar).verificarPermisosCreacionIntegrantes(anyInt());
        when(verificar.verificarCorreoCodigoIntegrante(any(Integrante.class))).thenAnswer(inv -> inv.getArgument(0));
        Rol rol = new Rol(); rol.setIdRol(3); when(verificar.verificarRol(anyInt())).thenReturn(rol);
        Seleccion sel = new Seleccion(); sel.setIdSeleccion(1); when(verificar.verificarSeleccion(anyInt())).thenReturn(sel);
        Foto foto = new Foto(); foto.setIdFoto(7);
        when(fotoCmd.crearFotosIntegrante(anyList(), any())).thenReturn(List.of(foto));
        when(repo.save(any(Integrante.class))).thenAnswer(inv -> { Integrante i = inv.getArgument(0); i.setIdIntegrante(9); return i; });
        Deporte deporte = new Deporte(); deporte.setIdDeporte(1); deporte.setNombreDeporte("Fútbol");
        Posicion p1 = new Posicion(); p1.setIdPosicion(1); p1.setDeporte(deporte);
        Posicion p2 = new Posicion(); p2.setIdPosicion(2); p2.setDeporte(deporte);
        when(verificar.verificarPosiciones(anyList())).thenReturn(List.of(p1,p2));
        when(relacion.crearRelacionesPosicion(any(Integrante.class), anyList())).thenAnswer(inv -> {
            Integrante i = inv.getArgument(0);
            List<Posicion> ps = inv.getArgument(1);
            List<IntegrantePosicion> ips = new ArrayList<>();
            for (Posicion p : ps) ips.add(new IntegrantePosicion(null, i, p));
            return ips;
        });

        IntegranteResponse out = service.crearIntegrante(req());

        assertEquals(9, out.getIdIntegrante());
        verify(repo).save(any(Integrante.class));
        verify(relacion).crearRelacionesPosicion(any(Integrante.class), anyList());
    }

    @Test
    void actualizarIntegrante_ok() {
        doNothing().when(verificar).verificarPermisosCreacionIntegrantes(anyInt());
        Integrante existente = buildIntegrante(3);
        Foto old = new Foto(); old.setIdFoto(5); existente.setFotos(List.of(old));
        when(verificar.verificarIntegrante(3)).thenReturn(existente);
        when(verificar.verificarActualizarCodigoCorreoIntegrante(any(), any())).thenReturn(existente);
        Rol rol = new Rol(); rol.setIdRol(3); when(verificar.verificarRol(anyInt())).thenReturn(rol);
        Seleccion sel = new Seleccion(); sel.setIdSeleccion(1); when(verificar.verificarSeleccion(anyInt())).thenReturn(sel);
        doNothing().when(fotoCmd).eliminarFotosIntegrante(any());
        Foto nueva = new Foto(); nueva.setIdFoto(8);
        when(fotoCmd.crearFotosIntegrante(anyList(), any())).thenReturn(List.of(nueva));
        doNothing().when(relacion).eliminarRelacionesPosicion(any());
        Deporte deporte = new Deporte(); deporte.setIdDeporte(1); deporte.setNombreDeporte("Fútbol");
        Posicion p1 = new Posicion(); p1.setIdPosicion(1); p1.setDeporte(deporte);
        when(verificar.verificarPosiciones(anyList())).thenReturn(List.of(p1));
        when(relacion.crearRelacionesPosicion(any(Integrante.class), anyList())).thenReturn(List.of(new IntegrantePosicion(null, existente, p1)));
        when(repo.save(any(Integrante.class))).thenReturn(existente);

        IntegranteResponse out = service.actualizarIntegrante(3, req());

        assertEquals(3, out.getIdIntegrante());
        verify(repo).save(any(Integrante.class));
        verify(relacion).eliminarRelacionesPosicion(any());
        verify(relacion).crearRelacionesPosicion(any(Integrante.class), anyList());
    }

    @Test
    void softDelete_ok() {
        Integrante existente = buildIntegrante(4);
        when(verificar.verificarIntegrante(4)).thenReturn(existente);
        when(repo.save(existente)).thenReturn(existente);
        service.softDeleteIntegrante(4);
        verify(repo).save(existente);
    }
}
