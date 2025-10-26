package com.deporuis.Foto.aplicacion;

import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FotoService {

    @Autowired
    private FotoCommandService fotoCommandService;

    @Autowired
    private FotoQueryService fotoQueryService;

    @Transactional()
    public FotoResponse crearFoto(FotoRequest fotoRequest) {
        return fotoCommandService.crearFoto(fotoRequest);
    }

    @Transactional(readOnly = true)
    public Page<FotoResponse> obtenerFotosPaginadas(Integer page, Integer size) {
        return fotoQueryService.obtenerFotosPaginadas(page, size);
    }

    @Transactional(readOnly = true)
    public FotoResponse obtenerFoto(Integer id) {
        return fotoQueryService.obtenerFoto(id);
    }

    @Transactional(readOnly = true)
    public List<FotoResponse> obtenerFotosPorIntegrante(Integer idIntegrante) {
        return fotoQueryService.obtenerFotosPorIntegrante(idIntegrante);
    }

    @Transactional(readOnly = true)
    public List<FotoResponse> obtenerFotosPorSeleccion(Integer idSeleccion) {
        return fotoQueryService.obtenerFotosPorSeleccion(idSeleccion);
    }

    @Transactional()
    public FotoResponse actualizarFoto(Integer idFoto, FotoRequest fotoRequest) {
        return fotoCommandService.actualizarFoto(idFoto, fotoRequest);
    }

    @Transactional()
    public void eliminarFoto(Integer idFoto) {
        fotoCommandService.eliminarFotoPorId(idFoto);
    }
}
