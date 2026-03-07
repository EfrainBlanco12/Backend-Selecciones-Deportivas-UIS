package com.deporuis.posicion.aplicacion.helper;

import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.posicion.excepciones.PosicionNotFoundException;
import com.deporuis.posicion.excepciones.PosicionYaExisteException;
import com.deporuis.posicion.infraestructura.PosicionRepository;
import com.deporuis.posicion.infraestructura.dto.PosicionRequest;
import com.deporuis.shared.util.TextoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PosicionVerificarExistenciaService {

    @Autowired
    private PosicionRepository posicionRepository;

    @Transactional
    public Optional<Posicion> verificarPosicionNoExisteYReactivarSiAplica(PosicionRequest request) {
        String nombreNormalizado = TextoUtil.quitarAcentos(request.getNombrePosicion()).toLowerCase();

        Optional<Posicion> posicionExistente = posicionRepository.findAllByDeporte_IdDeporte(request.getIdDeporte()).stream()
                .filter(p -> TextoUtil.quitarAcentos(p.getNombrePosicion()).toLowerCase().equals(nombreNormalizado))
                .findFirst();

        if (posicionExistente.isPresent()) {
            Posicion existente = posicionExistente.get();

            if (Boolean.TRUE.equals(existente.getVisibilidad())) {
                throw new PosicionYaExisteException("Ya existe una posición con el nombre '" + request.getNombrePosicion() + "' en el deporte especificado.");
            } else {
                existente.setVisibilidad(true);
                return Optional.of(posicionRepository.save(existente));
            }
        }

        return Optional.empty();
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
