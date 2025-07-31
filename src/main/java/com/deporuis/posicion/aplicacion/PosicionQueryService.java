package com.deporuis.posicion.aplicacion;

import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.posicion.excepciones.PosicionNotFoundException;
import com.deporuis.posicion.infraestructura.PosicionRepository;
import com.deporuis.posicion.infraestructura.dto.PosicionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PosicionQueryService {

    @Autowired
    private PosicionRepository posicionRepository;

    public List<PosicionResponse> obtenerPosicionPorDeporte(Integer idDeporte) {
        List<Posicion> posiciones = posicionRepository.findAllByDeporte_IdDeporteAndVisibilidadTrue(idDeporte);

        if (posiciones.isEmpty()) {
            throw new PosicionNotFoundException("No se encontraron posiciones");
        }

        return posiciones.stream()
                .map(PosicionResponse::new)
                .collect(Collectors.toList());
    }


}
