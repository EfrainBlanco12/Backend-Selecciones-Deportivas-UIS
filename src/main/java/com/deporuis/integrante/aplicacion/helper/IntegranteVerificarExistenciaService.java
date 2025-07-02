package com.deporuis.integrante.aplicacion.helper;

import com.deporuis.Foto.aplicacion.helper.FotoVerificarExistenciaService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.auth.aplicacion.helper.RolVerificarExistenciaService;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.excepciones.IntegranteDobleUniqueKeyException;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
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

    @Transactional(readOnly = true)
    public void verificarCorreoCodigoIntegrante(Integrante integrante) {
        Optional<Integrante> codigoDuplicado = integranteRepository.findByCodigoUniversitario(integrante.getCodigoUniversitario());
        Optional<Integrante> correoDuplicado = integranteRepository.findByCorreoInstitucional(integrante.getCorreoInstitucional());

        if (codigoDuplicado.isPresent()) {
            throw new IntegranteDobleUniqueKeyException("El codigo universitario ya existe");
        }

        if (correoDuplicado.isPresent()) {
            throw new IntegranteDobleUniqueKeyException("El correo institucional ya existe");
        }
    }

    public Rol verificarRol(Integer id) {
        return rolVerificarExistenciaService.verificarRol(id);
    }

    public Seleccion verificarSeleccion(Integer id) {
        return seleccionVerificarExistenciaService.verificarSeleccion(id);
    }

    public Foto verificarFotoIntegrante(Foto foto) {
        return fotoVerificarExistenciaService.verificarFoto(foto.getIdFoto());
    }

    public List<Posicion> verificarPosiciones(List<Integer> idPosiciones) {
        return posicionVerificarExistenciaService.verificarPosiciones(idPosiciones);
    }
}
