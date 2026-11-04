package elnino.ui;

import elnino.excepciones.OperacionNoPermitidaException;
import elnino.modelo.EquipoRespuesta;
import elnino.modelo.EstacionMeteorologica;
import elnino.modelo.ResponsableTecnico;
import elnino.servicio.Sistema;

public class MenuPersonal {
    private final Sistema sistema;
    private final Consola c;

    public MenuPersonal(Sistema sistema, Consola c) {
        this.sistema = sistema;
        this.c = c;
    }

    public void ejecutar() {
        boolean volver = false;
        while (!volver) {
            c.titulo("PERSONAL TÉCNICO Y EQUIPOS DE RESPUESTA");
            c.info("1. Listar responsables técnicos");
            c.info("2. Registrar responsable técnico");
            c.info("3. Cambiar responsable de una estación");
            c.info("4. Listar equipos de respuesta");
            c.info("5. Crear equipo de respuesta");
            c.info("6. Agregar integrante a un equipo");
            c.info("7. Liberar equipo (marcarlo disponible)");
            c.info("0. Volver");
            switch (c.leerEntero("Opción", 0, 7)) {
                case 1 -> listarResponsables();
                case 2 -> registrarResponsable();
                case 3 -> cambiarResponsableEstacion();
                case 4 -> listarEquipos();
                case 5 -> crearEquipo();
                case 6 -> agregarIntegrante();
                case 7 -> liberarEquipo();
                default -> volver = true;
            }
        }
    }

    private void listarResponsables() {
        c.titulo("RESPONSABLES TÉCNICOS");
        for (ResponsableTecnico r : sistema.getResponsables()) {
            c.info(r + " | DNI " + r.getDni() + " | " + r.getEstacionesACargo().size() + " estaciones a cargo | "
                    + (r.getEquipo() == null ? "sin equipo" : r.getEquipo().getNombreEquipo()));
        }
        if (sistema.getResponsables().isEmpty()) {
            c.info("No hay responsables registrados.");
        }
    }

    private void registrarResponsable() {
        String dni = c.leerTexto("DNI");
        String nombre = c.leerTexto("Nombre");
        String apellido = c.leerTexto("Apellido");
        String correo = c.leerTexto("Correo electrónico");
        String celular = c.leerTexto("Celular");
        String especialidad = c.leerTexto("Especialidad");
        String licencia = c.leerTexto("Número de licencia");
        try {
            sistema.registrarResponsable(new ResponsableTecnico(dni, nombre, apellido, correo, celular, especialidad, licencia));
            c.ok("Responsable registrado.");
        } catch (OperacionNoPermitidaException e) {
            c.error(e.getMessage());
        }
    }

    private void cambiarResponsableEstacion() {
        EstacionMeteorologica e = c.elegir("Estación:", sistema.getEstaciones());
        if (e == null) {
            return;
        }
        ResponsableTecnico r = c.elegir("Nuevo responsable:", sistema.getResponsables());
        if (r != null) {
            e.setResponsable(r);
            c.ok("Ahora " + e.getNombre() + " está a cargo de " + r.getNombreCompleto());
        }
    }

    private void listarEquipos() {
        c.titulo("EQUIPOS DE RESPUESTA");
        for (EquipoRespuesta e : sistema.getEquipos()) {
            c.info(e + " | integrantes: " + e.getIntegrantes() + " | alertas atendidas: " + e.getAlertasAtendidas().size());
        }
        if (sistema.getEquipos().isEmpty()) {
            c.info("No hay equipos registrados.");
        }
    }

    private void crearEquipo() {
        ResponsableTecnico primero = c.elegir("Primer integrante (técnicos sin equipo):", sistema.getResponsablesSinEquipo());
        if (primero == null) {
            return;
        }
        String id = c.leerTexto("Id del equipo (ej. EQ-04)");
        String nombre = c.leerTexto("Nombre del equipo");
        try {
            EquipoRespuesta e = new EquipoRespuesta(id, nombre);
            sistema.registrarEquipo(e);
            e.agregarIntegrante(primero);
            c.ok("Equipo creado.");
        } catch (OperacionNoPermitidaException ex) {
            c.error(ex.getMessage());
        }
    }

    private void agregarIntegrante() {
        EquipoRespuesta e = c.elegir("Equipo:", sistema.getEquipos());
        if (e == null) {
            return;
        }
        ResponsableTecnico r = c.elegir("Técnico a agregar (sin equipo):", sistema.getResponsablesSinEquipo());
        if (r == null) {
            return;
        }
        try {
            e.agregarIntegrante(r);
            c.ok("Integrante agregado.");
        } catch (OperacionNoPermitidaException ex) {
            c.error(ex.getMessage());
        }
    }

    private void liberarEquipo() {
        EquipoRespuesta e = c.elegir("Equipo asignado a liberar:",
                sistema.getEquipos().stream().filter(x -> !x.isDisponible()).toList());
        if (e != null) {
            e.setDisponible(true);
            c.ok(e.getNombreEquipo() + " vuelve a estar disponible.");
        }
    }
}
