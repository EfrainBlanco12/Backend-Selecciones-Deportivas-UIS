package com.deporuis.deporte.aplicacion;

import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.deporte.infraestructura.DeporteRepository;
import com.deporuis.deporte.infraestructura.dto.DeporteResponse;
import com.deporuis.shared.util.TextoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeporteQueryService {

    @Autowired
    private DeporteRepository deporteRepository;

    @Transactional(readOnly = true)
    public Optional<Deporte> buscarPorNombreNormalizado(String nombre) {
        String nombreNormalizado = TextoUtil.quitarAcentos(nombre).toLowerCase();

        return deporteRepository.findAll().stream()
                .filter(d -> TextoUtil.quitarAcentos(d.getNombreDeporte()).toLowerCase().equals(nombreNormalizado))
                .findFirst();
    }

    @Transactional(readOnly = true)
    public List<DeporteResponse> obtenerTodosLosDeportesVisibles() {
        return deporteRepository.findAllByVisibilidadTrue()
                .stream()
                .map(DeporteResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Deporte> buscarPorId(Integer id) {
        return deporteRepository.findByIdDeporteAndVisibilidadTrue(id);
    }

    @Transactional(readOnly = true)
    public boolean existeNombreNormalizadoExcluyendoId(String nombreNormalizado, Integer idAExcluir) {
        return deporteRepository.findAll().stream()
                .filter(d -> !d.getIdDeporte().equals(idAExcluir))
                .anyMatch(d ->
                        TextoUtil.quitarAcentos(d.getNombreDeporte().toLowerCase())
                                .equals(nombreNormalizado));
    }
}
