package comercio;

public class Inventario {

    public Inventario() {
    }

    public void moverArticulo(Producto producto, UbicacionInventario origen, UbicacionInventario destino) {
        if (producto == null || origen == null || destino == null) {
            return;
        }

        if (mismaUbicacion(origen, destino)) {
            return;
        }

        if (producto.getUbicacionInventario() == null
                || !mismaUbicacion(producto.getUbicacionInventario(), origen)) {
            return;
        }

        if (producto.getCantidadDisponible() <= 0) {
            return;
        }

        producto.setUbicacionInventario(destino);
    }

    private boolean mismaUbicacion(UbicacionInventario a, UbicacionInventario b) {
        if (a == b) {
            return true;
        }
        if (a.getNombre() == null || b.getNombre() == null) {
            return false;
        }
        return a.getNombre().trim().equalsIgnoreCase(b.getNombre().trim());
    }
}