package comercio;

import autenticacion.Socio;
import java.util.Date;

public class Venta {
    private Date fecha;
    private double subtotal;
    private double total;
    private int puntosFidelidadGenerados;
    private Socio comprador;

    public Venta(Date fecha, double subtotal, double total, int puntosFidelidadGenerados) {
        this.fecha = fecha;
        this.subtotal = subtotal;
        this.total = total;
        this.puntosFidelidadGenerados = puntosFidelidadGenerados;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getPuntosFidelidadGenerados() {
        return puntosFidelidadGenerados;
    }

    public void setPuntosFidelidadGenerados(int puntosFidelidadGenerados) {
        this.puntosFidelidadGenerados = puntosFidelidadGenerados;
    }

    public Socio getComprador() {
        return comprador;
    }

    public void setComprador(Socio comprador) {
        this.comprador = comprador;
    }

    public void calcularTotal(double descuento, double impuesto) {
        this.total = this.subtotal - descuento + impuesto;
    }
}