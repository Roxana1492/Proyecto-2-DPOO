package pruebas;

import java.util.Calendar;
import java.util.Date;

import comercio.Compra;
import comercio.GeneradorReportes;
import comercio.VentaCafeteria;

public class Prueba12 {

    private static Date haceDias(int dias) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -dias);
        return c.getTime();
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================================================");
        System.out.println("   PRUEBA 12: REPORTE DE VENTAS POR TIPO Y RANGO DE FECHAS");
        System.out.println("==========================================================================================================");

        GeneradorReportes generador = new GeneradorReportes();

        // Tienda: hoy y hace 10 días
        generador.getVentas().add(new Compra(new Date(), 100000, 119000, 2380, 19000));
        generador.getVentas().add(new Compra(haceDias(10), 50000, 59500, 1190, 9500));
        // Cafetería: hoy y hace 2 días
        generador.getVentas().add(new VentaCafeteria(new Date(), 10000, 11800, 236, 800, 1000));
        generador.getVentas().add(new VentaCafeteria(haceDias(2), 20000, 23600, 472, 1600, 2000));

        Date desdeSemana = haceDias(7);
        Date hoy = new Date();

        String[] tipos = { "tienda", "cafeteria", "todos" };
        for (String tipo : tipos) {
            System.out.println("\n--- Últimos 7 días, tipo: " + tipo + " ---");
            System.out.println(generador.generarReporte(tipo, desdeSemana, hoy));
        }

        System.out.println("--- Últimos 30 días, tipo: todos ---");
        System.out.println(generador.generarReporte("todos", haceDias(30), hoy));

        System.out.println("--- Tipo inválido ---");
        System.out.println(generador.generarReporte("inventario", desdeSemana, hoy));

        System.out.println("\n--- Rango invertido ---");
        System.out.println(generador.generarReporte("todos", hoy, desdeSemana));

        System.out.println("\n==========================================================================================================");
        System.out.println("Esperado (7 días): tienda 1 venta $119000.00 | cafeteria 2 ventas $35400.00 | todos 3 ventas $154400.00");
        System.out.println("Esperado (30 días): todos 4 ventas $213900.00");
        System.out.println("==========================================================================================================");
    }
}
