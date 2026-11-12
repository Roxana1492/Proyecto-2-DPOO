package comercio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VentaCafeteria extends Venta {
    private double impuestoConsumo;
    private double propina;
    private List<DetalleVentaCafeteria> detalles;

    public VentaCafeteria(Date fecha, double subtotal, double total, int puntosFidelidadGenerados, double impuestoConsumo, double propina) {
        super(fecha, subtotal, total, puntosFidelidadGenerados);
        this.impuestoConsumo = impuestoConsumo;
        this.propina = propina;
        this.detalles = new ArrayList<>();
    }

    public double getImpuestoConsumo() { return impuestoConsumo; }
    public void setImpuestoConsumo(double impuestoConsumo) { this.impuestoConsumo = impuestoConsumo; }
    public double getPropina() { return propina; }
    public void setPropina(double propina) { this.propina = propina; }
    public List<DetalleVentaCafeteria> getDetalles() { return detalles; }
    public void agregarDetalleVentaCafeteria(DetalleVentaCafeteria x) { this.detalles.add(x); }
}
