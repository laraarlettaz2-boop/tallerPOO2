package elnino.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResponsableTecnico extends Persona {
    private String especialidad = "";
    private String numeroLicencia = "";
    private EquipoRespuesta equipo;
    private final List<EstacionMeteorologica> estacionesACargo = new ArrayList<>();
    private final List<Mantenimiento> mantenimientosRealizados = new ArrayList<>();

    public ResponsableTecnico() {
        super();
    }

    public ResponsableTecnico(String dni, String nombre, String apellido, String correoElectronico,
                              String celular, String especialidad, String numeroLicencia) {
        super(dni, nombre, apellido, correoElectronico, celular);
        this.especialidad = especialidad;
        this.numeroLicencia = numeroLicencia;
    }

    void agregarEstacionACargo(EstacionMeteorologica e) {
        if (!estacionesACargo.contains(e)) {
            estacionesACargo.add(e);
        }
    }

    void quitarEstacionACargo(EstacionMeteorologica e) {
        estacionesACargo.remove(e);
    }

    void agregarMantenimientoRealizado(Mantenimiento m) {
        mantenimientosRealizados.add(m);
    }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public String getNumeroLicencia() { return numeroLicencia; }
    public void setNumeroLicencia(String numeroLicencia) { this.numeroLicencia = numeroLicencia; }

    public EquipoRespuesta getEquipo() { return equipo; }
    public void setEquipo(EquipoRespuesta equipo) { this.equipo = equipo; }

    public List<EstacionMeteorologica> getEstacionesACargo() { return Collections.unmodifiableList(estacionesACargo); }
    public List<Mantenimiento> getMantenimientosRealizados() { return Collections.unmodifiableList(mantenimientosRealizados); }

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + especialidad + ")";
    }
}
