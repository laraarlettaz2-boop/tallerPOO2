package elnino.ui;

import java.time.LocalDate;

import elnino.enums.EstadoOperativo;
import elnino.enums.NivelVulnerabilidad;
import elnino.enums.TipoEstacion;
import elnino.enums.TipoMantenimiento;
import elnino.excepciones.OperacionNoPermitidaException;
import elnino.modelo.EstacionMeteorologica;
import elnino.modelo.ResponsableTecnico;
import elnino.modelo.Sensor;
import elnino.modelo.ZonaGeografica;
import elnino.servicio.Sistema;

public class MenuInfraestructura {
    private final Sistema sistema;
    private final Consola c;

    public MenuInfraestructura(Sistema sistema, Consola c) {
        this.sistema = sistema;
        this.c = c;
    }

    public void ejecutar() {
        boolean volver = false;
        while (!volver) {
            c.titulo("INFRAESTRUCTURA METEOROLÓGICA");
            c.info(" 1. Listar zonas");
            c.info(" 2. Registrar zona");
            c.info(" 3. Configurar umbrales de una zona");
            c.info(" 4. Cargar valor histórico mensual de una zona");
            c.info(" 5. Listar estaciones");
            c.info(" 6. Registrar estación");
            c.info(" 7. Cambiar estado de una estación");
            c.info(" 8. Listar sensores");
            c.info(" 9. Registrar sensor");
            c.info("10. Instalar sensor en una estación");
            c.info("11. Retirar sensor de su estación");
            c.info("12. Cambiar estado de un sensor");
            c.info("13. Registrar mantenimiento / calibración");
            c.info(" 0. Volver");
            switch (c.leerEntero("Opción", 0, 13)) {
                case 1 -> listarZonas();
                case 2 -> registrarZona();
                case 3 -> configurarUmbrales();
                case 4 -> cargarHistorico();
                case 5 -> listarEstaciones();
                case 6 -> registrarEstacion();
                case 7 -> cambiarEstadoEstacion();
                case 8 -> listarSensores();
                case 9 -> registrarSensor();
                case 10 -> instalarSensor();
                case 11 -> retirarSensor();
                case 12 -> cambiarEstadoSensor();
                case 13 -> registrarMantenimiento();
                default -> volver = true;
            }
        }
    }

    private void listarZonas() {
        c.titulo("ZONAS");
        for (ZonaGeografica z : sistema.getZonas()) {
            c.info(z + String.format(" | umbrales %.0f / %.0f / %.0f mm | %d estaciones",
                    z.getUmbralAmarilla(), z.getUmbralNaranja(), z.getUmbralRoja(), z.getEstaciones().size()));
        }
        if (sistema.getZonas().isEmpty()) {
            c.info("No hay zonas registradas.");
        }
    }

    private void registrarZona() {
        String id = c.leerTexto("Id de la zona (ej. Z-04)");
        String nombre = c.leerTexto("Nombre de la zona");
        NivelVulnerabilidad nv = c.elegirEnum("Nivel de vulnerabilidad:", NivelVulnerabilidad.class);
        if (nv == null) {
            return;
        }
        try {
            ZonaGeografica z;
            if (c.confirmar("¿Usar la escala oficial de umbrales (50 / 100 / 150 mm en 24 h)?")) {
                z = new ZonaGeografica(id, nombre, nv);
            } else {
                double a = c.leerDecimal("Umbral amarilla (mm en 24 h)");
                double n = c.leerDecimal("Umbral naranja (mm en 24 h)");
                double r = c.leerDecimal("Umbral roja (mm en 24 h)");
                z = new ZonaGeografica(id, nombre, nv, a, n, r);
            }
            sistema.registrarZona(z);
            c.ok("Zona registrada.");
        } catch (IllegalArgumentException | OperacionNoPermitidaException e) {
            c.error(e.getMessage());
        }
    }

    private void configurarUmbrales() {
        ZonaGeografica z = c.elegir("Zona a configurar:", sistema.getZonas());
        if (z == null) {
            return;
        }
        try {
            if (c.confirmar("¿Restablecer la escala oficial (50 / 100 / 150 mm)?")) {
                z.usarUmbralesOficiales();
            } else {
                double a = c.leerDecimal("Nuevo umbral amarilla (mm)");
                double n = c.leerDecimal("Nuevo umbral naranja (mm)");
                double r = c.leerDecimal("Nuevo umbral roja (mm)");
                z.configurarUmbrales(a, n, r);
            }
            c.ok("Umbrales actualizados.");
        } catch (IllegalArgumentException e) {
            c.error(e.getMessage());
        }
    }

    private void cargarHistorico() {
        ZonaGeografica z = c.elegir("Zona:", sistema.getZonas());
        if (z == null) {
            return;
        }
        int mes = c.leerEntero("Mes (1 a 12)", 1, 12);
        double mm = c.leerDecimal("Acumulado medio histórico (mm)");
        try {
            z.agregarValorHistorico(mes, mm);
            c.ok("Valor histórico guardado.");
        } catch (IllegalArgumentException e) {
            c.error(e.getMessage());
        }
    }

    private void listarEstaciones() {
        c.titulo("ESTACIONES");
        for (EstacionMeteorologica e : sistema.getEstaciones()) {
            c.info(e + " | zona " + e.getZona().getNombre() + " | " + e.getSensores().size()
                    + " sensores | resp. " + (e.getResponsable() == null ? "-" : e.getResponsable().getNombreCompleto()));
        }
        if (sistema.getEstaciones().isEmpty()) {
            c.info("No hay estaciones registradas.");
        }
    }

