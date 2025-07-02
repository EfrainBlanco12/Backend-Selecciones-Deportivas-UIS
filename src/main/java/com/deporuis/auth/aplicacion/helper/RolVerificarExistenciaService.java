package com.deporuis.auth.aplicacion.helper;

import com.deporuis.auth.dominio.Rol;
import com.deporuis.auth.infraestructura.RolRepository;
import com.deporuis.excepcion.common.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RolVerificarExistenciaService {

    @Autowired
    private RolRepository rolRepository;

    public Rol verificarRol(Integer id) {
        Optional<Rol> rol = rolRepository.findById(id);

        if (rol.isEmpty()) {
            throw new ResourceNotFoundException("Rol no existe");
        }

        return rol.get();
    }
}
