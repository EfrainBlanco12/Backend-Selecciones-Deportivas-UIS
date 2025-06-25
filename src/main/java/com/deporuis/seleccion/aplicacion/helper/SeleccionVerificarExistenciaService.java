package com.deporuis.seleccion.aplicacion.helper;

import com.deporuis.Foto.aplicacion.helper.FotoVerificarExistenciaService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.deporte.aplicacion.helper.DeporteVerificarExistenciaService;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.excepcion.common.BadRequestException;
import com.deporuis.horario.aplicacion.helper.HorarioVerificarExistenciaService;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeleccionVerificarExistenciaService {

    @Autowired
    private SeleccionRepository seleccionRepository;

    @Autowired
    private DeporteVerificarExistenciaService deporteVerificarExistenciaService;

    @Autowired
    private FotoVerificarExistenciaService fotoVerificarExistenciaService;

    @Autowired
    private HorarioVerificarExistenciaService horarioVerificarExistenciaService;

    public List<Seleccion> verificarSelecciones(List<Integer> idSelecciones) {
        if (idSelecciones.isEmpty()) {
            throw new BadRequestException("Debe haber al menos una seleccion");
        }
        List<Seleccion> selecciones = seleccionRepository.findAllById(idSelecciones);
        if (selecciones.size() != idSelecciones.size()) {
            throw new BadRequestException("Alguna seleccion no existe");
        }
        return selecciones;
    }

    public Deporte verificarDeporte(Integer idDeporte) {
        return deporteVerificarExistenciaService.verificarDeporte(idDeporte);
    }

    public List<Foto> verificarFotos(List<Foto> fotos) {
        return fotoVerificarExistenciaService.verificarFotos(fotos);
    }

    public List<Horario> verificarHorarios(List<Horario> horarios) {
        return horarioVerificarExistenciaService.verificarHorarios(horarios);
    }
}
