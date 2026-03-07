package com.deporuis.Foto.aplicacion;

import com.deporuis.Foto.aplicacion.helper.FotoVerificarExistenciaService;
import com.deporuis.Foto.aplicacion.mapper.FotoMapper;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FotoQueryService {

    @Autowired
    private FotoRepository fotoRepository;

    @Autowired
    private FotoVerificarExistenciaService verificarExistenciaService;

    @Autowired
    private IntegranteRepository integranteRepository;

    @Autowired
    private SeleccionRepository seleccionRepository;

    @Transactional(readOnly = true)
    public Page<FotoResponse> obtenerFotosPaginadas(Integer page, Integer size) {
        return fotoRepository.findAll(PageRequest.of(page, size))
                .map(FotoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public FotoResponse obtenerFoto(Integer id) {
        Foto foto = verificarExistenciaService.verificarFoto(id);
        return FotoMapper.toResponse(foto);
    }

    @Transactional(readOnly = true)
    public List<FotoResponse> obtenerFotosPorIntegrante(Integer idIntegrante) {
        Integrante integrante = integranteRepository.findById(idIntegrante)
                .orElseThrow(() -> new RuntimeException("Integrante no encontrado"));
        
        return fotoRepository.findAllByIntegrante(integrante).stream()
                .map(FotoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FotoResponse> obtenerFotosPorSeleccion(Integer idSeleccion) {
        Seleccion seleccion = seleccionRepository.findById(idSeleccion)
                .orElseThrow(() -> new RuntimeException("Selección no encontrada"));
        
        return fotoRepository.findAllBySeleccion(seleccion).stream()
                .map(FotoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FotoResponse obtenerPrimeraFotoPorIdSeleccion(Integer idSeleccion) {
        // Verificar que la selección existe
        seleccionRepository.findById(idSeleccion)
                .orElseThrow(() -> new RuntimeException("Selección no encontrada con id: " + idSeleccion));
        
        // Buscar la primera foto de la selección
        Foto foto = fotoRepository.findFirstBySeleccionIdOrderByIdFotoAsc(idSeleccion)
                .orElseThrow(() -> new RuntimeException("No se encontró ninguna foto para la selección con id: " + idSeleccion));
        
        return FotoMapper.toResponse(foto);
    }
}
