package elnino.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import elnino.enums.EstadoOperativo;
import elnino.enums.TipoEstacion;
import elnino.enums.TipoMantenimiento;
import elnino.excepciones.OperacionNoPermitidaException;

public class EstacionMeteorologica {
    private String idEstacion = "";
    private String nombre = "";
    private double latitud;
    private double longitud;
    private double altitud;
    private EstadoOperativo estado = EstadoOperativo.OPERATIVO;
    private TipoEstacion tipo = TipoEstacion.AUTOMATICA;
    private ZonaGeografica zona;
    private ResponsableTecnico responsable;
    private final List<Sensor> sensores = new ArrayList<>();
    private final List<Mantenimiento> mantenimientos = new ArrayList<>();

    public EstacionMeteorologica() {
    }

    public EstacionMeteorologica(String idEstacion, String nombre, double latitud, double longitud,
                                 double altitud, EstadoOperativo estado, TipoEstacion tipo,
                                 ZonaGeografica zona, ResponsableTecnico responsable) {
        this.idEstacion = idEstacion;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.altitud = altitud;
        this.estado = estado;
        this.tipo = tipo;
        setZona(zona);
        setResponsable(responsable);
    }

    public void instalarSensor(Sensor s) throws OperacionNoPermitidaException {
        if (s.getEstacion() != null) {
            throw new OperacionNoPermitidaException("El sensor " + s.getIdSensor()
                    + " ya está instalado en la estación " + s.getEstacion().getNombre()
                    + "; debe retirarse antes de instalarlo en otra");
        }
        sensores.add(s);
        s.setEstacion(this);
    }

    public void retirarSensor(Sensor s) throws OperacionNoPermitidaException {
        if (!sensores.remove(s)) {
            throw new OperacionNoPermitidaException("El sensor " + s.getIdSensor() + " no está en esta estación");
        }
        s.setEstacion(null);
    }

    public Mantenimiento registrarMantenimiento(String id, LocalDate fecha, TipoMantenimiento tipo,
                                                String descripcion, Sensor sensor,
                                                ResponsableTecnico realizadoPor)
            throws OperacionNoPermitidaException {
        if (sensor != null && !sensores.contains(sensor)) {
            throw new OperacionNoPermitidaException("El sensor " + sensor.getIdSensor() + " no pertenece a esta estación");
        }
        Mantenimiento m = new Mantenimiento(id, fecha, tipo, descripcion, this, sensor, realizadoPor);
        mantenimientos.add(m);
        if (sensor != null) {
            sensor.agregarMantenimiento(m);
            if (tipo == TipoMantenimiento.CALIBRACION) {
                sensor.setUltimaCalibracion(fecha);
            }
        }
        if (realizadoPor != null) {
            realizadoPor.agregarMantenimientoRealizado(m);
        }
        return m;
    }

    public boolean estaOperativa() {
        return estado == EstadoOperativo.OPERATIVO;
    }

    public double acumuladoEntre(LocalDateTime desde, LocalDateTime hasta) {
        double total = 0;
        for (Sensor s : sensores) {
            total += s.acumuladoEntre(desde, hasta);
        }
        return total;
    }

    public double acumuladoRango(LocalDateTime desde, LocalDateTime hasta) {
        double total = 0;
        for (Sensor s : sensores) {
            total += s.acumuladoRango(desde, hasta);
        }
        return total;
    }

    public String getIdEstacion() { return idEstacion; }
    public void setIdEstacion(String idEstacion) { this.idEstacion = idEstacion; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public double getAltitud() { return altitud; }
    public void setAltitud(double altitud) { this.altitud = altitud; }
    public EstadoOperativo getEstado() { return estado; }
    public void setEstado(EstadoOperativo estado) { this.estado = estado; }
    public TipoEstacion getTipo() { return tipo; }
    public void setTipo(TipoEstacion tipo) { this.tipo = tipo; }

    public ZonaGeografica getZona() { return zona; }
    public void setZona(ZonaGeografica zona) {
        if (this.zona != null) {
            this.zona.quitarEstacion(this);
        }
        this.zona = zona;
        if (zona != null) {
            zona.agregarEstacion(this);
        }
    }

    public ResponsableTecnico getResponsable() { return responsable; }
    public void setResponsable(ResponsableTecnico responsable) {
        if (this.responsable != null) {
            this.responsable.quitarEstacionACargo(this);
        }
        this.responsable = responsable;
        if (responsable != null) {
            responsable.agregarEstacionACargo(this);
        }
    }

    public List<Sensor> getSensores() { return Collections.unmodifiableList(sensores); }
    public List<Mantenimiento> getMantenimientos() { return Collections.unmodifiableList(mantenimientos); }

    @Override
    public String toString() {
        return idEstacion + " - " + nombre + " [" + tipo + ", " + estado + "]";
    }
}
