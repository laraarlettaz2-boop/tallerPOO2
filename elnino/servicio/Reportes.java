package elnino.servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import elnino.enums.EstadoOperativo;
import elnino.enums.NivelAlerta;
import elnino.modelo.Alerta;
import elnino.modelo.EstacionMeteorologica;
import elnino.modelo.Medicion;
import elnino.modelo.ResponsableTecnico;
import elnino.modelo.Sensor;
import elnino.modelo.ValorHistorico;
import elnino.modelo.ZonaGeografica;

public class Reportes {
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Sistema sistema;

    public Reportes(Sistema sistema) {
        this.sistema = sistema;
    }

    public String historicoEstacion(EstacionMeteorologica e) {
        List<Medicion> ms = new ArrayList<>();
        for (Sensor s : e.getSensores()) {
            ms.addAll(s.getMediciones());
        }
        return historico("ESTACIÓN " + e.getNombre(), e.getZona(), ms);
    }

    public String historicoZona(ZonaGeografica z) {
        List<Medicion> ms = new ArrayList<>();
        for (EstacionMeteorologica e : z.getEstaciones()) {
            for (Sensor s : e.getSensores()) {
                ms.addAll(s.getMediciones());
            }
        }
        return historico("ZONA " + z.getNombre(), z, ms);
    }

