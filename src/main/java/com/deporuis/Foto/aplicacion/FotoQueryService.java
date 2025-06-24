package com.deporuis.Foto.aplicacion;

import com.deporuis.Foto.aplicacion.helper.FotoVerificarExistenciaService;
import com.deporuis.Foto.aplicacion.mapper.FotoMapper;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.publicacion.dominio.Publicacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FotoQueryService {

    @Autowired
    private FotoRepository fotoRepository;

    @Autowired
    private FotoVerificarExistenciaService verificarExistenciaService;

    @Transactional(readOnly = true)
    public Page<FotoResponse> obtenerFotosPaginadas(Integer page, Integer size) {
        return fotoRepository.findAll(PageRequest.of(page, size))
                .map(FotoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public FotoResponse obtenerFoto(Integer id) {
        Foto foto = verificarExistenciaService.verificarFoto(id);
        return FotoMapper.toResponse(foto);
    }
}
