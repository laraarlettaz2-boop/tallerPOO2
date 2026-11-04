package elnino.servicio;

import elnino.modelo.Alerta;
import elnino.modelo.Medicion;

public record ResultadoLectura(Medicion medicion, Alerta alerta) {
}
