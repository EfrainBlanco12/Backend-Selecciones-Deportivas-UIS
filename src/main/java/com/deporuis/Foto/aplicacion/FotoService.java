package com.deporuis.Foto.aplicacion;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.excepciones.FotoNotFoundException;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FotoService {

    @Autowired
    private FotoRepository fotoRepository;

    @Transactional()
    public FotoResponse crearFoto(FotoRequest fotoRequest) {
        Foto nuevaFoto = fotoRequestToFoto(fotoRequest);

        Foto fotoGuardada = fotoRepository.save(nuevaFoto);
        return fotoToResponse(fotoGuardada);
    }

    @Transactional(readOnly = true)
    public Page<FotoResponse> obtenerFotosPaginadas(Integer page, Integer size) {
        Page<Foto> fotos = fotoRepository.findAll(PageRequest.of(page, size));
        return fotos.map(this::fotoToResponse);
    }

    @Transactional(readOnly = true)
    public FotoResponse obtenerFoto(Integer id) {
        Foto foto = verificarExistenciaFoto(id);
        return fotoToResponse(foto);
    }

    private FotoResponse fotoToResponse(Foto foto) {
        return new FotoResponse(
                foto.getIdFoto(),
                foto.getContenido(),
                foto.getTemporada()
        );
    }

    private Foto fotoRequestToFoto(FotoRequest fotoRequest) {
        return new Foto(
                fotoRequest.getContenido(),
                fotoRequest.getTemporada()
        );
    }

    private Foto verificarExistenciaFoto(Integer id) {
        Optional<Foto> fotoOptional = fotoRepository.findById(id);

        if (fotoOptional.isEmpty()) {
            throw new FotoNotFoundException("No se encontro Foto con ID = " + id);
        }

        return fotoOptional.get();
    }
}
