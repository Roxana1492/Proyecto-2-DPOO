package comercio;

public class Producto {
    private String nombre;
    private String categoria;
    private double precio;
    private int cantidadDisponible;
    private UbicacionInventario ubicacionInventario;

    public Producto(String nombre, String categoria, double precio, int cantidadDisponible) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidadDisponible = cantidadDisponible;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public UbicacionInventario getUbicacionInventario() {
        return ubicacionInventario;
    }

    public void setUbicacionInventario(UbicacionInventario ubicacionInventario) {
        this.ubicacionInventario = ubicacionInventario;
    }

    public boolean hayExistencias(int cantidad) {
        return cantidad > 0 && cantidadDisponible >= cantidad;
    }

    public void descontar(int cantidad) {
        if (hayExistencias(cantidad)) {
            cantidadDisponible -= cantidad;
        }
    }
}
