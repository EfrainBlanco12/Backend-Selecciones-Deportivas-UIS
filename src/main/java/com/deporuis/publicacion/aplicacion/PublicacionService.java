package com.deporuis.publicacion.aplicacion;

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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PublicacionService {

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private SeleccionPublicacionRepository seleccionPublicacionRepository;

    @Autowired
    private SeleccionRepository seleccionRepository;

    @Transactional()
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

        List<Seleccion> selecciones = verificarExistenciaSelecciones(publicacionRequest.getSelecciones());

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
     * @return Page<PublicacionResponse> con metadatos.
     */
    @Transactional(readOnly = true)
    public Page<PublicacionResponse> obtenerPublicacionesPaginadas(int page, int size) {
        Page<Publicacion> publicaciones = publicacionRepository.findAll(PageRequest.of(page, size));
        // Mapear cada Publicacion → PublicacionResponse
        return publicaciones.map(this::publicacionToResponse);
    }

    @Transactional(readOnly = true)
    public PublicacionResponse obtenerPublicacion(Integer id) {
        Publicacion publicacion = verificarExistenciaPublicacion(id);
        return publicacionToResponse(publicacion);
    }

    @Transactional()
    public PublicacionResponse actualizarPublicacion(Integer id, PublicacionRequest request) {
        Publicacion publicacion = verificarExistenciaPublicacion(id);

        publicacion.setTitulo(request.getTitulo());
        publicacion.setDescripcion(request.getDescripcion());
        publicacion.setLugar(request.getLugar());
        publicacion.setFecha(request.getFecha());
        publicacion.setDuracion(request.getDuracion());
        publicacion.setFoto(request.getFoto());

        List<Seleccion> selecciones = verificarExistenciaSelecciones(request.getSelecciones());

        List<SeleccionPublicacion> seleccionPublicacionOld = seleccionPublicacionRepository.findAllByPublicacion(publicacion);

        // Calcula la diferencia entre las relaciones a eliminar contra las que toca crear
        Set<Integer> actualesIds = seleccionPublicacionOld.stream()
                .map(sp -> sp.getSeleccion().getIdSeleccion())
                .collect(Collectors.toSet());
        Set<Integer> nuevosSet  = new HashSet<>(request.getSelecciones());

        // Eliminar relaciones ya no necesarias
        List<SeleccionPublicacion> toDelete = seleccionPublicacionOld.stream()
                .filter(sp -> !nuevosSet.contains(sp.getSeleccion().getIdSeleccion()))
                .collect(Collectors.toList());
        seleccionPublicacionRepository.deleteAll(toDelete);

        // Crear relaciones nuevas
        List<Integer> toCreate = nuevosSet.stream()
                .filter(idSel -> !actualesIds.contains(idSel))
                .collect(Collectors.toList());

        List<Seleccion> seleccionesARelacionar = verificarExistenciaSelecciones(toCreate);

        seleccionesARelacionar.forEach(s -> {
            SeleccionPublicacion sp = new SeleccionPublicacion();
            sp.setPublicacion(publicacion);
            sp.setSeleccion(s);

            seleccionPublicacionRepository.save(sp);
        });


        Publicacion actualizada = publicacionRepository.save(publicacion);

        return publicacionToResponse(actualizada);
    }

    @Transactional()
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

    private List<Seleccion> verificarExistenciaSelecciones(List<Integer> idSelecciones) {
        List<Seleccion> selecciones = seleccionRepository.findAllById(idSelecciones);

        if (idSelecciones.isEmpty()){
            throw new Error();
        }

        if (idSelecciones.size() != selecciones.size()){
            throw new Error();
        }

        return selecciones;
    }
}
