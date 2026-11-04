package elnino.ui;

import java.time.LocalDate;

import elnino.enums.NivelAlerta;
import elnino.modelo.EstacionMeteorologica;
import elnino.modelo.ZonaGeografica;
import elnino.servicio.Reportes;
import elnino.servicio.Sistema;

public class MenuReportes {
    private final Sistema sistema;
    private final Reportes reportes;
    private final Consola c;

    public MenuReportes(Sistema sistema, Consola c) {
        this.sistema = sistema;
        this.reportes = new Reportes(sistema);
        this.c = c;
    }

    public void ejecutar() {
        boolean volver = false;
        while (!volver) {
            c.titulo("INFORMES Y REPORTES");
            c.info("1. Histórico de precipitaciones por estación");
            c.info("2. Histórico de precipitaciones por zona");
            c.info("3. Ranking de estaciones por volumetría");
            c.info("4. Historial de alertas emitidas");
            c.info("5. Ficha técnica operativa de una estación");
            c.info("6. Informe de fallas y mantenimiento");
            c.info("7. Estadísticas de anomalía climática por zona");
            c.info("0. Volver");
            switch (c.leerEntero("Opción", 0, 7)) {
                case 1 -> historicoEstacion();
                case 2 -> historicoZona();
                case 3 -> ranking();
                case 4 -> historialAlertas();
                case 5 -> fichaTecnica();
                case 6 -> c.info("\n" + reportes.informeFallas());
                case 7 -> anomalia();
                default -> volver = true;
            }
        }
    }

    private void historicoEstacion() {
        EstacionMeteorologica e = c.elegir("Estación:", sistema.getEstaciones());
        if (e != null) {
            c.info("\n" + reportes.historicoEstacion(e));
        }
    }

    private void historicoZona() {
        ZonaGeografica z = c.elegir("Zona:", sistema.getZonas());
        if (z != null) {
            c.info("\n" + reportes.historicoZona(z));
        }
    }

    private void ranking() {
        LocalDate desde = c.leerFecha("Fecha desde");
        LocalDate hasta = c.leerFecha("Fecha hasta");
        if (hasta.isBefore(desde)) {
            c.error("La fecha 'hasta' no puede ser anterior a 'desde'.");
            return;
        }
        c.info("\n" + reportes.ranking(desde, hasta));
    }

    private void historialAlertas() {
        NivelAlerta nivel = c.elegirEnumOpcional("Filtrar por nivel de severidad:", NivelAlerta.class);
        LocalDate desde = c.leerFechaOpcional("Fecha desde");
        LocalDate hasta = c.leerFechaOpcional("Fecha hasta");
        ZonaGeografica zona = c.elegir("Filtrar por zona:", sistema.getZonas(), "Todas las zonas");
        c.info("\n" + reportes.historialAlertas(nivel, desde, hasta, zona));
    }

    private void fichaTecnica() {
        EstacionMeteorologica e = c.elegir("Estación:", sistema.getEstaciones());
        if (e != null) {
            c.info("\n" + reportes.fichaTecnica(e));
        }
    }

    private void anomalia() {
        ZonaGeografica z = c.elegir("Zona:", sistema.getZonas());
        if (z != null) {
            c.info("\n" + reportes.anomaliaClimatica(z));
        }
    }
}
