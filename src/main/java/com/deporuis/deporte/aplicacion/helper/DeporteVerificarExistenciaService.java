package com.deporuis.deporte.aplicacion.helper;

import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.deporte.excepciones.DeporteNotFoundException;
import com.deporuis.deporte.infraestructura.DeporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DeporteVerificarExistenciaService {

    @Autowired
    private DeporteRepository deporteRepository;

    @Transactional(readOnly = true)
    public Deporte verificarDeporte(Integer id) {
        Optional<Deporte> deporte = deporteRepository.findById(id);

        if (deporte.isEmpty()) {
            throw new DeporteNotFoundException("No se encontro Deporte con ID = " + id);
        }

        return deporte.get();
    }

}
