package com.deporuis.publicacion.aplicacion;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import com.deporuis.seleccion.infraestructura.SeleccionPublicacionRepository;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PublicacionService {

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private SeleccionPublicacionRepository seleccionPublicacionRepository;

    @Autowired
    private SeleccionRepository seleccionRepository;

    public PublicacionResponse crearPublicacion(PublicacionRequest publicacionRequest) {
        //TODO: Mirar si hay excepciones

        Publicacion nuevaPublicacion = new Publicacion(
                publicacionRequest.getTitulo(),
                publicacionRequest.getDescripcion(),
                publicacionRequest.getLugar(),
                publicacionRequest.getFecha(),
                publicacionRequest.getDuracion(),
                publicacionRequest.getFoto()
        );

        List<Integer> idSelecciones = publicacionRequest.getSelecciones();
        List<Seleccion> selecciones = seleccionRepository.findAllById(idSelecciones);

        if (idSelecciones.isEmpty()){
            throw new Error();
        }

        if (idSelecciones.size() != selecciones.size()){
            throw new Error();
        }

        List<SeleccionPublicacion> relacionSelecciones = selecciones.stream()
                .map(seleccion -> {
                    SeleccionPublicacion cs = new SeleccionPublicacion();
                    cs.setSeleccion(seleccion);
                    cs.setPublicacion(nuevaPublicacion);
                    return cs;
                })
                .collect(Collectors.toList());

        Publicacion publicacionGuardada = publicacionRepository.save(nuevaPublicacion);

        seleccionPublicacionRepository.saveAll(relacionSelecciones);

        return publicacionToResponse(publicacionGuardada);

    }

    /**
     * Devuelve una página de publicaciones (para scroll).
     *
     * @param page Número de página (0-based).
     * @param size Cantidad de publicaciones por página.
     * @return Page<PublicacionResponse> con metadatos (totalPages, totalElements, etc.).
     */
    public Page<PublicacionResponse> obtenerPublicacionesPaginadas(int page, int size) {
        Page<Publicacion> publicaciones = publicacionRepository.findAll(PageRequest.of(page, size));
        // Mapear cada Publicacion → PublicacionResponse
        return publicaciones.map(this::publicacionToResponse);
    }

    public PublicacionResponse obtenerPublicacion(Integer id) {
        Publicacion publicacion = verificarExistenciaPublicacion(id);
        return publicacionToResponse(publicacion);
    }

    public PublicacionResponse actualizarPublicacion(Integer id, PublicacionRequest request) {
        Publicacion publicacion = verificarExistenciaPublicacion(id);

        publicacion.setTitulo(request.getTitulo());
        publicacion.setDescripcion(request.getDescripcion());
        publicacion.setLugar(request.getLugar());
        publicacion.setFecha(request.getFecha());
        publicacion.setDuracion(request.getDuracion());
        publicacion.setFoto(request.getFoto());

        List<SeleccionPublicacion> seleccionPublicacionOld = seleccionPublicacionRepository.findAllByPublicacion(publicacion);

        Publicacion actualizada = publicacionRepository.save(publicacion);

        return publicacionToResponse(actualizada);
    }

    public void eliminarPublicacion(Integer id) {
        Publicacion publicacion = verificarExistenciaPublicacion(id);

        List<SeleccionPublicacion> seleccionPublicacion = seleccionPublicacionRepository.findAllByPublicacion(publicacion);

        seleccionPublicacionRepository.deleteAll(seleccionPublicacion);
        publicacionRepository.delete(publicacion);
    }

    private PublicacionResponse publicacionToResponse(Publicacion p){
        return new PublicacionResponse(p.getIdPublicacion(), p.getTitulo());
    }

    private List<PublicacionResponse> publicacionesRequestToResponse(List<Publicacion> publicaciones){
        return publicaciones.stream()
                .map(p -> new PublicacionResponse(p.getIdPublicacion(), p.getTitulo()))
                .collect(Collectors.toList());
    }

    private Publicacion verificarExistenciaPublicacion(Integer id) {
        return publicacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró Publicación con ID = " + id));
    }
}
