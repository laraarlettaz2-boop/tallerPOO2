package elnino.modelo;

public class ValorHistorico {
    private int mes = 1;
    private double acumuladoMedio;
    private ZonaGeografica zona;

    public ValorHistorico() {
    }

    public ValorHistorico(int mes, double acumuladoMedio) {
        setMes(mes);
        setAcumuladoMedio(acumuladoMedio);
    }

    public int getMes() { return mes; }
    public void setMes(int mes) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12");
        }
        this.mes = mes;
    }
    public double getAcumuladoMedio() { return acumuladoMedio; }
    public void setAcumuladoMedio(double acumuladoMedio) {
        if (acumuladoMedio < 0) {
            throw new IllegalArgumentException("El acumulado medio no puede ser negativo");
        }
        this.acumuladoMedio = acumuladoMedio;
    }
    public ZonaGeografica getZona() { return zona; }
    public void setZona(ZonaGeografica zona) { this.zona = zona; }

    @Override
    public String toString() {
        return "Mes " + mes + ": " + acumuladoMedio + " mm";
    }
}
