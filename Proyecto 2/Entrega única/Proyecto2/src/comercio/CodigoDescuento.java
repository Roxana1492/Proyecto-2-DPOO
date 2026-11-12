package comercio;

import autenticacion.Empleado;

public class CodigoDescuento {
    private double porcentajeEmpleado;
    private double porcentajeCompartido;
    private Empleado propietario;

    public CodigoDescuento(double porcentajeEmpleado, double porcentajeCompartido) {
        this.porcentajeEmpleado = porcentajeEmpleado;
        this.porcentajeCompartido = porcentajeCompartido;
    }

    public double getPorcentajeEmpleado() { return porcentajeEmpleado; }
    public void setPorcentajeEmpleado(double porcentajeEmpleado) { this.porcentajeEmpleado = porcentajeEmpleado; }
    public double getPorcentajeCompartido() { return porcentajeCompartido; }
    public void setPorcentajeCompartido(double porcentajeCompartido) { this.porcentajeCompartido = porcentajeCompartido; }
    public Empleado getPropietario() { return propietario; }
    public void setPropietario(Empleado propietario) { this.propietario = propietario; }
}
