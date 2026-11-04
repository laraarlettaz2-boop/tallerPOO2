package elnino.ui;

import elnino.servicio.Sistema;

public class MenuPrincipal {
    private final Consola c = new Consola();
    private final MenuInfraestructura infraestructura;
    private final MenuPersonal personal;
    private final MenuMediciones mediciones;
    private final MenuAlertas alertas;
    private final MenuReportes reportes;

    public MenuPrincipal(Sistema sistema) {
        this.infraestructura = new MenuInfraestructura(sistema, c);
        this.personal = new MenuPersonal(sistema, c);
        this.mediciones = new MenuMediciones(sistema, c);
        this.alertas = new MenuAlertas(sistema, c);
        this.reportes = new MenuReportes(sistema, c);
    }

    public void iniciar() {
        c.titulo("RED DE ESTACIONES METEOROLÓGICAS - EL NIÑO");
        boolean salir = false;
        while (!salir) {
            c.titulo("MENÚ PRINCIPAL");
            c.info("1. Infraestructura (zonas, estaciones, sensores)");
            c.info("2. Personal técnico y equipos de respuesta");
            c.info("3. Mediciones");
            c.info("4. Alerta temprana");
            c.info("5. Informes y reportes");
            c.info("0. Salir");
            switch (c.leerEntero("Opción", 0, 5)) {
                case 1 -> infraestructura.ejecutar();
                case 2 -> personal.ejecutar();
                case 3 -> mediciones.ejecutar();
                case 4 -> alertas.ejecutar();
                case 5 -> reportes.ejecutar();
                default -> salir = true;
            }
        }
        c.info("Hasta luego.");
    }
}
