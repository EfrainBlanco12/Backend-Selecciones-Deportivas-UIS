package com.deporuis.publicacion.aplicacion.helper;

import com.deporuis.Foto.aplicacion.helper.FotoVerificarExistenciaService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.excepcion.common.BadRequestException;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.excepciones.PublicacionNotFoundException;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PublicacionVerificarExistenciaService {

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private SeleccionVerificarExistenciaService seleccionVerificarExistenciaService;

    @Autowired
    private FotoVerificarExistenciaService fotoVerificarExistenciaService;

    public List<Seleccion> verificarSelecciones(List<Integer> idSelecciones) {
        return seleccionVerificarExistenciaService.verificarSelecciones(idSelecciones);
    }

    public List<Foto> verificarFotos(List<Foto> fotos) {
        return fotoVerificarExistenciaService.verificarFotos(fotos);
    }

    @Transactional(readOnly = true)
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
