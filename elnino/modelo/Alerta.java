package elnino.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import elnino.enums.NivelAlerta;
import elnino.enums.TipoEventoClimatico;
import elnino.excepciones.OperacionNoPermitidaException;

public class Alerta {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private String idAlerta = "";
    private LocalDateTime fechaEmision = LocalDateTime.now();
    private NivelAlerta nivel = NivelAlerta.VERDE;
    private TipoEventoClimatico evento = TipoEventoClimatico.LLUVIA_COPIOSA;
    private String descripcion = "";
    private boolean atendida;
    private ZonaGeografica zona;
    private EquipoRespuesta equipo;

    public Alerta() {
    }

    public Alerta(String idAlerta, LocalDateTime fechaEmision, NivelAlerta nivel,
                  TipoEventoClimatico evento, String descripcion) {
        this.idAlerta = idAlerta;
        this.fechaEmision = fechaEmision;
        this.nivel = nivel;
        this.evento = evento;
        this.descripcion = descripcion;
    }

    public void asignarEquipo(EquipoRespuesta e) throws OperacionNoPermitidaException {
        if (!requiereEquipo()) {
            throw new OperacionNoPermitidaException("Una alerta de nivel " + nivel + " no requiere equipo de respuesta");
        }
        if (!e.estaConformado()) {
            throw new OperacionNoPermitidaException("El equipo " + e.getNombreEquipo() + " no tiene integrantes");
        }
        if (!e.isDisponible()) {
            throw new OperacionNoPermitidaException("El equipo " + e.getNombreEquipo() + " no está disponible");
        }
        this.equipo = e;
        this.atendida = true;
        e.setDisponible(false);
        e.agregarAlertaAtendida(this);
    }

    public boolean requiereEquipo() {
        return nivel.requiereEquipo();
    }

    public boolean estaPendiente() {
        return requiereEquipo() && equipo == null;
    }

    public String getIdAlerta() { return idAlerta; }
    public void setIdAlerta(String idAlerta) { this.idAlerta = idAlerta; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
    public NivelAlerta getNivel() { return nivel; }
    public void setNivel(NivelAlerta nivel) { this.nivel = nivel; }
    public TipoEventoClimatico getEvento() { return evento; }
    public void setEvento(TipoEventoClimatico evento) { this.evento = evento; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isAtendida() { return atendida; }
    public void setAtendida(boolean atendida) { this.atendida = atendida; }
    public ZonaGeografica getZona() { return zona; }
    public void setZona(ZonaGeografica zona) { this.zona = zona; }
    public EquipoRespuesta getEquipo() { return equipo; }
    public void setEquipo(EquipoRespuesta equipo) { this.equipo = equipo; }

    @Override
    public String toString() {
        return idAlerta + " [" + nivel + "] " + evento + " - " + (zona != null ? zona.getNombre() + " - " : "")
                + fechaEmision.format(FORMATO) + " - " + descripcion
                + (estaPendiente() ? " (SIN EQUIPO ASIGNADO)" : (equipo != null ? " (atiende " + equipo.getNombreEquipo() + ")" : ""));
    }
}
