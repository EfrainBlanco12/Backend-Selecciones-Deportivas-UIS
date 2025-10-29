package com.deporuis.auth.aplicacion;

import com.deporuis.auth.infraestructura.dto.RolResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RolService {

    @Autowired
    private RolQueryService rolQueryService;

    @Transactional(readOnly = true)
    public List<RolResponse> obtenerRolesExceptoAdministrador() {
        return rolQueryService.obtenerRolesExceptoAdministrador();
    }
}
