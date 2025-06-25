package com.deporuis.Foto.aplicacion;

import com.deporuis.Foto.aplicacion.mapper.FotoMapper;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.seleccion.dominio.Seleccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FotoCommandService {

    @Autowired
    private FotoRepository fotoRepository;

    @Transactional()
    public FotoResponse crearFoto(FotoRequest fotoRequest) {
        Foto nuevaFoto = FotoMapper.requestToFoto(fotoRequest);

        Foto fotoGuardada = fotoRepository.save(nuevaFoto);
        return FotoMapper.toResponse(fotoGuardada);
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
}
