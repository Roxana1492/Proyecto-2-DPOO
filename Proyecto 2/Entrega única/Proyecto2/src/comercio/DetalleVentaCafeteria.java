package comercio;

public class DetalleVentaCafeteria {
    private int cantidad;
    private double precioUnitario;
    private AlimentoCafeteria alimentoCafeteria;

    public DetalleVentaCafeteria(int cantidad, double precioUnitario) {
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public AlimentoCafeteria getAlimentoCafeteria() { return alimentoCafeteria; }
    public void setAlimentoCafeteria(AlimentoCafeteria alimentoCafeteria) { this.alimentoCafeteria = alimentoCafeteria; }
}
