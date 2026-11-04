package elnino.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import elnino.enums.EstadoOperativo;
import elnino.excepciones.LecturaInvalidaException;
import elnino.excepciones.OperacionNoPermitidaException;

public class Sensor {
    public static final double MAX_MM = 500.0;
    public static final double DIFERENCIA_ANOMALA_MM = 100.0;
    public static final int MESES_CALIBRACION = 12;

    private String idSensor = "";
    private String marca = "";
    private String modelo = "";
    private double precision;
    private EstadoOperativo estado = EstadoOperativo.OPERATIVO;
    private LocalDate ultimaCalibracion;
    private EstacionMeteorologica estacion;
    private final List<Medicion> mediciones = new ArrayList<>();
    private final List<Mantenimiento> mantenimientos = new ArrayList<>();

    public Sensor() {
    }

    public Sensor(String idSensor, String marca, String modelo, double precision,
                  EstadoOperativo estado, LocalDate ultimaCalibracion) {
        this.idSensor = idSensor;
        this.marca = marca;
        this.modelo = modelo;
        this.precision = precision;
        this.estado = estado;
        this.ultimaCalibracion = ultimaCalibracion;
    }

    public Medicion registrarMedicion(double mm, LocalDateTime fh)
            throws OperacionNoPermitidaException, LecturaInvalidaException {
        if (estacion == null) {
            throw new OperacionNoPermitidaException("El sensor " + idSensor + " no está instalado en ninguna estación");
        }
        if (estado != EstadoOperativo.OPERATIVO) {
            throw new OperacionNoPermitidaException("El sensor " + idSensor + " está " + estado);
        }
        if (!estacion.estaOperativa()) {
            throw new OperacionNoPermitidaException("La estación " + estacion.getNombre() + " está " + estacion.getEstado());
        }
        if (!validar(mm)) {
            throw new LecturaInvalidaException("Lectura fuera de rango (0 a " + MAX_MM + " mm): " + mm);
        }
        boolean anomala = !mediciones.isEmpty()
                && Math.abs(mm - mediciones.get(mediciones.size() - 1).getMilimetros()) > DIFERENCIA_ANOMALA_MM;
        Medicion m = new Medicion(idSensor + "-M" + (mediciones.size() + 1), fh, mm, anomala);
        m.setSensor(this);
        mediciones.add(m);
        return m;
    }

    public boolean validar(double mm) {
        return !Double.isNaN(mm) && mm >= 0 && mm <= MAX_MM;
    }

    public boolean requiereCalibracion() {
        return requiereCalibracion(LocalDate.now());
    }

    public boolean requiereCalibracion(LocalDate hoy) {
        return ultimaCalibracion == null || ultimaCalibracion.isBefore(hoy.minusMonths(MESES_CALIBRACION));
    }

    public double acumuladoEntre(LocalDateTime desde, LocalDateTime hasta) {
        double total = 0;
        for (Medicion m : mediciones) {
            if (m.getFechaHora().isAfter(desde) && !m.getFechaHora().isAfter(hasta)) {
                total += m.getMilimetros();
            }
        }
        return total;
    }

    public double acumuladoRango(LocalDateTime desde, LocalDateTime hasta) {
        double total = 0;
        for (Medicion m : mediciones) {
            if (!m.getFechaHora().isBefore(desde) && !m.getFechaHora().isAfter(hasta)) {
                total += m.getMilimetros();
            }
        }
        return total;
    }

    void agregarMantenimiento(Mantenimiento m) {
        mantenimientos.add(m);
    }

    public String getIdSensor() { return idSensor; }
    public void setIdSensor(String idSensor) { this.idSensor = idSensor; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public double getPrecision() { return precision; }
    public void setPrecision(double precision) { this.precision = precision; }
    public EstadoOperativo getEstado() { return estado; }
    public void setEstado(EstadoOperativo estado) { this.estado = estado; }
    public LocalDate getUltimaCalibracion() { return ultimaCalibracion; }
    public void setUltimaCalibracion(LocalDate ultimaCalibracion) { this.ultimaCalibracion = ultimaCalibracion; }

    public EstacionMeteorologica getEstacion() { return estacion; }
    public void setEstacion(EstacionMeteorologica estacion) { this.estacion = estacion; }

    public List<Medicion> getMediciones() { return Collections.unmodifiableList(mediciones); }
    public List<Mantenimiento> getMantenimientos() { return Collections.unmodifiableList(mantenimientos); }

    @Override
    public String toString() {
        return idSensor + " (" + marca + " " + modelo + ", " + estado + ", "
                + (estacion == null ? "sin instalar" : estacion.getNombre()) + ")";
    }
}
