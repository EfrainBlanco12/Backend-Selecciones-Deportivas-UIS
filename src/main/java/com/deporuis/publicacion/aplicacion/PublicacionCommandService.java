package com.deporuis.publicacion.aplicacion;

import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.publicacion.aplicacion.helper.PublicacionRelacionService;
import com.deporuis.publicacion.aplicacion.helper.PublicacionVerificarExistenciaService;
import com.deporuis.publicacion.aplicacion.mapper.PublicacionMapper;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PublicacionCommandService {

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private PublicacionVerificarExistenciaService verificarExistenciaService;

    @Autowired
    private PublicacionRelacionService relacionService;

    @Autowired
    private FotoCommandService fotoCommandService;

    @Transactional()
    public PublicacionResponse crearPublicacion(PublicacionRequest request) {
        Publicacion publicacion = PublicacionMapper.requestToPublicacion(request);
        publicacion = publicacionRepository.save(publicacion);

        List<Seleccion> selecciones = verificarExistenciaService.verificarSelecciones(request.getSelecciones());
        List<SeleccionPublicacion> relacionesSeleccion = relacionService.crearRelacionesSeleccion(publicacion, selecciones);

        List<Foto> fotosCreadas = fotoCommandService.crearFotosPublicacion(request.getFotos(), publicacion);
        List<Foto> fotos = verificarExistenciaService.verificarFotos(fotosCreadas);

        publicacion.setSelecciones(relacionesSeleccion);
        publicacion.setFotos(fotos);

        return PublicacionMapper.toResponse(publicacion);
    }

    @Transactional()
    public PublicacionResponse actualizarPublicacion(Integer id, PublicacionRequest request) {
        Publicacion publicacion = verificarExistenciaService.verificarPublicacion(id);

        publicacion.setTitulo(request.getTitulo());
        publicacion.setDescripcion(request.getDescripcion());
        publicacion.setLugar(request.getLugar());
        publicacion.setFecha(request.getFecha());
        publicacion.setDuracion(request.getDuracion());
        publicacion.setTipoPublicacion(request.getTipoPublicacion());

        List<Integer> idSelecciones = request.getSelecciones();
        List<Seleccion> nuevasSelecciones = verificarExistenciaService.verificarSelecciones(idSelecciones);
        List<SeleccionPublicacion> relacionesSeleccion = relacionService.actualizarRelacionesSeleccion(publicacion, nuevasSelecciones, idSelecciones);

        fotoCommandService.eliminarFotosPublicacion(publicacion);
        List<Foto> nuevasFotos = fotoCommandService.crearFotosPublicacion(request.getFotos(), publicacion);
        List<Foto> fotos = verificarExistenciaService.verificarFotos(nuevasFotos);

        publicacion.setSelecciones(relacionesSeleccion);
        publicacion.setFotos(nuevasFotos);

        Publicacion actualizada = publicacionRepository.save(publicacion);
        return PublicacionMapper.toResponse(actualizada);
    }

    @Transactional()
    public void eliminarPublicacion(Integer id) {
        Publicacion publicacion = verificarExistenciaService.verificarPublicacion(id);

        relacionService.eliminarRelacionesSeleccion(publicacion);
        fotoCommandService.eliminarFotosPublicacion(publicacion);

        publicacionRepository.delete(publicacion);
    }

    @Transactional()
    public void softDeletePublicacion(Integer id) {
        Publicacion publicacion = verificarExistenciaService.verificarPublicacion(id);

        publicacion.setVisibilidad(false);
        publicacionRepository.save(publicacion);
    }
}
