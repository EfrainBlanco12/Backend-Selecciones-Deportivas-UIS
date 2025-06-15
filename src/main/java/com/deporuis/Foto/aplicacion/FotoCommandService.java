package com.deporuis.Foto.aplicacion;

import com.deporuis.Foto.aplicacion.mapper.FotoMapper;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FotoCommandService {

    @Autowired
    private FotoRepository fotoRepository;

    public FotoResponse crearFoto(FotoRequest fotoRequest) {
        Foto nuevaFoto = FotoMapper.requestToFoto(fotoRequest);

        Foto fotoGuardada = fotoRepository.save(nuevaFoto);
        return FotoMapper.toResponse(fotoGuardada);
    }
}
