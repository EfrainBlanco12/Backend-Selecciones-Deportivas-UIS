package com.deporuis.auth.aplicacion;

import com.deporuis.auth.aplicacion.mapper.RolMapper;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.auth.infraestructura.RolRepository;
import com.deporuis.auth.infraestructura.dto.RolResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolQueryService {

    @Autowired
    private RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<RolResponse> obtenerRolesExceptoAdministrador() {
        List<Rol> roles = rolRepository.findAllExceptAdministrador();
        return roles.stream()
                .map(RolMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RolResponse> obtenerTodosLosRoles() {
        List<Rol> roles = rolRepository.findAll();
        return roles.stream()
                .map(RolMapper::toResponse)
                .collect(Collectors.toList());
    }
}
