package com.deporuis.auth.infraestructura;

import com.deporuis.auth.dominio.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    
    @Query("SELECT r FROM Rol r WHERE r.nombreRol != 'ADMINISTRADOR'")
    List<Rol> findAllExceptAdministrador();
}
