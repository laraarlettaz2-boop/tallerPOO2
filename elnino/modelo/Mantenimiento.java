package elnino.modelo;

import java.time.LocalDate;

import elnino.enums.TipoMantenimiento;

public class Mantenimiento {
    private String idMantenimiento = "";
    private LocalDate fecha = LocalDate.now();
    private TipoMantenimiento tipo = TipoMantenimiento.PREVENTIVO;
    private String descripcion = "";
    private EstacionMeteorologica estacion;
    private Sensor sensor;
    private ResponsableTecnico responsable;

    public Mantenimiento() {
    }

    public Mantenimiento(String idMantenimiento, LocalDate fecha, TipoMantenimiento tipo, String descripcion,
                         EstacionMeteorologica estacion, Sensor sensor, ResponsableTecnico responsable) {
        this.idMantenimiento = idMantenimiento;
        this.fecha = fecha;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.estacion = estacion;
        this.sensor = sensor;
        this.responsable = responsable;
    }

    public String getIdMantenimiento() { return idMantenimiento; }
    public void setIdMantenimiento(String idMantenimiento) { this.idMantenimiento = idMantenimiento; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public TipoMantenimiento getTipo() { return tipo; }
    public void setTipo(TipoMantenimiento tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public EstacionMeteorologica getEstacion() { return estacion; }
    public void setEstacion(EstacionMeteorologica estacion) { this.estacion = estacion; }
    public Sensor getSensor() { return sensor; }
    public void setSensor(Sensor sensor) { this.sensor = sensor; }
    public ResponsableTecnico getResponsable() { return responsable; }
    public void setResponsable(ResponsableTecnico responsable) { this.responsable = responsable; }

    @Override
    public String toString() {
        return fecha + " " + tipo + " - " + descripcion
                + (sensor != null ? " (sensor " + sensor.getIdSensor() + ")" : " (estación completa)");
    }
}
