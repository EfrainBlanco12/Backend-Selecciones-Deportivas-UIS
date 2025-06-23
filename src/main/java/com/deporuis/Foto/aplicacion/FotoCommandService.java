package com.deporuis.Foto.aplicacion;

import com.deporuis.Foto.aplicacion.mapper.FotoMapper;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.publicacion.dominio.Publicacion;
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
}
