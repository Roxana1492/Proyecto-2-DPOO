package comercio;

public class GestorDescuentos {

    private ParametroFinanciero parametroFinanciero;

    public GestorDescuentos(ParametroFinanciero parametroFinanciero) {
        this.parametroFinanciero = parametroFinanciero;
    }

    public ParametroFinanciero getParametroFinanciero() {
        return parametroFinanciero;
    }

    public double calcularDescuento(CodigoDescuento codigo, Compra compra) {
        if (codigo == null || compra == null) {
            return 0.0;
        }

        if (parametroFinanciero == null) {
            return 0.0;
        }

        double subtotal = compra.getSubtotal();
        double porcentaje = codigo.getPorcentajeCompartido();

        if (codigo.getPropietario() != null && compra.getComprador() != null
                && codigo.getPropietario().getLogin().equals(compra.getComprador().getLogin())) {
            porcentaje = codigo.getPorcentajeEmpleado();
        }

        if (subtotal <= 0 || porcentaje <= 0) {
            return 0.0;
        }

        if (porcentaje > 100) {
            porcentaje = 100;
        }

        return subtotal * porcentaje / 100.0;
    }
}