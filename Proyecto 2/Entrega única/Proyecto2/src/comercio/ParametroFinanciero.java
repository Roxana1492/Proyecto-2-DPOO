package comercio;

public class ParametroFinanciero {
    private double iva;
    private double impuestoConsumo;
    private double tasaPuntos;
    private double porcentajeDescuento;

    public ParametroFinanciero(double iva, double impuestoConsumo, double tasaPuntos, double porcentajeDescuento) {
        this.iva = iva;
        this.impuestoConsumo = impuestoConsumo;
        this.tasaPuntos = tasaPuntos;
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }
    public double getImpuestoConsumo() { return impuestoConsumo; }
    public void setImpuestoConsumo(double impuestoConsumo) { this.impuestoConsumo = impuestoConsumo; }
    public double getTasaPuntos() { return tasaPuntos; }
    public void setTasaPuntos(double tasaPuntos) { this.tasaPuntos = tasaPuntos; }
    public double getPorcentajeDescuento() { return porcentajeDescuento; }
    public void setPorcentajeDescuento(double porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }
}
