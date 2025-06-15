package com.deporuis.Foto.aplicacion.helper;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.excepciones.FotoNotFoundException;
import com.deporuis.Foto.infraestructura.FotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FotoVerificarExistenciaService {

    @Autowired
    private FotoRepository fotoRepository;

    public Foto verificarFoto(Integer id) {
        Optional<Foto> fotoOptional = fotoRepository.findById(id);

        if (fotoOptional.isEmpty()) {
            throw new FotoNotFoundException("No se encontro Foto con ID = " + id);
        }

        return fotoOptional.get();
    }
}
