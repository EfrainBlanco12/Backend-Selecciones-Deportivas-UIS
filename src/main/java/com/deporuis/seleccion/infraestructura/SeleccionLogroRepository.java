package com.deporuis.seleccion.infraestructura;

import com.deporuis.logro.dominio.Logro;
import com.deporuis.seleccion.dominio.SeleccionLogro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeleccionLogroRepository extends JpaRepository<SeleccionLogro, Integer> {
    SeleccionLogro findByLogro(Logro logro);
    List<SeleccionLogro> findAllByLogro(Logro logro);
}
