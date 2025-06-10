package com.deporuis.publicacion.aplicacion;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.excepcion.common.BadRequestException;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.dominio.PublicacionFoto;
import com.deporuis.publicacion.excepciones.PublicacionNotFoundException;
import com.deporuis.publicacion.infraestructura.PublicacionFotoRepository;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import com.deporuis.seleccion.infraestructura.SeleccionPublicacionRepository;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PublicacionService {

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private SeleccionPublicacionRepository seleccionPublicacionRepository;

    @Autowired
    private PublicacionFotoRepository publicacionFotoRepository;

    @Autowired
    private SeleccionRepository seleccionRepository;

    @Autowired
    private FotoRepository fotoRepository;

    /**
     * Creacion de una publicacion y relaciones de las tablas muchos a muchos con las tablas fotos y selecciones
     */
    @Transactional()
    public PublicacionResponse crearPublicacion(PublicacionRequest publicacionRequest) {

        Publicacion nuevaPublicacion = new Publicacion(
                publicacionRequest.getTitulo(),
                publicacionRequest.getDescripcion(),
                publicacionRequest.getLugar(),
                publicacionRequest.getFecha(),
                publicacionRequest.getDuracion(),
                publicacionRequest.getTipoPublicacion()
        );

        List<Seleccion> selecciones = verificarExistenciaSelecciones(publicacionRequest.getSelecciones());
        List<Foto> fotos = verificarExistenciaFotos(publicacionRequest.getFotos());

        List<SeleccionPublicacion> relacionSelecciones = selecciones.stream()
                .map(seleccion -> {
                    SeleccionPublicacion cs = new SeleccionPublicacion();
                    cs.setSeleccion(seleccion);
                    cs.setPublicacion(nuevaPublicacion);
                    return cs;
                })
                .collect(Collectors.toList());

        List<PublicacionFoto> relacionFotos = fotos.stream()
                .map(foto -> {
                    PublicacionFoto pf = new PublicacionFoto();
                    pf.setFoto(foto);
                    pf.setPublicacion(nuevaPublicacion);
                    return pf;
                })
                .collect(Collectors.toList());

        Publicacion publicacionGuardada = publicacionRepository.save(nuevaPublicacion);

        seleccionPublicacionRepository.saveAll(relacionSelecciones);
        publicacionFotoRepository.saveAll(relacionFotos);

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
        Page<Publicacion> publicaciones = publicacionRepository.findByVisibilidadTrue(PageRequest.of(page, size));
        return publicaciones.map(this::publicacionToResponse);
    }

    /**
     * Devuelve una publicacion buscando por su id.
     */
    @Transactional(readOnly = true)
    public PublicacionResponse obtenerPublicacion(Integer id) {
        Publicacion publicacion = verificarExistenciaPublicacion(id);
        return publicacionToResponse(publicacion);
    }

    /**
     * Actualiza una publicacion y sus relaciones muchos a muchos con las tablas selecciones y foto
     */
    @Transactional()
    public PublicacionResponse actualizarPublicacion(Integer id, PublicacionRequest request) {

        Publicacion publicacion = verificarExistenciaPublicacion(id);
        List<Integer> idSelecciones = request.getSelecciones();
        List<Integer> idFotos = request.getFotos();

        List<SeleccionPublicacion> seleccionPublicacionOld = seleccionPublicacionRepository.findAllByPublicacion(publicacion);
        List<PublicacionFoto> publicacionFotoOld = publicacionFotoRepository.findAllByPublicacion(publicacion);

        publicacion.setTitulo(request.getTitulo());
        publicacion.setDescripcion(request.getDescripcion());
        publicacion.setLugar(request.getLugar());
        publicacion.setFecha(request.getFecha());
        publicacion.setDuracion(request.getDuracion());

        List<Seleccion> selecciones = verificarExistenciaSelecciones(idSelecciones);
        List<Foto> fotos = verificarExistenciaFotos(idFotos);


        // Calcula la diferencia entre las relaciones a eliminar contra las que toca crear
        Set<Integer> actualesIdsSelecciones = seleccionPublicacionOld.stream()
                .map(sp -> sp.getSeleccion().getIdSeleccion())
                .collect(Collectors.toSet());
        Set<Integer> nuevosSetSeleccion  = new HashSet<>(idSelecciones);

        Set<Integer> actualesIdsFotos = publicacionFotoOld.stream()
                .map(pf -> pf.getFoto().getIdFoto())
                .collect(Collectors.toSet());
        Set<Integer> nuevosSetFoto  = new HashSet<>(idFotos);

        // Eliminar relaciones ya no necesarias
        List<SeleccionPublicacion> toDeleteSeleccion = seleccionPublicacionOld.stream()
                .filter(sp -> !nuevosSetSeleccion.contains(sp.getSeleccion().getIdSeleccion()))
                .collect(Collectors.toList());
        seleccionPublicacionRepository.deleteAll(toDeleteSeleccion);

        List<PublicacionFoto> toDeleteFoto = publicacionFotoOld.stream()
                .filter(pf -> !nuevosSetFoto.contains(pf.getFoto().getIdFoto()))
                .collect(Collectors.toList());
        publicacionFotoRepository.deleteAll(toDeleteFoto);

        // Crear relaciones nuevas
        List<Integer> toCreateSeleccion = nuevosSetSeleccion.stream()
                .filter(idSel -> !actualesIdsSelecciones.contains(idSel))
                .collect(Collectors.toList());

        List<Integer> toCreateFoto = nuevosSetFoto.stream()
                .filter(idFoto -> !actualesIdsFotos.contains(idFoto))
                .collect(Collectors.toList());

        if (!toCreateSeleccion.isEmpty()){
            List<Seleccion> seleccionesARelacionar = verificarExistenciaSelecciones(toCreateSeleccion);

            seleccionesARelacionar.forEach(s -> {
                SeleccionPublicacion sp = new SeleccionPublicacion();
                sp.setPublicacion(publicacion);
                sp.setSeleccion(s);

                seleccionPublicacionRepository.save(sp);
            });
        }

        if (!toCreateFoto.isEmpty()){
            List<Foto> fotosARelacionar = verificarExistenciaFotos(toCreateFoto);

            fotosARelacionar.forEach(f -> {
                PublicacionFoto pf = new PublicacionFoto();
                pf.setPublicacion(publicacion);
                pf.setFoto(f);

                publicacionFotoRepository.save(pf);
            });
        }

        Publicacion actualizada = publicacionRepository.save(publicacion);

        return publicacionToResponse(actualizada);
    }

    /**
     * Elimina una publicacion tomando su id, busca las relaciones correspondientes y las elimina tambien
     */
    @Transactional()
    public void eliminarPublicacion(Integer id) {
        Publicacion publicacion = verificarExistenciaPublicacion(id);

        List<SeleccionPublicacion> seleccionPublicacion = seleccionPublicacionRepository.findAllByPublicacion(publicacion);
        List<PublicacionFoto> publicacionFoto = publicacionFotoRepository.findAllByPublicacion(publicacion);

        if (!seleccionPublicacion.isEmpty()){
            seleccionPublicacionRepository.deleteAll(seleccionPublicacion);
        }
        if (!publicacionFoto.isEmpty()){
            publicacionFotoRepository.deleteAll(publicacionFoto);
        }
        publicacionRepository.delete(publicacion);
    }

    /**
     * Hace soft delete por su id
     */
    @Transactional()
    public void softDeletePublicacion(Integer id) {
        Publicacion publicacion = verificarExistenciaPublicacion(id);
        publicacion.setVisibilidad(false);

        publicacionRepository.save(publicacion);
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
        Optional<Publicacion> publicacionOptional = publicacionRepository.findById(id);

        if (publicacionOptional.isEmpty()) {
            throw  new PublicacionNotFoundException("No se encontró Publicación con ID = " + id);
        }

        Publicacion publicacion = publicacionOptional.get();

        if (!Boolean.TRUE.equals(publicacion.getVisibilidad())) {
            throw new PublicacionNotFoundException("La publicación no está disponible");
        }

        return publicacion;
    }

    private List<Seleccion> verificarExistenciaSelecciones(List<Integer> idSelecciones) {
        List<Seleccion> selecciones = seleccionRepository.findAllById(idSelecciones);

        if (idSelecciones.isEmpty()){
            throw new BadRequestException("Debe haber al menos una seleccion");
        }

        if (idSelecciones.size() != selecciones.size()){
            throw new BadRequestException("Alguna seleccion no existe");
        }

        return selecciones;
    }

    private List<Foto> verificarExistenciaFotos(List<Integer> idFotos) {
        List<Foto> fotos = fotoRepository.findAllById(idFotos);

        if (idFotos.isEmpty()){
            throw new BadRequestException("Debe haber al menos una foto");
        }

        if (idFotos.size() != fotos.size()){
            throw new BadRequestException("Alguna foto no existe");
        }

        return fotos;
    }
}
