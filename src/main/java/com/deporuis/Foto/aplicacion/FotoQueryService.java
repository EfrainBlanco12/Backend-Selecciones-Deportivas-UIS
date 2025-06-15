package com.deporuis.Foto.aplicacion;

import com.deporuis.Foto.aplicacion.helper.FotoVerificarExistenciaService;
import com.deporuis.Foto.aplicacion.mapper.FotoMapper;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class FotoQueryService {

    @Autowired
    private FotoRepository fotoRepository;

    @Autowired
    private FotoVerificarExistenciaService verificarExistenciaService;

    public Page<FotoResponse> obtenerFotosPaginadas(Integer page, Integer size) {
        return fotoRepository.findAll(PageRequest.of(page, size))
                .map(FotoMapper::toResponse);
    }

    public FotoResponse obtenerFoto(Integer id) {
        Foto foto = verificarExistenciaService.verificarFoto(id);
        return FotoMapper.toResponse(foto);
    }
}
