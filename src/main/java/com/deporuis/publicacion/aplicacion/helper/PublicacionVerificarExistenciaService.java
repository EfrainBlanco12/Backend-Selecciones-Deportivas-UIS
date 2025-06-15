package com.deporuis.publicacion.aplicacion.helper;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.excepcion.common.BadRequestException;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.excepciones.PublicacionNotFoundException;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PublicacionVerificarExistenciaService {

    @Autowired
    private SeleccionRepository seleccionRepository;

    @Autowired
    private FotoRepository fotoRepository;

    @Autowired
    private PublicacionRepository publicacionRepository;

    public List<Seleccion> verificarSelecciones(List<Integer> idSelecciones) {
        if (idSelecciones.isEmpty()) {
            throw new BadRequestException("Debe haber al menos una seleccion");
        }
        List<Seleccion> selecciones = seleccionRepository.findAllById(idSelecciones);
        if (selecciones.size() != idSelecciones.size()) {
            throw new BadRequestException("Alguna seleccion no existe");
        }
        return selecciones;
    }

    public List<Foto> verificarFotos(List<Integer> idFotos) {
        if (idFotos.isEmpty()) {
            throw new BadRequestException("Debe haber al menos una foto");
        }
        List<Foto> fotos = fotoRepository.findAllById(idFotos);
        if (fotos.size() != idFotos.size()) {
            throw new BadRequestException("Alguna foto no existe");
        }
        return fotos;
    }

    public Publicacion verificarPublicacion(Integer id) {
        Optional<Publicacion> publicacionOptional = publicacionRepository.findById(id);

        if (publicacionOptional.isEmpty()) {
            throw new PublicacionNotFoundException("No se encontró Publicación con ID = " + id);
        }

        Publicacion publicacion = publicacionOptional.get();

        if (!Boolean.TRUE.equals(publicacion.getVisibilidad())) {
            throw new PublicacionNotFoundException("La publicación no está disponible");
        }

        return publicacion;
    }
}
