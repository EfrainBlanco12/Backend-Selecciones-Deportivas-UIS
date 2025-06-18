package com.deporuis.seleccion.infraestructura;

import com.deporuis.logro.dominio.Logro;
import com.deporuis.seleccion.dominio.SeleccionLogro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeleccionLogroRepository extends JpaRepository<SeleccionLogro, Integer> {
    SeleccionLogro findByLogro(Logro logro);
    List<SeleccionLogro> findAllByLogro(Logro logro);
}
