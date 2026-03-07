package com.deporuis.integrante.aplicacion.helper;

import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.dominio.IntegrantePosicion;
import com.deporuis.integrante.infraestructura.IntegrantePosicionRepository;
import com.deporuis.posicion.dominio.Posicion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IntegranteRelacionService {

    @Autowired
    private IntegrantePosicionRepository integrantePosicionRepository;

    @Transactional()
    public List<IntegrantePosicion> crearRelacionesPosicion(Integrante integrante, List<Posicion> posiciones) {
        return integrantePosicionRepository.saveAll(
                posiciones.stream()
                        .map(posicion -> {
                            IntegrantePosicion ip = new IntegrantePosicion();
                            ip.setIntegrante(integrante);
                            ip.setPosicion(posicion);
                            return ip;
                        })
                        .toList()
        );
    }

    public void eliminarRelacionesPosicion(Integrante integrante) {
        integrantePosicionRepository.deleteAll(integrantePosicionRepository.findAllByIntegrante(integrante));
    }
}
