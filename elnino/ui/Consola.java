package elnino.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Consola {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Scanner in = new Scanner(System.in);

    public void titulo(String texto) {
        System.out.println();
        System.out.println("==== " + texto + " ====");
    }

    public void ok(String texto) {
        System.out.println("[OK] " + texto);
    }

    public void error(String texto) {
        System.out.println("[ERROR] " + texto);
    }

    public void info(String texto) {
        System.out.println(texto);
    }

    private String leerLinea(String mensaje) {
        System.out.print(mensaje + ": ");
        return in.nextLine().trim();
    }

    public String leerTexto(String mensaje) {
        while (true) {
            String s = leerLinea(mensaje);
            if (!s.isEmpty()) {
                return s;
            }
            error("El valor no puede estar vacío.");
        }
    }

    public double leerDecimal(String mensaje) {
        while (true) {
            String s = leerLinea(mensaje).replace(',', '.');
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                error("Ingresá un número válido (ej. 12.5).");
            }
        }
    }

    public int leerEntero(String mensaje, int min, int max) {
        while (true) {
            String s = leerLinea(mensaje);
            try {
                int n = Integer.parseInt(s);
                if (n >= min && n <= max) {
                    return n;
                }
            } catch (NumberFormatException e) {
            }
            error("Ingresá un número entre " + min + " y " + max + ".");
        }
    }

    public LocalDate leerFecha(String mensaje) {
        while (true) {
            String s = leerLinea(mensaje + " (dd/MM/aaaa)");
            try {
                return LocalDate.parse(s, FECHA);
            } catch (DateTimeParseException e) {
                error("Fecha inválida. Usá el formato dd/MM/aaaa (ej. 14/10/2026).");
            }
        }
    }

    public LocalDate leerFechaOpcional(String mensaje) {
        while (true) {
            String s = leerLinea(mensaje + " (dd/MM/aaaa, Enter para omitir)");
            if (s.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(s, FECHA);
            } catch (DateTimeParseException e) {
                error("Fecha inválida. Usá el formato dd/MM/aaaa.");
            }
        }
    }

    public LocalDateTime leerFechaHoraOpcional(String mensaje) {
        while (true) {
            String s = leerLinea(mensaje + " (dd/MM/aaaa HH:mm, Enter = ahora)");
            if (s.isEmpty()) {
                return LocalDateTime.now().withSecond(0).withNano(0);
            }
            try {
                return LocalDateTime.parse(s, FECHA_HORA);
            } catch (DateTimeParseException e) {
                error("Formato inválido. Usá dd/MM/aaaa HH:mm (ej. 14/10/2026 18:30).");
            }
        }
    }

    public boolean confirmar(String mensaje) {
        String s = leerLinea(mensaje + " (s/n)").toLowerCase();
        return s.equals("s") || s.equals("si") || s.equals("sí");
    }

    public <T> T elegir(String titulo, List<T> opciones, String etiquetaCero) {
        if (opciones.isEmpty()) {
            info("  (No hay opciones disponibles)");
            return null;
        }
        info(titulo);
        for (int i = 0; i < opciones.size(); i++) {
            info("  " + (i + 1) + ". " + opciones.get(i));
        }
        info("  0. " + etiquetaCero);
        int n = leerEntero("Opción", 0, opciones.size());
        return n == 0 ? null : opciones.get(n - 1);
    }

    public <T> T elegir(String titulo, List<T> opciones) {
        return elegir(titulo, opciones, "Cancelar");
    }

    public <T extends Enum<T>> T elegirEnum(String titulo, Class<T> tipo) {
        return elegir(titulo, Arrays.asList(tipo.getEnumConstants()));
    }

    public <T extends Enum<T>> T elegirEnumOpcional(String titulo, Class<T> tipo) {
        return elegir(titulo, Arrays.asList(tipo.getEnumConstants()), "Todos (sin filtrar)");
    }
}
