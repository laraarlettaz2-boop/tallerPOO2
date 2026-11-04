package elnino.ui;

import java.time.LocalDateTime;
import java.util.List;

import elnino.enums.NivelAlerta;
import elnino.excepciones.LecturaInvalidaException;
import elnino.excepciones.OperacionNoPermitidaException;
import elnino.modelo.Medicion;
import elnino.modelo.Sensor;
import elnino.modelo.ZonaGeografica;
import elnino.servicio.ResultadoLectura;
import elnino.servicio.Sistema;

public class MenuMediciones {
    private final Sistema sistema;
    private final Consola c;

    public MenuMediciones(Sistema sistema, Consola c) {
        this.sistema = sistema;
        this.c = c;
    }

    public void ejecutar() {
        boolean volver = false;
        while (!volver) {
            c.titulo("MEDICIONES");
            c.info("1. Registrar lectura de precipitación");
            c.info("2. Ver últimas lecturas de un sensor");
            c.info("3. Evaluar riesgo actual de una zona (últimas 24 h)");
            c.info("0. Volver");
            switch (c.leerEntero("Opción", 0, 3)) {
                case 1 -> registrarLectura();
                case 2 -> verLecturas();
                case 3 -> evaluarZona();
                default -> volver = true;
            }
        }
    }

    private void registrarLectura() {
        Sensor s = c.elegir("Sensor que registra la lectura:", sistema.getSensoresInstalados());
        if (s == null) {
            return;
        }
        double mm = c.leerDecimal("Precipitación (mm)");
        LocalDateTime fh = c.leerFechaHoraOpcional("Fecha y hora de la lectura");
        try {
            ResultadoLectura r = sistema.registrarLectura(s, mm, fh);
            c.ok("Lectura registrada: " + r.medicion());
            if (r.medicion().isAnomalia()) {
                c.info("[!] La lectura difiere más de " + (int) Sensor.DIFERENCIA_ANOMALA_MM
                        + " mm de la anterior: quedó marcada como ANÓMALA.");
            }
            if (r.alerta() != null) {
                c.info("[!] Se superó un umbral. ALERTA EMITIDA: " + r.alerta());
                if (r.alerta().requiereEquipo()) {
                    c.info("[!] Esta alerta requiere asignar un equipo de respuesta (menú Alertas).");
                }
            }
        } catch (LecturaInvalidaException e) {
            c.error("Lectura rechazada: " + e.getMessage());
        } catch (OperacionNoPermitidaException e) {
            c.error("Operación no permitida: " + e.getMessage());
        }
    }

    private void verLecturas() {
        Sensor s = c.elegir("Sensor:", sistema.getSensores());
        if (s == null) {
            return;
        }
        List<Medicion> ms = s.getMediciones();
        if (ms.isEmpty()) {
            c.info("El sensor no tiene lecturas.");
            return;
        }
        c.info("Últimas lecturas de " + s.getIdSensor() + " (" + ms.size() + " en total):");
        for (Medicion m : ms.subList(Math.max(0, ms.size() - 10), ms.size())) {
            c.info("  " + m);
        }
    }

    private void evaluarZona() {
        ZonaGeografica z = c.elegir("Zona:", sistema.getZonas());
        if (z == null) {
            return;
        }
        double mm = z.lluviaAcumulada24h();
        NivelAlerta nivel = z.evaluarRiesgo();
        c.info(String.format("%s: %.1f mm acumulados en las últimas 24 h -> nivel %s", z.getNombre(), mm, nivel));
        c.info(String.format("Umbrales: amarilla %.0f | naranja %.0f | roja %.0f", z.getUmbralAmarilla(),
                z.getUmbralNaranja(), z.getUmbralRoja()));
    }
}
