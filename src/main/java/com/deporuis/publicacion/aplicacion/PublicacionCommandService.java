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
        List<Foto> fotos = verificarExistenciaService.verificarFotos(fotosCreadas.stream().map(Foto::getIdFoto).toList());

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

//        List<Integer> idSelecciones = request.getSelecciones();
////        List<Integer> idFotos = request.getFotos();
//
//        List<Seleccion> nuevasSelecciones = verificarExistenciaService.verificarSelecciones(idSelecciones);
////        List<Foto> nuevasFotos = verificarExistenciaService.verificarFotos(idFotos);
//
//        List<SeleccionPublicacion> actualesSeleccion = seleccionPublicacionRepository.findAllByPublicacion(publicacion);
//        List<PublicacionFoto> actualesFotos = publicacionFotoRepository.findAllByPublicacion(publicacion);
//
//        Set<Integer> actualesIdsSeleccion = actualesSeleccion.stream()
//                .map(sp -> sp.getSeleccion().getIdSeleccion())
//                .collect(Collectors.toSet());
//        Set<Integer> nuevasIdsSeleccion = new HashSet<>(idSelecciones);
//
//        Set<Integer> actualesIdsFoto = actualesFotos.stream()
//                .map(pf -> pf.getFoto().getIdFoto())
//                .collect(Collectors.toSet());
////        Set<Integer> nuevasIdsFoto = new HashSet<>(idFotos);
//
//        List<SeleccionPublicacion> toRemoveSeleccion = actualesSeleccion.stream()
//                .filter(sp -> !nuevasIdsSeleccion.contains(sp.getSeleccion().getIdSeleccion()))
//                .collect(Collectors.toList());
//        seleccionPublicacionRepository.deleteAll(toRemoveSeleccion);
//
//        List<PublicacionFoto> toRemoveFoto = actualesFotos.stream()
////                .filter(pf -> !nuevasIdsFoto.contains(pf.getFoto().getIdFoto()))
//                .collect(Collectors.toList());
//        publicacionFotoRepository.deleteAll(toRemoveFoto);
//
//        List<Seleccion> seleccionesToAdd = nuevasSelecciones.stream()
//                .filter(s -> !actualesIdsSeleccion.contains(s.getIdSeleccion()))
//                .collect(Collectors.toList());
//        seleccionPublicacionRepository.saveAll(relacionService.crearRelacionesSeleccion(publicacion, seleccionesToAdd));
//
////        List<Foto> fotosToAdd = nuevasFotos.stream()
////                .filter(f -> !actualesIdsFoto.contains(f.getIdFoto()))
//                .collect(Collectors.toList());
//        publicacionFotoRepository.saveAll(relacionService.crearRelacionesFoto(publicacion, fotosToAdd));

        Publicacion actualizada = publicacionRepository.save(publicacion);
        return PublicacionMapper.toResponse(actualizada);
    }

    @Transactional()
    public void eliminarPublicacion(Integer id) {
        Publicacion publicacion = verificarExistenciaService.verificarPublicacion(id);

        relacionService.eliminarRelacionesSeleccion(publicacion);
//        publicacionFotoRepository.deleteAll(publicacionFotoRepository.findAllByPublicacion(publicacion));
        publicacionRepository.delete(publicacion);
    }

    @Transactional()
    public void softDeletePublicacion(Integer id) {
        Publicacion publicacion = verificarExistenciaService.verificarPublicacion(id);

        publicacion.setVisibilidad(false);
        publicacionRepository.save(publicacion);
    }
}
