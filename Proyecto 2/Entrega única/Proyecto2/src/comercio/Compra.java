package comercio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Compra extends Venta {

    private double iva;
    private List<DetalleCompra> detalles;

    public Compra(Date fecha, double subtotal, double total,
                  int puntosFidelidadGenerados, double iva) {
        super(fecha, subtotal, total, puntosFidelidadGenerados);
        this.iva = iva;
        this.detalles = new ArrayList<>();
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public List<DetalleCompra> getDetalles() {
        return detalles;
    }

    public void agregarDetalleCompra(DetalleCompra detalle) {
        if (detalle != null) {
            detalles.add(detalle);
        }
    }

    public void agregarDetalle(Producto producto, int cantidad) {
        if (producto == null || cantidad <= 0) {
            return;
        }

        if (!producto.hayExistencias(cantidad)) {
            return;
        }

        DetalleCompra detalle = new DetalleCompra(
            cantidad,
            producto.getPrecio()
        );

        detalle.setProducto(producto);
        agregarDetalleCompra(detalle);

        setSubtotal(getSubtotal() + cantidad * producto.getPrecio());
    }
}