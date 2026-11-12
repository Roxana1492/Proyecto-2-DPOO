package comercio;

public class Impuestos {

    private ParametroFinanciero parametroFinanciero;

    public Impuestos(ParametroFinanciero parametroFinanciero) {
        this.parametroFinanciero = parametroFinanciero;
    }

    public ParametroFinanciero getParametroFinanciero() {
        return parametroFinanciero;
    }

    public double calcularMontos(Venta venta) {
        if (venta == null || parametroFinanciero == null) {
            return 0.0;
        }

        double subtotal = venta.getSubtotal();

        if (subtotal <= 0) {
            return 0.0;
        }

        if (venta instanceof Compra) {
            double monto = subtotal * parametroFinanciero.getIva();
            ((Compra) venta).setIva(monto);
            return monto;
        } else if (venta instanceof VentaCafeteria) {
            double monto = subtotal * parametroFinanciero.getImpuestoConsumo();
            ((VentaCafeteria) venta).setImpuestoConsumo(monto);
            return monto;
        }

        return 0.0;
    }
}