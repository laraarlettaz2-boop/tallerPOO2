package elnino.servicio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import elnino.enums.EstadoOperativo;
import elnino.enums.NivelAlerta;
import elnino.enums.TipoEstacion;
import elnino.enums.TipoEventoClimatico;
import elnino.excepciones.LecturaInvalidaException;
import elnino.excepciones.OperacionNoPermitidaException;
import elnino.modelo.Alerta;
import elnino.modelo.EquipoRespuesta;
import elnino.modelo.EstacionMeteorologica;
import elnino.modelo.Mantenimiento;
import elnino.modelo.ResponsableTecnico;
import elnino.modelo.Sensor;
import elnino.modelo.ZonaGeografica;

public class Sistema {
    private final List<ZonaGeografica> zonas = new ArrayList<>();
    private final List<Sensor> sensores = new ArrayList<>();
    private final List<ResponsableTecnico> responsables = new ArrayList<>();
    private final List<EquipoRespuesta> equipos = new ArrayList<>();

    public void registrarZona(ZonaGeografica z) throws OperacionNoPermitidaException {
        if (zonas.stream().anyMatch(x -> x.getIdZona().equalsIgnoreCase(z.getIdZona()))) {
            throw new OperacionNoPermitidaException("Ya existe una zona con id " + z.getIdZona());
        }
        zonas.add(z);
    }

    public EstacionMeteorologica registrarEstacion(String id, String nombre, double latitud, double longitud,
                                                   double altitud, EstadoOperativo estado, TipoEstacion tipo,
                                                   ZonaGeografica zona, ResponsableTecnico responsable)
            throws OperacionNoPermitidaException {
        if (zona == null || responsable == null) {
            throw new OperacionNoPermitidaException("La estación requiere una zona y un responsable técnico");
        }
        if (getEstaciones().stream().anyMatch(e -> e.getIdEstacion().equalsIgnoreCase(id))) {
            throw new OperacionNoPermitidaException("Ya existe una estación con id " + id);
        }
        return new EstacionMeteorologica(id, nombre, latitud, longitud, altitud, estado, tipo, zona, responsable);
    }

    public void registrarSensor(Sensor s) throws OperacionNoPermitidaException {
        if (sensores.stream().anyMatch(x -> x.getIdSensor().equalsIgnoreCase(s.getIdSensor()))) {
            throw new OperacionNoPermitidaException("Ya existe un sensor con id " + s.getIdSensor());
        }
        sensores.add(s);
    }

    public void registrarResponsable(ResponsableTecnico r) throws OperacionNoPermitidaException {
        if (responsables.stream().anyMatch(x -> x.getDni().equals(r.getDni()))) {
            throw new OperacionNoPermitidaException("Ya existe un responsable con DNI " + r.getDni());
        }
        responsables.add(r);
    }

    public void registrarEquipo(EquipoRespuesta e) throws OperacionNoPermitidaException {
        if (equipos.stream().anyMatch(x -> x.getIdEquipo().equalsIgnoreCase(e.getIdEquipo()))) {
            throw new OperacionNoPermitidaException("Ya existe un equipo con id " + e.getIdEquipo());
        }
        equipos.add(e);
    }

    public String siguienteIdMantenimiento() {
        int total = 0;
        for (EstacionMeteorologica e : getEstaciones()) {
            total += e.getMantenimientos().size();
        }
        return String.format("M-%02d", total + 1);
    }

    public ResultadoLectura registrarLectura(Sensor sensor, double mm, LocalDateTime fechaHora)
            throws LecturaInvalidaException, OperacionNoPermitidaException {
        var medicion = sensor.registrarMedicion(mm, fechaHora);
        ZonaGeografica zona = sensor.getEstacion().getZona();
        Alerta alerta = zona == null ? null : zona.evaluarYAlertar(fechaHora);
        return new ResultadoLectura(medicion, alerta);
    }

    public Alerta registrarAlertaOperador(ZonaGeografica zona, TipoEventoClimatico evento,
                                          NivelAlerta nivel, String descripcion)
            throws OperacionNoPermitidaException {
        if (evento == TipoEventoClimatico.LLUVIA_COPIOSA) {
            throw new OperacionNoPermitidaException("Las alertas de LLUVIA_COPIOSA se generan automáticamente por umbrales");
        }
        return zona.emitirAlerta(evento, nivel, descripcion);
    }

    public List<ZonaGeografica> getZonas() { return Collections.unmodifiableList(zonas); }
    public List<Sensor> getSensores() { return Collections.unmodifiableList(sensores); }
    public List<ResponsableTecnico> getResponsables() { return Collections.unmodifiableList(responsables); }
    public List<EquipoRespuesta> getEquipos() { return Collections.unmodifiableList(equipos); }

    public List<EstacionMeteorologica> getEstaciones() {
        List<EstacionMeteorologica> r = new ArrayList<>();
        for (ZonaGeografica z : zonas) {
            r.addAll(z.getEstaciones());
        }
        return r;
    }

    public List<Sensor> getSensoresInstalados() {
        return sensores.stream().filter(s -> s.getEstacion() != null).toList();
    }

    public List<Sensor> getSensoresLibres() {
        return sensores.stream().filter(s -> s.getEstacion() == null).toList();
    }

    public List<ResponsableTecnico> getResponsablesSinEquipo() {
        return responsables.stream().filter(r -> r.getEquipo() == null).toList();
    }

    public List<Alerta> getAlertas() {
        List<Alerta> r = new ArrayList<>();
        for (ZonaGeografica z : zonas) {
            r.addAll(z.getAlertas());
        }
        r.sort(Comparator.comparing(Alerta::getFechaEmision));
        return r;
    }

    public List<Alerta> getAlertasPendientes() {
        return getAlertas().stream().filter(Alerta::estaPendiente).toList();
    }

    public List<Mantenimiento> getMantenimientos() {
        List<Mantenimiento> r = new ArrayList<>();
        for (EstacionMeteorologica e : getEstaciones()) {
            r.addAll(e.getMantenimientos());
        }
        return r;
    }
}
