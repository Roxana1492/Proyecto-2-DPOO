package comercio;

public class CalculadorPuntos {
    private ParametroFinanciero parametroFinanciero;

    public CalculadorPuntos(ParametroFinanciero parametroFinanciero) {
        this.parametroFinanciero = parametroFinanciero;
    }

    public ParametroFinanciero getParametroFinanciero() {
        return parametroFinanciero;
    }

    public int calcularPuntos(Venta venta) {
        if (venta == null || parametroFinanciero == null) {
            return 0;
        }

        int puntos = (int) (venta.getTotal()
                * parametroFinanciero.getTasaPuntos());

        venta.setPuntosFidelidadGenerados(puntos);

        return puntos;
    }

    public double convertirPuntos(int puntos) {
        if (puntos <= 0) {
            return 0.0;
        }

        return puntos;
    }
}