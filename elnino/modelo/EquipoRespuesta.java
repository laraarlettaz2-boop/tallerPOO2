package elnino.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import elnino.excepciones.OperacionNoPermitidaException;

public class EquipoRespuesta {
    private String idEquipo = "";
    private String nombreEquipo = "";
    private boolean disponible = true;
    private final List<ResponsableTecnico> integrantes = new ArrayList<>();
    private final List<Alerta> alertasAtendidas = new ArrayList<>();

    public EquipoRespuesta() {
    }

    public EquipoRespuesta(String idEquipo, String nombreEquipo) {
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
    }

    public void agregarIntegrante(ResponsableTecnico r) throws OperacionNoPermitidaException {
        if (r == null) {
            throw new IllegalArgumentException("El integrante no puede ser nulo");
        }
        if (r.getEquipo() != null) {
            throw new OperacionNoPermitidaException(
                    r.getNombreCompleto() + " ya integra el equipo " + r.getEquipo().getNombreEquipo());
        }
        integrantes.add(r);
        r.setEquipo(this);
    }

    public void quitarIntegrante(ResponsableTecnico r) {
        if (integrantes.remove(r)) {
            r.setEquipo(null);
        }
    }

    public boolean estaConformado() {
        return !integrantes.isEmpty();
    }

    void agregarAlertaAtendida(Alerta a) {
        if (!alertasAtendidas.contains(a)) {
            alertasAtendidas.add(a);
        }
    }

    public String getIdEquipo() { return idEquipo; }
    public void setIdEquipo(String idEquipo) { this.idEquipo = idEquipo; }
    public String getNombreEquipo() { return nombreEquipo; }
    public void setNombreEquipo(String nombreEquipo) { this.nombreEquipo = nombreEquipo; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public List<ResponsableTecnico> getIntegrantes() { return Collections.unmodifiableList(integrantes); }
    public List<Alerta> getAlertasAtendidas() { return Collections.unmodifiableList(alertasAtendidas); }

    @Override
    public String toString() {
        return nombreEquipo + " (" + integrantes.size() + " integrantes, "
                + (disponible ? "disponible" : "asignado") + ")";
    }
}
