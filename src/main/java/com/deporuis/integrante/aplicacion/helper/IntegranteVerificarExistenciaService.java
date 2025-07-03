package com.deporuis.integrante.aplicacion.helper;

import com.deporuis.Foto.aplicacion.helper.FotoVerificarExistenciaService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.auth.aplicacion.helper.RolVerificarExistenciaService;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.excepciones.IntegranteDobleUniqueKeyException;
import com.deporuis.integrante.excepciones.IntegranteNotFoundException;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.integrante.infraestructura.dto.IntegranteRequest;
import com.deporuis.posicion.aplicacion.helper.PosicionVerificarExistenciaService;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.dominio.Seleccion;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IntegranteVerificarExistenciaService {

    @Autowired
    private IntegranteRepository integranteRepository;

    @Autowired
    private RolVerificarExistenciaService rolVerificarExistenciaService;

    @Autowired
    private SeleccionVerificarExistenciaService seleccionVerificarExistenciaService;

    @Autowired
    private FotoVerificarExistenciaService fotoVerificarExistenciaService;

    @Autowired
    private PosicionVerificarExistenciaService posicionVerificarExistenciaService;

    public Integrante verificarCorreoCodigoIntegrante(Integrante integrante) {
        integrante = verificarCodigoIntegrante(integrante);
        integrante = verificarCorreoIntegrante(integrante);
        return integrante;
    }

    @Transactional()
    public Integrante verificarCodigoIntegrante(Integrante integrante) {
        integrante.setCodigoUniversitario(integrante.getCodigoUniversitario().toUpperCase());

        Optional<Integrante> codigoDuplicado = integranteRepository.findByCodigoUniversitario(integrante.getCodigoUniversitario());

        if (codigoDuplicado.isPresent()) {
            throw new IntegranteDobleUniqueKeyException("El codigo universitario ya existe");
        }

        return integrante;
    }

    @Transactional()
    public Integrante verificarCorreoIntegrante(Integrante integrante) {
        integrante.setCorreoInstitucional(integrante.getCorreoInstitucional().toUpperCase());

        Optional<Integrante> correoDuplicado = integranteRepository.findByCorreoInstitucional(integrante.getCorreoInstitucional());

        if (correoDuplicado.isPresent()) {
            throw new IntegranteDobleUniqueKeyException("El correo institucional ya existe");
        }

        return integrante;
    }

    public Rol verificarRol(Integer id) {
        return rolVerificarExistenciaService.verificarRol(id);
    }

    public Seleccion verificarSeleccion(Integer id) {
        return seleccionVerificarExistenciaService.verificarSeleccion(id);
    }

    public Foto verificarFotoIntegrante(Integer id) {
        return fotoVerificarExistenciaService.verificarFoto(id);
    }

    public List<Posicion> verificarPosiciones(List<Integer> idPosiciones) {
        return posicionVerificarExistenciaService.verificarPosiciones(idPosiciones);
    }

    @Transactional(readOnly = true)
    public Integrante verificarIntegrante(Integer id) {
        Optional<Integrante> integranteOptional = integranteRepository.findById(id);

        if (integranteOptional.isEmpty()) {
            throw new IntegranteNotFoundException("No se encontro Integrante con ID = " + id);
        }

        Integrante integrante = integranteOptional.get();

        if (!Boolean.TRUE.equals(integrante.getVisibilidad())) {
            throw new IntegranteNotFoundException("El integrante no esta disponible");
        }

        return integrante;
    }

    @Transactional()
    public Integrante verificarActualizarCodigoCorreoIntegrante(IntegranteRequest integranteRequest, Integrante integrante) {
        integrante.setCorreoInstitucional(integrante.getCorreoInstitucional().toUpperCase());
        integrante.setCodigoUniversitario(integrante.getCodigoUniversitario().toUpperCase());
        integranteRequest.setCorreoInstitucional(integranteRequest.getCorreoInstitucional().toUpperCase());
        integranteRequest.setCodigoUniversitario(integranteRequest.getCodigoUniversitario().toUpperCase());

        if (integranteRepository.existsByCodigoUniversitarioAndIdIntegranteNot(integranteRequest.getCodigoUniversitario(), integrante.getIdIntegrante())) {
            throw new IntegranteDobleUniqueKeyException("El codigo universitario ya existe");
        }

        if (integranteRepository.existsByCorreoInstitucionalAndIdIntegranteNot(integranteRequest.getCorreoInstitucional(), integrante.getIdIntegrante())) {
            throw new IntegranteDobleUniqueKeyException("El correo institucional ya existe");
        }

        integrante.setCodigoUniversitario(integranteRequest.getCodigoUniversitario());
        integrante.setCorreoInstitucional(integranteRequest.getCorreoInstitucional());

        return integrante;
    }
}
