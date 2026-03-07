package com.deporuis.seleccion.aplicacion;

import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.horario.aplicacion.HorarioCommandService;
import com.deporuis.horario.dominio.Horario;
import java.time.LocalDateTime;
import com.deporuis.seleccion.aplicacion.helper.SeleccionRelacionService;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.aplicacion.mapper.SeleccionMapper;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionHorario;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import com.deporuis.seleccion.infraestructura.dto.SeleccionPatchRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeleccionCommandService {

    @Autowired
    private SeleccionVerificarExistenciaService seleccionVerificarExistenciaService;

    @Autowired
    private SeleccionRepository seleccionRepository;

    @Autowired
    private FotoCommandService fotoCommandService;

    @Autowired
    private HorarioCommandService horarioCommandService;

    @Autowired
    private SeleccionRelacionService seleccionRelacionService;

    @Transactional()
    public SeleccionResponse crearSeleccion(SeleccionRequest request, Integer usuarioModifico) {
        Seleccion seleccion = SeleccionMapper.requestToSeleccion(request);

        Deporte deporte = seleccionVerificarExistenciaService.verificarDeporte(request.getIdDeporte());
        seleccion.setDeporte(deporte);

        seleccion.setUsuarioModifico(usuarioModifico);
        seleccion.setFechaModificacion(LocalDateTime.now());

        seleccion = seleccionRepository.save(seleccion);

        List<Foto> fotosCreadas = fotoCommandService.crearFotosSeleccion(request.getFotos(), seleccion);
        List<Foto> fotos = seleccionVerificarExistenciaService.verificarFotos(fotosCreadas);

        List<Horario> horariosSeleccion = horarioCommandService.obtenerOcrearHorariosSeleccion(request.getHorarios());
        List<Horario> horarios = seleccionVerificarExistenciaService.verificarHorarios(horariosSeleccion);
        List<SeleccionHorario> relacionesHorario = seleccionRelacionService.crearRelacionesHorarios(seleccion, horarios);

        seleccion.setFotos(fotos);
        seleccion.setHorarios(relacionesHorario);

        // Con el nuevo mapper, esto ya devuelve objetos relacionados completos
        return SeleccionMapper.seleccionToResponse(seleccion);
    }

    @Transactional()
    public SeleccionResponse actualizarSeleccion(Integer id, SeleccionRequest seleccionRequest, Integer usuarioModifico) {
        Seleccion seleccion = seleccionVerificarExistenciaService.verificarSeleccion(id);

        seleccion.setFechaCreacion(seleccionRequest.getFechaCreacion());
        seleccion.setNombreSeleccion(seleccionRequest.getNombreSeleccion());
        seleccion.setEspacioDeportivo(seleccionRequest.getEspacioDeportivo());
        seleccion.setEquipo(seleccionRequest.getEquipo());
        seleccion.setTipo_seleccion(seleccionRequest.getTipo_seleccion());

        Deporte deporte = seleccionVerificarExistenciaService.verificarDeporte(seleccionRequest.getIdDeporte());
        seleccion.setDeporte(deporte);

        seleccion.setUsuarioModifico(usuarioModifico);
        seleccion.setFechaModificacion(LocalDateTime.now());

        // Fotos
        fotoCommandService.eliminarFotosSeleccion(seleccion);
        List<Foto> nuevasFotos = fotoCommandService.crearFotosSeleccion(seleccionRequest.getFotos(), seleccion);
        nuevasFotos = seleccionVerificarExistenciaService.verificarFotos(nuevasFotos);
        seleccion.setFotos(nuevasFotos);

        // Horarios
        seleccionRelacionService.eliminarRelacionesSeleccion(seleccion);
        List<Horario> nuevosHorarios = horarioCommandService.obtenerOcrearHorariosSeleccion(seleccionRequest.getHorarios());
        nuevosHorarios = seleccionVerificarExistenciaService.verificarHorarios(nuevosHorarios);
        List<SeleccionHorario> relacionesHorario = seleccionRelacionService.crearRelacionesHorarios(seleccion, nuevosHorarios);
        seleccion.setHorarios(relacionesHorario);

        Seleccion seleccionActualizada = seleccionRepository.save(seleccion);

        // Con el nuevo mapper, esto ya devuelve objetos relacionados completos
        return SeleccionMapper.seleccionToResponse(seleccionActualizada);
    }

    @Transactional()
    public void eliminarSeleccion(Integer id) {
        Seleccion seleccion = seleccionVerificarExistenciaService.verificarSeleccion(id);
        seleccionRelacionService.eliminarRelacionesSeleccion(seleccion);
        fotoCommandService.eliminarFotosSeleccion(seleccion);
        seleccionRepository.delete(seleccion);
    }

    @Transactional()
    public void softDeleteSeleccion(Integer id) {
        Seleccion seleccion = seleccionVerificarExistenciaService.verificarSeleccion(id);
        seleccion.setVisibilidad(false);
        seleccionRepository.save(seleccion);
    }

    @Transactional()
    public SeleccionResponse actualizarSeleccionParcial(Integer id, SeleccionPatchRequest patchRequest, Integer usuarioModifico) {
        Seleccion seleccion = seleccionVerificarExistenciaService.verificarSeleccion(id);

        seleccion.setUsuarioModifico(usuarioModifico);
        seleccion.setFechaModificacion(LocalDateTime.now());

        // Actualizar solo los campos que vienen en el request
        if (patchRequest.getFechaCreacion() != null) {
            seleccion.setFechaCreacion(patchRequest.getFechaCreacion());
        }

        if (patchRequest.getNombreSeleccion() != null) {
            seleccion.setNombreSeleccion(patchRequest.getNombreSeleccion());
        }

        if (patchRequest.getEspacioDeportivo() != null) {
            seleccion.setEspacioDeportivo(patchRequest.getEspacioDeportivo());
        }

        if (patchRequest.getEquipo() != null) {
            seleccion.setEquipo(patchRequest.getEquipo());
        }

        if (patchRequest.getTipo_seleccion() != null) {
            seleccion.setTipo_seleccion(patchRequest.getTipo_seleccion());
        }

        // Actualizar deporte si viene en el request
        if (patchRequest.getIdDeporte() != null) {
            Deporte deporte = seleccionVerificarExistenciaService.verificarDeporte(patchRequest.getIdDeporte());
            seleccion.setDeporte(deporte);
        }

        // Actualizar fotos solo si vienen en el request
        if (patchRequest.getFotos() != null) {
            fotoCommandService.eliminarFotosSeleccion(seleccion);
            List<Foto> nuevasFotos = fotoCommandService.crearFotosSeleccion(patchRequest.getFotos(), seleccion);
            nuevasFotos = seleccionVerificarExistenciaService.verificarFotos(nuevasFotos);
            seleccion.setFotos(nuevasFotos);
        }

        // Actualizar horarios solo si vienen en el request
        if (patchRequest.getHorarios() != null) {
            seleccionRelacionService.eliminarRelacionesSeleccion(seleccion);
            List<Horario> nuevosHorarios = horarioCommandService.obtenerOcrearHorariosSeleccion(patchRequest.getHorarios());
            nuevosHorarios = seleccionVerificarExistenciaService.verificarHorarios(nuevosHorarios);
            List<SeleccionHorario> relacionesHorario = seleccionRelacionService.crearRelacionesHorarios(seleccion, nuevosHorarios);
            seleccion.setHorarios(relacionesHorario);
        }

        Seleccion seleccionActualizada = seleccionRepository.save(seleccion);

        return SeleccionMapper.seleccionToResponse(seleccionActualizada);
    }
}
