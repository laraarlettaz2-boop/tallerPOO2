package elnino.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Medicion {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private String idMedicion = "";
    private LocalDateTime fechaHora = LocalDateTime.now();
    private double milimetros;
    private boolean anomalia;
    private Sensor sensor;

    public Medicion() {
    }

    public Medicion(String idMedicion, LocalDateTime fechaHora, double milimetros, boolean anomalia) {
        this.idMedicion = idMedicion;
        this.fechaHora = fechaHora;
        this.milimetros = milimetros;
        this.anomalia = anomalia;
    }

    public String getIdMedicion() { return idMedicion; }
    public void setIdMedicion(String idMedicion) { this.idMedicion = idMedicion; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public double getMilimetros() { return milimetros; }
    public void setMilimetros(double milimetros) { this.milimetros = milimetros; }
    public boolean isAnomalia() { return anomalia; }
    public void setAnomalia(boolean anomalia) { this.anomalia = anomalia; }
    public Sensor getSensor() { return sensor; }
    public void setSensor(Sensor sensor) { this.sensor = sensor; }

    @Override
    public String toString() {
        return fechaHora.format(FORMATO) + " - " + milimetros + " mm" + (anomalia ? " [ANÓMALA]" : "");
    }
}