    private void registrarEstacion() {
        ZonaGeografica z = c.elegir("Zona de la estación:", sistema.getZonas());
        if (z == null) {
            return;
        }
        ResponsableTecnico r = c.elegir("Responsable técnico a cargo:", sistema.getResponsables());
        if (r == null) {
            return;
        }
        String id = c.leerTexto("Id de la estación (ej. E-06)");
        String nombre = c.leerTexto("Nombre");
        double lat = c.leerDecimal("Latitud");
        double lon = c.leerDecimal("Longitud");
        double alt = c.leerDecimal("Altitud (m)");
        TipoEstacion tipo = c.elegirEnum("Tipo de estación:", TipoEstacion.class);
        EstadoOperativo estado = c.elegirEnum("Estado inicial:", EstadoOperativo.class);
        if (tipo == null || estado == null) {
            return;
        }
        try {
            sistema.registrarEstacion(id, nombre, lat, lon, alt, estado, tipo, z, r);
            c.ok("Estación registrada.");
        } catch (OperacionNoPermitidaException e) {
            c.error(e.getMessage());
        }
    }

    private void cambiarEstadoEstacion() {
        EstacionMeteorologica e = c.elegir("Estación:", sistema.getEstaciones());
        if (e == null) {
            return;
        }
        EstadoOperativo nuevo = c.elegirEnum("Nuevo estado:", EstadoOperativo.class);
        if (nuevo != null) {
            e.setEstado(nuevo);
            c.ok("Estado actualizado: " + e);
        }
    }

    private void listarSensores() {
        c.titulo("SENSORES");
        for (Sensor s : sistema.getSensores()) {
            c.info(s + " | " + s.getMediciones().size() + " lecturas"
                    + (s.requiereCalibracion() ? " | REQUIERE CALIBRACIÓN" : ""));
        }
        if (sistema.getSensores().isEmpty()) {
            c.info("No hay sensores registrados.");
        }
    }

    private void registrarSensor() {
        String id = c.leerTexto("Id del sensor (ej. S-09)");
        String marca = c.leerTexto("Marca");
        String modelo = c.leerTexto("Modelo");
        double precision = c.leerDecimal("Precisión (mm)");
        EstadoOperativo estado = c.elegirEnum("Estado inicial:", EstadoOperativo.class);
        if (estado == null) {
            return;
        }
        LocalDate calibracion = c.leerFecha("Fecha de última calibración");
        try {
            sistema.registrarSensor(new Sensor(id, marca, modelo, precision, estado, calibracion));
            c.ok("Sensor registrado (queda sin instalar).");
        } catch (OperacionNoPermitidaException e) {
            c.error(e.getMessage());
        }
    }

    private void instalarSensor() {
        Sensor s = c.elegir("Sensor a instalar (solo sensores libres):", sistema.getSensoresLibres());
        if (s == null) {
            c.info("Tip: si el sensor está en otra estación, primero retiralo (opción 11).");
            return;
        }
        EstacionMeteorologica e = c.elegir("Estación destino:", sistema.getEstaciones());
        if (e == null) {
            return;
        }
        try {
            e.instalarSensor(s);
            c.ok("Sensor " + s.getIdSensor() + " instalado en " + e.getNombre());
        } catch (OperacionNoPermitidaException ex) {
            c.error(ex.getMessage());
        }
    }

    private void retirarSensor() {
        Sensor s = c.elegir("Sensor a retirar:", sistema.getSensoresInstalados());
        if (s == null) {
            return;
        }
        try {
            s.getEstacion().retirarSensor(s);
            c.ok("Sensor retirado; queda libre para instalarse en otra estación.");
        } catch (OperacionNoPermitidaException ex) {
            c.error(ex.getMessage());
        }
    }

    private void cambiarEstadoSensor() {
        Sensor s = c.elegir("Sensor:", sistema.getSensores());
        if (s == null) {
            return;
        }
        EstadoOperativo nuevo = c.elegirEnum("Nuevo estado:", EstadoOperativo.class);
        if (nuevo != null) {
            s.setEstado(nuevo);
            c.ok("Estado actualizado: " + s);
        }
    }

    private void registrarMantenimiento() {
        EstacionMeteorologica e = c.elegir("Estación intervenida:", sistema.getEstaciones());
        if (e == null) {
            return;
        }
        TipoMantenimiento tipo = c.elegirEnum("Tipo de mantenimiento:", TipoMantenimiento.class);
        if (tipo == null) {
            return;
        }
        Sensor sensor = null;
        if (!e.getSensores().isEmpty() && c.confirmar("¿Se aplica a un sensor puntual?")) {
            sensor = c.elegir("Sensor intervenido:", e.getSensores());
        }
        if (tipo == TipoMantenimiento.CALIBRACION && sensor == null) {
            c.error("Una calibración debe aplicarse a un sensor puntual.");
            return;
        }
        ResponsableTecnico r = c.elegir("Técnico que lo realizó:", sistema.getResponsables());
        if (r == null) {
            return;
        }
        String desc = c.leerTexto("Descripción");
        LocalDate fecha = c.leerFechaOpcional("Fecha de realización");
        try {
            e.registrarMantenimiento(sistema.siguienteIdMantenimiento(), fecha == null ? LocalDate.now() : fecha,
                    tipo, desc, sensor, r);
            c.ok("Mantenimiento registrado.");
        } catch (OperacionNoPermitidaException ex) {
            c.error(ex.getMessage());
        }
    }
}
