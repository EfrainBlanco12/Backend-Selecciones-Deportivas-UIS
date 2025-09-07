// Ajusta el package al de tus excepciones de selección
package com.deporuis.seleccion.excepciones;

// Ajusta este import al package real de tu excepción base común
import com.deporuis.excepcion.common.ResourceNotFoundException;

public class SeleccionSinEventosRelacionadosException extends ResourceNotFoundException {
    public SeleccionSinEventosRelacionadosException(Integer idSeleccion, String nombreSeleccion) {
        super(
                (nombreSeleccion != null && !nombreSeleccion.isBlank())
                        ? "La selección '" + nombreSeleccion + "' no tiene eventos relacionados"
                        : "La selección con id " + idSeleccion + " no tiene eventos relacionados"
        );
    }
}
