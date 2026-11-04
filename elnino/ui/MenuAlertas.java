package elnino.ui;

import elnino.enums.NivelAlerta;
import elnino.enums.TipoEventoClimatico;
import elnino.excepciones.OperacionNoPermitidaException;
import elnino.modelo.Alerta;
import elnino.modelo.EquipoRespuesta;
import elnino.modelo.ZonaGeografica;
import elnino.servicio.Sistema;

public class MenuAlertas {
    private final Sistema sistema;
    private final Consola c;

    public MenuAlertas(Sistema sistema, Consola c) {
        this.sistema = sistema;
        this.c = c;
    }

    public void ejecutar() {
        boolean volver = false;
        while (!volver) {
            c.titulo("ALERTA TEMPRANA");
            c.info("1. Ver estado de las zonas");
            c.info("2. Listar todas las alertas");
            c.info("3. Registrar alerta de operador (crecida de río / tormenta severa)");
            c.info("4. Asignar equipo de respuesta a una alerta pendiente");
            c.info("0. Volver");
            switch (c.leerEntero("Opción", 0, 4)) {
                case 1 -> estadoZonas();
                case 2 -> listarAlertas();
                case 3 -> registrarAlertaOperador();
                case 4 -> asignarEquipo();
                default -> volver = true;
            }
        }
    }

    private void estadoZonas() {
        c.titulo("ESTADO DE LAS ZONAS");
        for (ZonaGeografica z : sistema.getZonas()) {
            c.info(z + String.format(" | umbrales %.0f / %.0f / %.0f mm", z.getUmbralAmarilla(),
                    z.getUmbralNaranja(), z.getUmbralRoja()));
        }
    }

    private void listarAlertas() {
        c.titulo("ALERTAS EMITIDAS");
        for (Alerta a : sistema.getAlertas()) {
            c.info(a.toString());
        }
        if (sistema.getAlertas().isEmpty()) {
            c.info("No hay alertas emitidas.");
        }
        c.info("Pendientes de equipo: " + sistema.getAlertasPendientes().size());
    }

    private void registrarAlertaOperador() {
        ZonaGeografica z = c.elegir("Zona afectada:", sistema.getZonas());
        if (z == null) {
            return;
        }
        TipoEventoClimatico ev = c.elegir("Tipo de evento:",
                java.util.List.of(TipoEventoClimatico.CRECIDA_RIO, TipoEventoClimatico.TORMENTA_SEVERA));
        if (ev == null) {
            return;
        }
        NivelAlerta nivel = c.elegirEnum("Nivel de la alerta:", NivelAlerta.class);
        if (nivel == null) {
            return;
        }
        String desc = c.leerTexto("Descripción");
        try {
            Alerta a = sistema.registrarAlertaOperador(z, ev, nivel, desc);
            c.ok("Alerta registrada: " + a);
            if (a.requiereEquipo()) {
                c.info("[!] Requiere asignación obligatoria de un equipo de respuesta.");
            }
        } catch (OperacionNoPermitidaException e) {
            c.error(e.getMessage());
        }
    }

    private void asignarEquipo() {
        Alerta a = c.elegir("Alertas NARANJA/ROJA sin equipo:", sistema.getAlertasPendientes());
        if (a == null) {
            return;
        }
        EquipoRespuesta e = c.elegir("Equipos disponibles:",
                sistema.getEquipos().stream().filter(x -> x.isDisponible() && x.estaConformado()).toList());
        if (e == null) {
            c.info("Si no hay equipos disponibles, liberá uno desde el menú de Personal.");
            return;
        }
        try {
            a.asignarEquipo(e);
            c.ok("Equipo " + e.getNombreEquipo() + " asignado a la alerta " + a.getIdAlerta());
        } catch (OperacionNoPermitidaException ex) {
            c.error(ex.getMessage());
        }
    }
}