    private String historico(String titulo, ZonaGeografica zona, List<Medicion> ms) {
        StringBuilder sb = new StringBuilder("=== HISTÓRICO DE PRECIPITACIONES - " + titulo + " ===\n");
        if (ms.isEmpty()) {
            return sb.append("Sin lecturas registradas.\n").toString();
        }
        Map<LocalDate, Double> diario = new TreeMap<>();
        Map<String, Double> semanal = new TreeMap<>();
        Map<YearMonth, Double> mensual = new TreeMap<>();
        for (Medicion m : ms) {
            LocalDate d = m.getFechaHora().toLocalDate();
            diario.merge(d, m.getMilimetros(), Double::sum);
            String sem = String.format("%d-S%02d", d.get(IsoFields.WEEK_BASED_YEAR), d.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
            semanal.merge(sem, m.getMilimetros(), Double::sum);
            mensual.merge(YearMonth.from(d), m.getMilimetros(), Double::sum);
        }
        sb.append("\n-- Acumulado diario (mm) --\n");
        diario.forEach((k, v) -> sb.append(String.format("  %s: %.1f%n", k.format(FECHA), v)));
        sb.append("\n-- Acumulado semanal (mm) --\n");
        semanal.forEach((k, v) -> sb.append(String.format("  %s: %.1f%n", k, v)));
        sb.append("\n-- Acumulado mensual vs. media histórica de la zona --\n");
        mensual.forEach((k, v) -> sb.append("  ").append(k).append(": ").append(String.format("%.1f mm", v))
                .append(compararConHistorico(zona, k, v)).append("\n"));
        return sb.toString();
    }

    private String compararConHistorico(ZonaGeografica zona, YearMonth mes, double acumulado) {
        Optional<ValorHistorico> h = zona == null ? Optional.empty() : zona.getValorHistorico(mes.getMonthValue());
        if (h.isEmpty() || h.get().getAcumuladoMedio() <= 0) {
            return " | sin media histórica cargada";
        }
        double hist = h.get().getAcumuladoMedio();
        double desv = (acumulado - hist) / hist * 100.0;
        return String.format(" | media histórica %.1f mm | desvío %+.1f%%", hist, desv);
    }

    public String ranking(LocalDate desde, LocalDate hasta) {
        LocalDateTime d = desde.atStartOfDay();
        LocalDateTime h = hasta.atTime(LocalTime.MAX);
        List<EstacionMeteorologica> est = new ArrayList<>(sistema.getEstaciones());
        est.sort(Comparator.comparingDouble((EstacionMeteorologica e) -> e.acumuladoRango(d, h)).reversed());
        StringBuilder sb = new StringBuilder("=== RANKING DE ESTACIONES POR VOLUMETRÍA ===\n");
        sb.append("Rango: ").append(desde.format(FECHA)).append(" al ").append(hasta.format(FECHA)).append("\n");
        int pos = 1;
        for (EstacionMeteorologica e : est) {
            sb.append(String.format("%2d. %-28s %-22s %8.1f mm%n", pos++, e.getNombre(),
                    "(" + e.getZona().getNombre() + ")", e.acumuladoRango(d, h)));
        }
        if (est.isEmpty()) {
            sb.append("No hay estaciones registradas.\n");
        }
        return sb.toString();
    }

    public String historialAlertas(NivelAlerta nivel, LocalDate desde, LocalDate hasta, ZonaGeografica zona) {
        List<Alerta> filtradas = sistema.getAlertas().stream()
                .filter(a -> nivel == null || a.getNivel() == nivel)
                .filter(a -> desde == null || !a.getFechaEmision().toLocalDate().isBefore(desde))
                .filter(a -> hasta == null || !a.getFechaEmision().toLocalDate().isAfter(hasta))
                .filter(a -> zona == null || a.getZona() == zona)
                .toList();
        StringBuilder sb = new StringBuilder("=== HISTORIAL DE ALERTAS ===\n");
        sb.append("Filtros: nivel=").append(nivel == null ? "todos" : nivel)
          .append(", desde=").append(desde == null ? "-" : desde.format(FECHA))
          .append(", hasta=").append(hasta == null ? "-" : hasta.format(FECHA))
          .append(", zona=").append(zona == null ? "todas" : zona.getNombre()).append("\n");
        if (filtradas.isEmpty()) {
            sb.append("No hay alertas que coincidan.\n");
        }
        for (Alerta a : filtradas) {
            sb.append("  ").append(a).append("\n");
        }
        sb.append("Total: ").append(filtradas.size()).append("\n");
        return sb.toString();
    }

    public String fichaTecnica(EstacionMeteorologica e) {
        StringBuilder sb = new StringBuilder("=== FICHA TÉCNICA OPERATIVA ===\n");
        sb.append(e.getIdEstacion()).append(" - ").append(e.getNombre()).append("\n");
        sb.append("Tipo: ").append(e.getTipo()).append(" | Estado: ").append(e.getEstado()).append("\n");
        sb.append(String.format("Ubicación: lat %.4f, lon %.4f, altitud %.0f m%n", e.getLatitud(), e.getLongitud(), e.getAltitud()));
        if (e.getZona() != null) {
            sb.append("Zona: ").append(e.getZona().getNombre()).append(" (vulnerabilidad ")
              .append(e.getZona().getNivelVulnerabilidad()).append(", alerta vigente ")
              .append(e.getZona().getNivelAlertaActual()).append(")\n");
        }
        ResponsableTecnico r = e.getResponsable();
        if (r != null) {
            sb.append("Responsable: ").append(r.getNombreCompleto()).append(" - ").append(r.getEspecialidad())
              .append(" | cel. ").append(r.getCelular()).append(" | ").append(r.getCorreoElectronico()).append("\n");
        }
        sb.append("\nSensores instalados (").append(e.getSensores().size()).append("):\n");
        for (Sensor s : e.getSensores()) {
            sb.append("  - ").append(s.getIdSensor()).append(" ").append(s.getMarca()).append(" ").append(s.getModelo())
              .append(" | ").append(s.getEstado())
              .append(" | última calibración: ").append(s.getUltimaCalibracion() == null ? "nunca" : s.getUltimaCalibracion().format(FECHA))
              .append(s.requiereCalibracion() ? " (REQUIERE CALIBRACIÓN)" : "").append("\n");
            List<Medicion> ms = s.getMediciones();
            if (ms.isEmpty()) {
                sb.append("      Sin lecturas.\n");
            } else {
                sb.append("      Últimas lecturas:\n");
                for (Medicion m : ms.subList(Math.max(0, ms.size() - 3), ms.size())) {
                    sb.append("        ").append(m).append("\n");
                }
            }
        }
        sb.append("\nÚltimos mantenimientos:\n");
        var mants = e.getMantenimientos();
        if (mants.isEmpty()) {
            sb.append("  (ninguno)\n");
        }
        for (var m : mants.subList(Math.max(0, mants.size() - 3), mants.size())) {
            sb.append("  ").append(m).append("\n");
        }
        return sb.toString();
    }

    public String informeFallas() {
        StringBuilder sb = new StringBuilder("=== INFORME DE FALLAS Y MANTENIMIENTO ===\n");
        sb.append("\n-- Estaciones no operativas --\n");
        int n = 0;
        for (EstacionMeteorologica e : sistema.getEstaciones()) {
            if (e.getEstado() != EstadoOperativo.OPERATIVO) {
                sb.append("  ").append(e).append("\n");
                n++;
            }
        }
        if (n == 0) {
            sb.append("  (ninguna)\n");
        }
        sb.append("\n-- Sensores en mantenimiento o fuera de servicio --\n");
        n = 0;
        for (Sensor s : sistema.getSensores()) {
            if (s.getEstado() != EstadoOperativo.OPERATIVO) {
                sb.append("  ").append(s).append("\n");
                n++;
            }
        }
        if (n == 0) {
            sb.append("  (ninguno)\n");
        }
        sb.append("\n-- Lecturas anómalas (difieren más de ").append((int) Sensor.DIFERENCIA_ANOMALA_MM)
          .append(" mm de la anterior) --\n");
        n = 0;
        for (Sensor s : sistema.getSensores()) {
            for (Medicion m : s.getMediciones()) {
                if (m.isAnomalia()) {
                    sb.append("  Sensor ").append(s.getIdSensor()).append(": ").append(m).append("\n");
                    n++;
                }
            }
        }
        if (n == 0) {
            sb.append("  (ninguna)\n");
        }
        sb.append("\n-- Sensores que requieren calibración (más de ").append(Sensor.MESES_CALIBRACION).append(" meses) --\n");
        n = 0;
        for (Sensor s : sistema.getSensores()) {
            if (s.requiereCalibracion()) {
                sb.append("  ").append(s.getIdSensor()).append(" - última calibración: ")
                  .append(s.getUltimaCalibracion() == null ? "nunca" : s.getUltimaCalibracion().format(FECHA)).append("\n");
                n++;
            }
        }
        if (n == 0) {
            sb.append("  (ninguno)\n");
        }
        return sb.toString();
    }

    public String anomaliaClimatica(ZonaGeografica z) {
        StringBuilder sb = new StringBuilder("=== ANOMALÍA CLIMÁTICA - " + z.getNombre() + " ===\n");
        Map<YearMonth, Double> mensual = new TreeMap<>();
        for (EstacionMeteorologica e : z.getEstaciones()) {
            for (Sensor s : e.getSensores()) {
                for (Medicion m : s.getMediciones()) {
                    mensual.merge(YearMonth.from(m.getFechaHora()), m.getMilimetros(), Double::sum);
                }
            }
        }
        if (mensual.isEmpty()) {
            return sb.append("Sin lecturas registradas.\n").toString();
        }
        mensual.forEach((mes, acum) -> {
            Optional<ValorHistorico> h = z.getValorHistorico(mes.getMonthValue());
            if (h.isEmpty() || h.get().getAcumuladoMedio() <= 0) {
                sb.append(String.format("  %s: %.1f mm | sin valor histórico para comparar%n", mes, acum));
            } else {
                double hist = h.get().getAcumuladoMedio();
                double desv = (acum - hist) / hist * 100.0;
                String etiqueta = desv > 20 ? "SUPERIOR a lo normal" : desv < -20 ? "INFERIOR a lo normal" : "dentro de lo normal";
                sb.append(String.format("  %s: %.1f mm | histórico %.1f mm | desvío %+.1f%% (%s)%n", mes, acum, hist, desv, etiqueta));
            }
        });
        return sb.toString();
    }
}
