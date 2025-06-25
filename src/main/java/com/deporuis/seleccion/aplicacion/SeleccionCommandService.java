package com.deporuis.seleccion.aplicacion;

import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.horario.aplicacion.HorarioCommandService;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.seleccion.aplicacion.helper.SeleccionRelacionService;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.aplicacion.mapper.SeleccionMapper;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionHorario;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
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
    public SeleccionResponse crearSeleccion(SeleccionRequest request) {
        Seleccion seleccion = SeleccionMapper.requestToSeleccion(request);

        Deporte deporte = seleccionVerificarExistenciaService.verificarDeporte(request.getIdDeporte());
        seleccion.setDeporte(deporte);

        seleccion = seleccionRepository.save(seleccion);

        List<Foto> fotosCreadas = fotoCommandService.crearFotosSeleccion(request.getFotos(), seleccion);
        List<Foto> fotos = seleccionVerificarExistenciaService.verificarFotos(fotosCreadas);

        List<Horario> horariosCreados = horarioCommandService.crearHorariosSeleccion(request.getHorarios());
        List<Horario> horarios = seleccionVerificarExistenciaService.verificarHorarios(horariosCreados);
        List<SeleccionHorario> relacionesHorario = seleccionRelacionService.crearRelacionesHorarios(seleccion, horarios);

        seleccion.setFotos(fotos);
        seleccion.setHorarios(relacionesHorario);

        return SeleccionMapper.seleccionToResponse(seleccion);
    }
}
