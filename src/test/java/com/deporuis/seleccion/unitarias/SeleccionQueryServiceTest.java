package com.deporuis.seleccion.unitarias;

import com.deporuis.seleccion.aplicacion.SeleccionQueryService;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.aplicacion.mapper.SeleccionMapper;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeleccionQueryServiceTest {

    @InjectMocks private SeleccionQueryService service;
    @Mock private SeleccionRepository repository;
    @Mock private SeleccionVerificarExistenciaService verificar;

    @Test
    void obtenerSeleccionesPaginadas_mapeaConMapperEstatico() {
        Seleccion entidad = mock(Seleccion.class);
        when(repository.findByVisibilidadTrue(PageRequest.of(0, 2)))
                .thenReturn(new PageImpl<>(List.of(entidad)));

        try (MockedStatic<SeleccionMapper> mocked = Mockito.mockStatic(SeleccionMapper.class)) {
            mocked.when(() -> SeleccionMapper.seleccionToResponse(entidad))
                  .thenReturn(new SeleccionResponse());

            Page<SeleccionResponse> page = service.obtenerSeleccionesPaginadas(0, 2);
            assertEquals(1, page.getTotalElements());
        }

        verify(repository).findByVisibilidadTrue(PageRequest.of(0, 2));
        verifyNoMoreInteractions(repository, verificar);
    }

    @Test
    void obtenerSeleccion_mapeaConMapperEstatico() {
        Seleccion entidad = mock(Seleccion.class);
        when(verificar.verificarSeleccion(9)).thenReturn(entidad);

        try (MockedStatic<SeleccionMapper> mocked = Mockito.mockStatic(SeleccionMapper.class)) {
            SeleccionResponse esperado = new SeleccionResponse();
            mocked.when(() -> SeleccionMapper.seleccionToResponse(entidad))
                  .thenReturn(esperado);

            SeleccionResponse out = service.obtenerSeleccion(9);
            assertSame(esperado, out);
        }

        verify(verificar).verificarSeleccion(9);
        verifyNoMoreInteractions(repository, verificar);
    }
}
