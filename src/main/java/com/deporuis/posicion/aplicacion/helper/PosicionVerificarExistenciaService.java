package com.deporuis.posicion.aplicacion.helper;

import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.posicion.excepciones.PosicionNotFoundException;
import com.deporuis.posicion.infraestructura.PosicionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PosicionVerificarExistenciaService {

    @Autowired
    private PosicionRepository posicionRepository;

    @Transactional(readOnly = true)
    public Posicion verificarPosicion(Integer id) {
        Optional<Posicion> posicionOptional = posicionRepository.findById(id);

        if (posicionOptional.isEmpty()) {
            throw new PosicionNotFoundException("No se encontro posicion con ID = " + id);
        }

        Posicion posicion = posicionOptional.get();

        if (!Boolean.TRUE.equals(posicion.getVisibilidad())) {
            throw new PosicionNotFoundException("La posicion no esta disponible");
        }

        return posicion;
    }

    @Transactional(readOnly = true)
    public List<Posicion> verificarPosiciones(List<Integer> ids) {
        List<Posicion> posiciones = posicionRepository.findAllById(ids);

        if (posiciones.size() != ids.size()) {
            throw new PosicionNotFoundException("Alguna posicion no existe");
        }

        return posiciones;
    }
}
