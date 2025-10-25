package com.deporuis.integrante.aplicacion;

import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.integrante.aplicacion.helper.IntegranteRelacionService;
import com.deporuis.integrante.aplicacion.helper.IntegranteVerificarExistenciaService;
import com.deporuis.integrante.aplicacion.mapper.IntegranteMapper;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.dominio.IntegrantePosicion;
import com.deporuis.integrante.excepciones.IntegranteEntrenadorSeleccionExists;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.integrante.infraestructura.dto.IntegranteRequest;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.seleccion.dominio.Seleccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IntegranteCommandService {

    @Autowired
    private IntegranteVerificarExistenciaService integranteVerificarExistenciaService;

    @Autowired
    private FotoCommandService fotoCommandService;

    @Autowired
    private IntegranteRelacionService integranteRelacionService;

    @Autowired
    private IntegranteRepository integranteRepository;

    @Transactional()
    public IntegranteResponse crearIntegrante(IntegranteRequest integranteRequest) {
        integranteVerificarExistenciaService.verificarPermisosCreacionIntegrantes(integranteRequest.getIdRol());

        Integrante integrante = IntegranteMapper.requestToIntegrante(integranteRequest);

        integrante = integranteVerificarExistenciaService.verificarCorreoCodigoIntegrante(integrante);

        Rol rol = integranteVerificarExistenciaService.verificarRol(integranteRequest.getIdRol());
        integrante.setRol(rol);

        Seleccion seleccion = integranteVerificarExistenciaService.verificarSeleccion(integranteRequest.getIdSeleccion());

        // 🚨 Nueva validación: si el rol es ENTRENADOR, verificar que la selección no tenga ya otro
        if ("ENTRENADOR".equalsIgnoreCase(rol.getNombreRol())) {
            boolean yaTieneEntrenador = integranteRepository
                    .existsBySeleccion_IdSeleccionAndRol_NombreRolAndVisibilidadTrue(seleccion.getIdSeleccion(), "ENTRENADOR");

            if (yaTieneEntrenador) {
                throw new IntegranteEntrenadorSeleccionExists("La selección " + seleccion.getIdSeleccion() + " ya tiene un entrenador asignado");
            }
        }

        integrante.setSeleccion(seleccion);

        // Crear fotos si se proporcionaron
        if (integranteRequest.getFotos() != null && !integranteRequest.getFotos().isEmpty()) {
            List<com.deporuis.Foto.dominio.Foto> fotos = fotoCommandService.crearFotosIntegrante(integranteRequest.getFotos(), integrante);
            integrante.setFotos(fotos);
        }

        integrante = integranteRepository.save(integrante);

        List<Posicion> posiciones = integranteVerificarExistenciaService.verificarPosiciones(integranteRequest.getIdPosiciones());
        List<IntegrantePosicion> relacionesPosicion = integranteRelacionService.crearRelacionesPosicion(integrante, posiciones);

        integrante.setPosiciones(relacionesPosicion);

        return IntegranteMapper.integranteToResponse(integrante);
    }


    @Transactional
    public IntegranteResponse actualizarIntegrante(Integer id, IntegranteRequest integranteRequest) {

        integranteVerificarExistenciaService.verificarPermisosCreacionIntegrantes(integranteRequest.getIdRol());

        Integrante integrante = integranteVerificarExistenciaService.verificarIntegrante(id);

        integrante = integranteVerificarExistenciaService.verificarActualizarCodigoCorreoIntegrante(integranteRequest, integrante);

        integrante.setCodigoUniversitario(integranteRequest.getCodigoUniversitario());
        integrante.setNombres(integranteRequest.getNombres());
        integrante.setApellidos(integranteRequest.getApellidos());
        integrante.setFechaNacimiento(integranteRequest.getFechaNacimiento());
        integrante.setAltura(integranteRequest.getAltura());
        integrante.setPeso(integranteRequest.getPeso());
        integrante.setDorsal(integranteRequest.getDorsal());
        integrante.setCorreoInstitucional(integranteRequest.getCorreoInstitucional());

        Rol rol = integranteVerificarExistenciaService.verificarRol(integranteRequest.getIdRol());
        integrante.setRol(rol);

        Seleccion seleccion = integranteVerificarExistenciaService.verificarSeleccion(integranteRequest.getIdSeleccion());

        // 🚨 Nueva validación: si el rol es ENTRENADOR, asegurarse que no haya otro en la selección
        if ("ENTRENADOR".equalsIgnoreCase(rol.getNombreRol())) {
            boolean yaTieneOtroEntrenador = integranteRepository
                    .existsBySeleccion_IdSeleccionAndRol_NombreRolAndVisibilidadTrueAndIdIntegranteNot(
                            seleccion.getIdSeleccion(), "ENTRENADOR", integrante.getIdIntegrante()
                    );

            if (yaTieneOtroEntrenador) {
                throw new IntegranteEntrenadorSeleccionExists(
                        "La selección " + seleccion.getIdSeleccion() + " ya tiene un entrenador asignado"
                );
            }
        }

        integrante.setSeleccion(seleccion);

        // Eliminar fotos antiguas y crear nuevas si se proporcionaron
        fotoCommandService.eliminarFotosIntegrante(integrante);
        
        if (integranteRequest.getFotos() != null && !integranteRequest.getFotos().isEmpty()) {
            List<com.deporuis.Foto.dominio.Foto> fotosNuevas = fotoCommandService.crearFotosIntegrante(integranteRequest.getFotos(), integrante);
            integrante.setFotos(fotosNuevas);
        }

        integranteRelacionService.eliminarRelacionesPosicion(integrante);
        List<Posicion> posiciones = integranteVerificarExistenciaService.verificarPosiciones(integranteRequest.getIdPosiciones());
        List<IntegrantePosicion> relacionesPosicion = integranteRelacionService.crearRelacionesPosicion(integrante, posiciones);

        integrante.setPosiciones(relacionesPosicion);

        integranteRepository.save(integrante);

        return IntegranteMapper.integranteToResponse(integrante);
    }


    @Transactional()
    public void softDeleteIntegrante(Integer id) {
        Integrante integrante = integranteVerificarExistenciaService.verificarIntegrante(id);

        integrante.setVisibilidad(false);
        integranteRepository.save(integrante);
    }
}
