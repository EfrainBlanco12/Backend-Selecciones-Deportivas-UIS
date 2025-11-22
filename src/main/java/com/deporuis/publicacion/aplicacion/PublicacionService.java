package com.deporuis.publicacion.aplicacion;

import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicacionService {

    @Autowired
    private PublicacionCommandService publicacionCommandService;

    @Autowired
    private PublicacionQueryService publicacionQueryService;

    /**
     * Creacion de una publicacion y relaciones de las tablas muchos a muchos con las tablas fotos y selecciones
     */
    @Transactional()
    public PublicacionResponse crearPublicacion(PublicacionRequest publicacionRequest) {
        return publicacionCommandService.crearPublicacion(publicacionRequest);
    }

    /**
     * Devuelve una publicacion buscando por su id.
     */
    @Transactional(readOnly = true)
    public PublicacionResponse obtenerPublicacion(Integer id) {
        return publicacionQueryService.obtenerPublicacion(id);
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
        return publicacionQueryService.obtenerPublicacionesPaginadas(page, size);
    }

    /**
     * Actualiza una publicacion y sus relaciones muchos a muchos con las tablas selecciones y foto
     */
    @Transactional()
    public PublicacionResponse actualizarPublicacion(Integer id, PublicacionRequest request) {
        return publicacionCommandService.actualizarPublicacion(id, request);
    }

    /**
     * Elimina una publicacion tomando su id, busca las relaciones correspondientes y las elimina tambien
     */
    @Transactional()
    public void eliminarPublicacion(Integer id) {
        publicacionCommandService.eliminarPublicacion(id);
    }

    /**
     * Hace soft delete por su id
     */
    @Transactional()
    public void softDeletePublicacion(Integer id) {
        publicacionCommandService.softDeletePublicacion(id);
    }

    @Transactional(readOnly = true)
    public Page<PublicacionResponse> obtenerNoticiasPaginadas(Integer page, Integer size) {
        return publicacionQueryService.obtenerPublicacionesPorTipoPaginadas(TipoPublicacion.NOTICIA, page, size);
    }

    @Transactional(readOnly = true)
    public Page<PublicacionResponse> obtenerEventosPaginados(Integer page, Integer size) {
        return publicacionQueryService.obtenerPublicacionesPorTipoPaginadas(TipoPublicacion.EVENTO, page, size);
    }
}
