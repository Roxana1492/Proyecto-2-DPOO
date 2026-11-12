package comercio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GeneradorReportes {

    private List<Venta> ventas;

    public GeneradorReportes() {
        this.ventas = new ArrayList<>();
    }

    public GeneradorReportes(List<Venta> ventas) {
        this.ventas = (ventas == null) ? new ArrayList<>() : new ArrayList<>(ventas);
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = (ventas == null) ? new ArrayList<>() : new ArrayList<>(ventas);
    }

    public String generarReporte(String tipo, Date desde, Date hasta) {
        if (tipo == null || tipo.trim().isEmpty()) {
            return "Error: debe especificar el tipo de reporte.";
        }

        if (desde == null || hasta == null) {
            return "Error: debe especificar las fechas de inicio y fin.";
        }

        if (desde.after(hasta)) {
            return "Error: la fecha de inicio no puede ser posterior a la fecha final.";
        }

        String tipoReporte = tipo.trim().toLowerCase();

        if (!tipoReporte.equals("tienda")
                && !tipoReporte.equals("cafeteria")
                && !tipoReporte.equals("ventas")
                && !tipoReporte.equals("todos")) {
            return "Error: tipo de reporte no válido. Use tienda, cafeteria, ventas o todos.";
        }

        // El día final se incluye completo (hasta las 23:59:59)
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(hasta);
        calendario.set(Calendar.HOUR_OF_DAY, 23);
        calendario.set(Calendar.MINUTE, 59);
        calendario.set(Calendar.SECOND, 59);
        calendario.set(Calendar.MILLISECOND, 999);
        Date finDelDia = calendario.getTime();

        int cantidadVentas = 0;
        double totalVentas = 0.0;

        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

        StringBuilder reporte = new StringBuilder();

        reporte.append("REPORTE DE VENTAS\n");
        reporte.append("------------------------------\n");
        reporte.append("Tipo: ").append(tipoReporte).append("\n");
        reporte.append("Desde: ").append(formatoFecha.format(desde)).append("\n");
        reporte.append("Hasta: ").append(formatoFecha.format(hasta)).append("\n\n");

        for (Venta venta : ventas) {
            if (venta == null || venta.getFecha() == null) {
                continue;
            }

            Date fechaVenta = venta.getFecha();

            if (fechaVenta.before(desde) || fechaVenta.after(finDelDia)) {
                continue;
            }

            boolean incluirVenta;

            if (tipoReporte.equals("tienda")) {
                incluirVenta = venta instanceof Compra;
            } else if (tipoReporte.equals("cafeteria")) {
                incluirVenta = venta instanceof VentaCafeteria;
            } else {
                incluirVenta = venta instanceof Compra
                        || venta instanceof VentaCafeteria;
            }

            if (incluirVenta) {
                cantidadVentas++;
                totalVentas += venta.getTotal();
            }
        }

        reporte.append("Cantidad de ventas: ")
                .append(cantidadVentas)
                .append("\n");

        reporte.append(String.format(
                java.util.Locale.US,
                "Total vendido: $%.2f%n",
                totalVentas
        ));

        if (cantidadVentas == 0) {
            reporte.append("\nNo se encontraron ventas para los criterios indicados.");
        }

        return reporte.toString();
    }
}