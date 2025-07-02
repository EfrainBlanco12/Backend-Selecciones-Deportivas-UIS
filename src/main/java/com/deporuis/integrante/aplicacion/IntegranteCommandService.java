package com.deporuis.integrante.aplicacion;

import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.integrante.aplicacion.helper.IntegranteRelacionService;
import com.deporuis.integrante.aplicacion.helper.IntegranteVerificarExistenciaService;
import com.deporuis.integrante.aplicacion.mapper.IntegranteMapper;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.dominio.IntegrantePosicion;
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

    @Transactional()
    public IntegranteResponse crearIntegrante(IntegranteRequest integranteRequest) {
        Integrante integrante = IntegranteMapper.requestToIntegrante(integranteRequest);

        integranteVerificarExistenciaService.verificarCorreoCodigoIntegrante(integrante);

        Rol rol = integranteVerificarExistenciaService.verificarRol(integranteRequest.getIdRol());
        integrante.setRol(rol);

        Seleccion seleccion = integranteVerificarExistenciaService.verificarSeleccion(integranteRequest.getIdSeleccion());
        integrante.setSeleccion(seleccion);

        Foto fotoCreada = fotoCommandService.crearFotoIntegrante(integranteRequest.getFoto());
        fotoCreada = integranteVerificarExistenciaService.verificarFotoIntegrante(fotoCreada);
        integrante.setFoto(fotoCreada);

        List<Posicion> posiciones = integranteVerificarExistenciaService.verificarPosiciones(integranteRequest.getIdPosiciones());
        List<IntegrantePosicion> relacionesPosicion = integranteRelacionService.crearRelacionesPosicion(integrante, posiciones);

        integrante.setPosiciones(relacionesPosicion);

        return IntegranteMapper.integranteToResponse(integrante);
    }
}
