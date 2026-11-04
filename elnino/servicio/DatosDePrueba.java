package elnino.servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import elnino.enums.EstadoOperativo;
import elnino.enums.NivelAlerta;
import elnino.enums.NivelVulnerabilidad;
import elnino.enums.TipoEstacion;
import elnino.enums.TipoEventoClimatico;
import elnino.enums.TipoMantenimiento;
import elnino.modelo.Alerta;
import elnino.modelo.EquipoRespuesta;
import elnino.modelo.EstacionMeteorologica;
import elnino.modelo.ResponsableTecnico;
import elnino.modelo.Sensor;
import elnino.modelo.ZonaGeografica;

public final class DatosDePrueba {
    private DatosDePrueba() {
    }

    public static void cargar(Sistema s) {
        try {
            cargarDatos(s);
        } catch (Exception e) {
            throw new IllegalStateException("Error al cargar los datos de prueba: " + e.getMessage(), e);
        }
    }

    private static void cargarDatos(Sistema s) throws Exception {
        LocalDate hoy = LocalDate.now();
        LocalDateTime base = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        ResponsableTecnico t1 = new ResponsableTecnico("30111222", "Lucía", "Fernández", "lfernandez@meteo.gob", "1155501001", "Hidrología", "MP-1001");
        ResponsableTecnico t2 = new ResponsableTecnico("28333444", "Martín", "Gómez", "mgomez@meteo.gob", "1155501002", "Meteorología", "MP-1002");
        ResponsableTecnico t3 = new ResponsableTecnico("32555666", "Sofía", "Ramírez", "sramirez@meteo.gob", "1155501003", "Electrónica", "MP-1003");
        ResponsableTecnico t4 = new ResponsableTecnico("27777888", "Diego", "Pérez", "dperez@meteo.gob", "1155501004", "Instrumentación", "MP-1004");
        ResponsableTecnico t5 = new ResponsableTecnico("33999000", "Valeria", "Torres", "vtorres@meteo.gob", "1155501005", "Hidrología", "MP-1005");
        ResponsableTecnico t6 = new ResponsableTecnico("29121314", "Nicolás", "Silva", "nsilva@meteo.gob", "1155501006", "Rescate", "MP-1006");
        ResponsableTecnico t7 = new ResponsableTecnico("31151617", "Camila", "Rojas", "crojas@meteo.gob", "1155501007", "Rescate", "MP-1007");
        ResponsableTecnico t8 = new ResponsableTecnico("34181920", "Julián", "Acosta", "jacosta@meteo.gob", "1155501008", "Logística", "MP-1008");
        for (ResponsableTecnico t : new ResponsableTecnico[] {t1, t2, t3, t4, t5, t6, t7, t8}) {
            s.registrarResponsable(t);
        }

        ZonaGeografica norte = new ZonaGeografica("Z-01", "Cuenca Río Norte", NivelVulnerabilidad.ALTO);
        ZonaGeografica sur = new ZonaGeografica("Z-02", "Cuenca Río Sur", NivelVulnerabilidad.MUY_ALTO);
        ZonaGeografica sierra = new ZonaGeografica("Z-03", "Zona Serrana", NivelVulnerabilidad.MEDIO);
        double[] hist = {210, 190, 230, 180, 140, 110, 90, 100, 130, 170, 200, 220};
        for (int m = 1; m <= 12; m++) {
            norte.agregarValorHistorico(m, hist[m - 1]);
            sur.agregarValorHistorico(m, Math.round(hist[m - 1] * 0.9 * 10) / 10.0);
            sierra.agregarValorHistorico(m, Math.round(hist[m - 1] * 0.4 * 10) / 10.0);
        }
        s.registrarZona(norte);
        s.registrarZona(sur);
        s.registrarZona(sierra);

        EstacionMeteorologica e1 = s.registrarEstacion("E-01", "Estación Río Norte", -34.60, -58.38, 25, EstadoOperativo.OPERATIVO, TipoEstacion.AUTOMATICA, norte, t1);
        EstacionMeteorologica e2 = s.registrarEstacion("E-02", "Estación Paso Seco", -34.75, -58.55, 40, EstadoOperativo.OPERATIVO, TipoEstacion.CONVENCIONAL, norte, t2);
        EstacionMeteorologica e3 = s.registrarEstacion("E-03", "Estación Delta", -34.90, -58.20, 5, EstadoOperativo.OPERATIVO, TipoEstacion.AUTOMATICA, sur, t3);
        EstacionMeteorologica e4 = s.registrarEstacion("E-04", "Estación Bajo Sur", -35.05, -58.30, 12, EstadoOperativo.OPERATIVO, TipoEstacion.CONVENCIONAL, sur, t3);
        EstacionMeteorologica e5 = s.registrarEstacion("E-05", "Estación Cumbre", -34.30, -59.10, 820, EstadoOperativo.OPERATIVO, TipoEstacion.AUTOMATICA, sierra, t2);

        Sensor s1 = new Sensor("S-01", "Davis", "Rain Collector II", 0.2, EstadoOperativo.OPERATIVO, hoy.minusMonths(5));
        Sensor s2 = new Sensor("S-02", "Davis", "Rain Collector II", 0.2, EstadoOperativo.OPERATIVO, hoy.minusMonths(6));
        Sensor s3 = new Sensor("S-03", "Texas Electronics", "TR-525", 0.25, EstadoOperativo.OPERATIVO, hoy.minusMonths(6));
        Sensor s4 = new Sensor("S-04", "Campbell", "TB4", 0.2, EstadoOperativo.OPERATIVO, hoy.minusMonths(7));
        Sensor s5 = new Sensor("S-05", "Texas Electronics", "TR-525", 0.25, EstadoOperativo.OPERATIVO, hoy.minusMonths(3));
        Sensor s6 = new Sensor("S-06", "Campbell", "TB4", 0.2, EstadoOperativo.OPERATIVO, hoy.minusMonths(14));
        Sensor s7 = new Sensor("S-07", "Davis", "Rain Collector II", 0.2, EstadoOperativo.OPERATIVO, hoy.minusMonths(8));
        Sensor s8 = new Sensor("S-08", "Hobo", "RG3-M", 0.2, EstadoOperativo.OPERATIVO, hoy.minusMonths(2));
        for (Sensor x : new Sensor[] {s1, s2, s3, s4, s5, s6, s7, s8}) {
            s.registrarSensor(x);
        }
        e1.instalarSensor(s1);
        e1.instalarSensor(s2);
        e2.instalarSensor(s3);
        e3.instalarSensor(s4);
        e3.instalarSensor(s7);
        e4.instalarSensor(s5);
        e5.instalarSensor(s6);

        e1.registrarMantenimiento("M-01", hoy.minusMonths(5), TipoMantenimiento.CALIBRACION, "Calibración anual del pluviómetro", s1, t1);
        e1.registrarMantenimiento("M-02", hoy.minusDays(30), TipoMantenimiento.PREVENTIVO, "Limpieza de embudo y revisión de cableado", s2, t1);
        e3.registrarMantenimiento("M-03", hoy.minusDays(10), TipoMantenimiento.CORRECTIVO, "Falla en el basculante del pluviómetro", s7, t3);

        Random rnd = new Random(2026);
        Sensor[] activos = {s1, s2, s3, s4, s5, s6, s7};
        for (int d = 40; d >= 2; d--) {
            for (Sensor x : activos) {
                double mm = rnd.nextDouble() < 0.6 ? 0 : Math.round(rnd.nextDouble() * 120) / 10.0;
                s.registrarLectura(x, mm, base.minusDays(d));
            }
        }

        s.registrarLectura(s1, 55.0, base.minusHours(8));
        s.registrarLectura(s3, 45.0, base.minusHours(6));
        s.registrarLectura(s2, 60.0, base.minusHours(3));

        s.registrarLectura(s4, 130.0, base.minusHours(5));

        s.registrarLectura(s6, 3.5, base.minusHours(1));

        sierra.emitirAlerta(TipoEventoClimatico.CRECIDA_RIO, NivelAlerta.AMARILLA,
                "Crecida moderada del arroyo Las Piedras en el tramo medio.", base.minusHours(2));
        Alerta tormenta = sierra.emitirAlerta(TipoEventoClimatico.TORMENTA_SEVERA, NivelAlerta.NARANJA,
                "Tormenta eléctrica con granizo en la zona alta.", base.minusHours(1));

        EquipoRespuesta alfa = new EquipoRespuesta("EQ-01", "Equipo Alfa");
        alfa.agregarIntegrante(t4);
        alfa.agregarIntegrante(t5);
        EquipoRespuesta bravo = new EquipoRespuesta("EQ-02", "Equipo Bravo");
        bravo.agregarIntegrante(t6);
        bravo.agregarIntegrante(t7);
        EquipoRespuesta charlie = new EquipoRespuesta("EQ-03", "Equipo Charlie");
        charlie.agregarIntegrante(t8);
        s.registrarEquipo(alfa);
        s.registrarEquipo(bravo);
        s.registrarEquipo(charlie);

        norte.getAlertas().get(norte.getAlertas().size() - 1).asignarEquipo(alfa);
        tormenta.asignarEquipo(bravo);

        e4.setEstado(EstadoOperativo.EN_MANTENIMIENTO);
        e4.registrarMantenimiento("M-04", hoy.minusDays(1), TipoMantenimiento.CORRECTIVO, "Reemplazo de batería y revisión del datalogger", null, t3);
        s3.setEstado(EstadoOperativo.EN_MANTENIMIENTO);
        s7.setEstado(EstadoOperativo.FUERA_DE_SERVICIO);
    }
}
