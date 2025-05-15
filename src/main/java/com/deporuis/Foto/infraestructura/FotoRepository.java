package com.deporuis.Foto.infraestructura;

import com.deporuis.Foto.dominio.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoRepository extends JpaRepository<Foto,Integer> {
}
