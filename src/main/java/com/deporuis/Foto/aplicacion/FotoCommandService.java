package com.deporuis.Foto.aplicacion;

import com.deporuis.Foto.aplicacion.mapper.FotoMapper;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FotoCommandService {

    @Autowired
    private FotoRepository fotoRepository;

    @Autowired
    private IntegranteRepository integranteRepository;

    @Autowired
    private SeleccionRepository seleccionRepository;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Transactional()
    public FotoResponse crearFoto(FotoRequest fotoRequest) {
        Foto nuevaFoto = FotoMapper.requestToFoto(fotoRequest);

        // Asignar relaciones según los IDs proporcionados
        if (fotoRequest.getIdIntegrante() != null) {
            Integrante integrante = integranteRepository.findById(fotoRequest.getIdIntegrante())
                    .orElseThrow(() -> new RuntimeException("Integrante no encontrado"));
            nuevaFoto.setIntegrante(integrante);
        }

        if (fotoRequest.getIdSeleccion() != null) {
            Seleccion seleccion = seleccionRepository.findById(fotoRequest.getIdSeleccion())
                    .orElseThrow(() -> new RuntimeException("Selección no encontrada"));
            nuevaFoto.setSeleccion(seleccion);
        }

        if (fotoRequest.getIdPublicacion() != null) {
            Publicacion publicacion = publicacionRepository.findById(fotoRequest.getIdPublicacion())
                    .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));
            nuevaFoto.setPublicacion(publicacion);
        }

        Foto fotoGuardada = fotoRepository.save(nuevaFoto);
        return FotoMapper.toResponse(fotoGuardada);
    }

    @Transactional()
    public Foto crearFotoIntegrante(FotoRequest fotoRequest) {
        Foto nuevaFoto = FotoMapper.requestToFoto(fotoRequest);
        
        if (fotoRequest.getIdIntegrante() != null) {
            Integrante integrante = integranteRepository.findById(fotoRequest.getIdIntegrante())
                    .orElseThrow(() -> new RuntimeException("Integrante no encontrado"));
            nuevaFoto.setIntegrante(integrante);
        }

        return fotoRepository.save(nuevaFoto);
    }

    @Transactional()
    public List<Foto> crearFotosIntegrante(List<FotoRequest> fotoRequest, Integrante integrante) {
        List<Foto> nuevasFotos = fotoRequest.stream()
                .map(fotoReq -> {
                    Foto foto = new Foto();
                    foto.setContenido(fotoReq.getContenido());
                    foto.setTemporada(fotoReq.getTemporada());
                    foto.setIntegrante(integrante);
                    return foto;
                })
                .toList();

        return fotoRepository.saveAll(nuevasFotos);
    }

    @Transactional()
    public void eliminarFotosIntegrante(Integrante integrante) {
        fotoRepository.deleteAll(fotoRepository.findAllByIntegrante(integrante));
    }

    @Transactional()
    public List<Foto> crearFotosPublicacion(List<FotoRequest> fotoRequest, Publicacion publicacion) {
        List<Foto> nuevasFotos = FotoMapper.requestToFotosPublicacion(fotoRequest, publicacion);

        return fotoRepository.saveAll(nuevasFotos);
    }

    @Transactional()
    public void eliminarFotosPublicacion(Publicacion publicacion) {
        fotoRepository.deleteAll(fotoRepository.findAllByPublicacion(publicacion));
    }

    @Transactional()
    public List<Foto> crearFotosSeleccion(List<FotoRequest> fotos, Seleccion seleccion) {
        List<Foto> nuevasFotos = FotoMapper.requesToFotosSeleccion(fotos, seleccion);

        return fotoRepository.saveAll(nuevasFotos);
    }

    @Transactional()
    public void eliminarFotosSeleccion(Seleccion seleccion) {
        fotoRepository.deleteAll(fotoRepository.findAllBySeleccion(seleccion));
    }

    @Transactional()
    public void eliminarFoto(Foto foto) {
        fotoRepository.delete(foto);
    }
}
