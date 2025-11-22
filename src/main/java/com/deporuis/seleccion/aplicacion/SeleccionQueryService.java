package com.deporuis.seleccion.aplicacion;

import com.deporuis.integrante.aplicacion.mapper.IntegranteMapper;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import com.deporuis.logro.aplicacion.mapper.LogroMapper;
import com.deporuis.logro.infraestructura.LogroRepository;
import com.deporuis.logro.infraestructura.dto.LogroResponse;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.aplicacion.mapper.SeleccionMapper;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.excepciones.SeleccionSinEventosRelacionadosException;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.deporuis.publicacion.aplicacion.mapper.PublicacionMapper;

import java.util.List;

@Service
public class SeleccionQueryService {

    @Autowired
    private SeleccionRepository seleccionRepository;

    @Autowired
    private SeleccionVerificarExistenciaService seleccionVerificarExistenciaService;

    @Autowired
    private IntegranteRepository integranteRepository;

    @Autowired
    private LogroRepository logroRepository;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Transactional(readOnly = true)
    public Page<SeleccionResponse> obtenerSeleccionesPaginadas(Integer page, Integer size) {
        // findByVisibilidadTrue tiene @EntityGraph: deporte, fotos, horarios.horario
        return seleccionRepository.findByVisibilidadTrue(PageRequest.of(page, size))
                .map(SeleccionMapper::seleccionToResponse);
    }

    @Transactional(readOnly = true)
    public SeleccionResponse obtenerSeleccion(Integer id) {
        // verificarSeleccion usa el repository; con nuestro @EntityGraph en findById,
        // las relaciones están cargadas para el mapper.
        Seleccion seleccion = seleccionVerificarExistenciaService.verificarSeleccion(id);
        return SeleccionMapper.seleccionToResponse(seleccion);
    }

    @Transactional(readOnly = true)
    public Page<IntegranteResponse> obtenerIntegrantesDeSeleccion(Integer idSeleccion, int page, int size) {
        seleccionVerificarExistenciaService.verificarSeleccion(idSeleccion);

        Pageable pageable = PageRequest.of(page, size, Sort.by("idIntegrante").ascending());
        Page<Integrante> pageEntities =
                integranteRepository.findByVisibilidadTrueAndSeleccion_IdSeleccion(idSeleccion, pageable);

        return pageEntities.map(IntegranteMapper::integranteToResponse);
    }

    @Transactional(readOnly = true)
    public Page<LogroResponse> obtenerLogrosDeSeleccion(Integer idSeleccion, int page, int size) {
        seleccionVerificarExistenciaService.verificarSeleccion(idSeleccion);
        var pageable = PageRequest.of(page, size, Sort.by("idLogro").descending());
        var pageEntities = logroRepository.findLogrosBySeleccion(idSeleccion, pageable);
        return pageEntities.map(LogroMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PublicacionResponse> obtenerUltimos3EventosPorSeleccion(Integer idSeleccion) {
        // 1) Lanza 404 si no existe
        seleccionVerificarExistenciaService.verificarSeleccion(idSeleccion);

        // 2) Top 3 por fecha desc
        var page = publicacionRepository.findBySeleccionAndTipoOrderByFechaDesc(
                idSeleccion,
                TipoPublicacion.EVENTO,
                PageRequest.of(0, 3)
        );

        if (page.isEmpty()) {
            // 3) Traemos el nombre para el mensaje (opcionalmente cacheable)
            String nombre = seleccionRepository.findById(idSeleccion)
                    .map(sel -> sel.getNombreSeleccion()) // ajusta getter si usa otro nombre de campo
                    .orElse(null);

            throw new SeleccionSinEventosRelacionadosException(idSeleccion, nombre);
        }

        // Si tus mappers son estáticos:
        return page.getContent().stream()
                .map(PublicacionMapper::toResponse)
                .toList();

        // Si tu mapper es bean:
        // return page.getContent().stream().map(publicacionMapper::toResponse).toList();
    }

}
