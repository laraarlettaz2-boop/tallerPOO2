package elnino.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import elnino.enums.NivelAlerta;
import elnino.enums.NivelVulnerabilidad;
import elnino.enums.TipoEventoClimatico;

public class ZonaGeografica {
    public static final double UMBRAL_AMARILLA_OFICIAL = 50;
    public static final double UMBRAL_NARANJA_OFICIAL = 100;
    public static final double UMBRAL_ROJA_OFICIAL = 150;

    private String idZona = "";
    private String nombre = "";
    private NivelVulnerabilidad nivelVulnerabilidad = NivelVulnerabilidad.BAJO;
    private double umbralAmarilla = UMBRAL_AMARILLA_OFICIAL;
    private double umbralNaranja = UMBRAL_NARANJA_OFICIAL;
    private double umbralRoja = UMBRAL_ROJA_OFICIAL;
    private NivelAlerta nivelAlertaActual = NivelAlerta.VERDE;
    private final List<EstacionMeteorologica> estaciones = new ArrayList<>();
    private final List<ValorHistorico> valoresHistoricos = new ArrayList<>();
    private final List<Alerta> alertas = new ArrayList<>();

    public ZonaGeografica() {
    }

    public ZonaGeografica(String idZona, String nombre, NivelVulnerabilidad nivelVulnerabilidad) {
        this.idZona = idZona;
        this.nombre = nombre;
        this.nivelVulnerabilidad = nivelVulnerabilidad;
    }

    public ZonaGeografica(String idZona, String nombre, NivelVulnerabilidad nivelVulnerabilidad,
                          double umbralAmarilla, double umbralNaranja, double umbralRoja) {
        this.idZona = idZona;
        this.nombre = nombre;
        this.nivelVulnerabilidad = nivelVulnerabilidad;
        configurarUmbrales(umbralAmarilla, umbralNaranja, umbralRoja);
    }

    public void configurarUmbrales(double amarilla, double naranja, double roja) {
        if (amarilla <= 0 || !(amarilla < naranja && naranja < roja)) {
            throw new IllegalArgumentException("Los umbrales deben cumplir 0 < amarilla < naranja < roja");
        }
        this.umbralAmarilla = amarilla;
        this.umbralNaranja = naranja;
        this.umbralRoja = roja;
    }

    public void usarUmbralesOficiales() {
        configurarUmbrales(UMBRAL_AMARILLA_OFICIAL, UMBRAL_NARANJA_OFICIAL, UMBRAL_ROJA_OFICIAL);
    }

    void agregarEstacion(EstacionMeteorologica e) {
        if (!estaciones.contains(e)) {
            estaciones.add(e);
        }
    }

    void quitarEstacion(EstacionMeteorologica e) {
        estaciones.remove(e);
    }

    public void agregarValorHistorico(int mes, double acumuladoMedio) {
        ValorHistorico nuevo = new ValorHistorico(mes, acumuladoMedio);
        nuevo.setZona(this);
        valoresHistoricos.removeIf(v -> v.getMes() == mes);
        valoresHistoricos.add(nuevo);
    }

    public Optional<ValorHistorico> getValorHistorico(int mes) {
        return valoresHistoricos.stream().filter(v -> v.getMes() == mes).findFirst();
    }

    public double lluviaAcumulada24h() {
        return lluviaAcumulada24h(LocalDateTime.now());
    }

    public double lluviaAcumulada24h(LocalDateTime referencia) {
        double total = 0;
        for (EstacionMeteorologica e : estaciones) {
            total += e.acumuladoEntre(referencia.minusHours(24), referencia);
        }
        return total;
    }

    public NivelAlerta evaluarRiesgo() {
        return evaluarRiesgo(LocalDateTime.now());
    }

    public NivelAlerta evaluarRiesgo(LocalDateTime referencia) {
        double mm = lluviaAcumulada24h(referencia);
        NivelAlerta nivel;
        if (mm >= umbralRoja) {
            nivel = NivelAlerta.ROJA;
        } else if (mm >= umbralNaranja) {
            nivel = NivelAlerta.NARANJA;
        } else if (mm >= umbralAmarilla) {
            nivel = NivelAlerta.AMARILLA;
        } else {
            nivel = NivelAlerta.VERDE;
        }
        nivelAlertaActual = nivel;
        return nivel;
    }

    public Alerta evaluarYAlertar(LocalDateTime referencia) {
        NivelAlerta anterior = nivelAlertaActual;
        NivelAlerta nuevo = evaluarRiesgo(referencia);
        if (nuevo.ordinal() > anterior.ordinal()) {
            return emitirAlerta(TipoEventoClimatico.LLUVIA_COPIOSA, nuevo,
                    String.format("Acumulado en 24 h: %.1f mm", lluviaAcumulada24h(referencia)), referencia);
        }
        return null;
    }

    public Alerta emitirAlerta(TipoEventoClimatico ev) {
        return emitirAlerta(ev, nivelAlertaActual, "Alerta por " + ev, LocalDateTime.now());
    }

    public Alerta emitirAlerta(TipoEventoClimatico ev, NivelAlerta nivel, String descripcion) {
        return emitirAlerta(ev, nivel, descripcion, LocalDateTime.now());
    }

    public Alerta emitirAlerta(TipoEventoClimatico ev, NivelAlerta nivel, String descripcion, LocalDateTime fecha) {
        Alerta a = new Alerta(idZona + "-A" + (alertas.size() + 1), fecha, nivel, ev, descripcion);
        a.setZona(this);
        alertas.add(a);
        return a;
    }

    public String getIdZona() { return idZona; }
    public void setIdZona(String idZona) { this.idZona = idZona; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public NivelVulnerabilidad getNivelVulnerabilidad() { return nivelVulnerabilidad; }
    public void setNivelVulnerabilidad(NivelVulnerabilidad nivelVulnerabilidad) { this.nivelVulnerabilidad = nivelVulnerabilidad; }
    public double getUmbralAmarilla() { return umbralAmarilla; }
    public void setUmbralAmarilla(double v) { configurarUmbrales(v, umbralNaranja, umbralRoja); }
    public double getUmbralNaranja() { return umbralNaranja; }
    public void setUmbralNaranja(double v) { configurarUmbrales(umbralAmarilla, v, umbralRoja); }
    public double getUmbralRoja() { return umbralRoja; }
    public void setUmbralRoja(double v) { configurarUmbrales(umbralAmarilla, umbralNaranja, v); }
    public NivelAlerta getNivelAlertaActual() { return nivelAlertaActual; }
    public void setNivelAlertaActual(NivelAlerta nivelAlertaActual) { this.nivelAlertaActual = nivelAlertaActual; }
    public List<EstacionMeteorologica> getEstaciones() { return Collections.unmodifiableList(estaciones); }
    public List<ValorHistorico> getValoresHistoricos() { return Collections.unmodifiableList(valoresHistoricos); }
    public List<Alerta> getAlertas() { return Collections.unmodifiableList(alertas); }

    @Override
    public String toString() {
        return idZona + " - " + nombre + " [" + nivelVulnerabilidad + ", alerta " + nivelAlertaActual + "]";
    }
}
