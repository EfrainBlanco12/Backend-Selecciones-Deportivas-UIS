package com.deporuis.integrante.aplicacion;

import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.integrante.aplicacion.helper.IntegranteRelacionService;
import com.deporuis.integrante.aplicacion.helper.IntegranteVerificarExistenciaService;
import com.deporuis.integrante.aplicacion.mapper.IntegranteMapper;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.dominio.IntegrantePosicion;
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

        /**
         * TODO: Verificar los roles que pueden crear integrantes con otros roles,
         *  admin crea todos, entrenador crea deportistas y el deportista no puede
         *  acceder a este metodo
         */

        Integrante integrante = IntegranteMapper.requestToIntegrante(integranteRequest);

        integrante = integranteVerificarExistenciaService.verificarCorreoCodigoIntegrante(integrante);

        Rol rol = integranteVerificarExistenciaService.verificarRol(integranteRequest.getIdRol());
        integrante.setRol(rol);

        Seleccion seleccion = integranteVerificarExistenciaService.verificarSeleccion(integranteRequest.getIdSeleccion());
        integrante.setSeleccion(seleccion);

        Foto fotoCreada = fotoCommandService.crearFotoIntegrante(integranteRequest.getFoto());
        fotoCreada = integranteVerificarExistenciaService.verificarFotoIntegrante(fotoCreada.getIdFoto());
        integrante.setFoto(fotoCreada);

        integrante = integranteRepository.save(integrante);

        List<Posicion> posiciones = integranteVerificarExistenciaService.verificarPosiciones(integranteRequest.getIdPosiciones());
        List<IntegrantePosicion> relacionesPosicion = integranteRelacionService.crearRelacionesPosicion(integrante, posiciones);

        integrante.setPosiciones(relacionesPosicion);

        return IntegranteMapper.integranteToResponse(integrante);
    }

    @Transactional()
    public IntegranteResponse actualizarIntegrante(Integer id, IntegranteRequest integranteRequest) {

        /**
         * TODO: Verificar los roles que pueden crear integrantes con otros roles,
         *  admin crea todos, entrenador crea deportistas y el deportista no puede
         *  acceder a este metodo
         */

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
        integrante.setSeleccion(seleccion);

        Foto fotoAntigua = integranteVerificarExistenciaService.verificarFotoIntegrante(integrante.getFoto().getIdFoto());
        fotoCommandService.eliminarFoto(fotoAntigua);
        Foto fotoCreada = fotoCommandService.crearFotoIntegrante(integranteRequest.getFoto());
        fotoCreada = integranteVerificarExistenciaService.verificarFotoIntegrante(fotoCreada.getIdFoto());
        integrante.setFoto(fotoCreada);

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
