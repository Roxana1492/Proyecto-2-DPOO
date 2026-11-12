package pruebas;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import autenticacion.Empleado;
import autenticacion.Socio;
import comercio.CalculadorPuntos;
import comercio.CodigoDescuento;
import comercio.Compra;
import comercio.CuentaPuntos;
import comercio.GestorDescuentos;
import comercio.Impuestos;
import comercio.ParametroFinanciero;
import comercio.Producto;

public class Prueba9 {

    private static Date fechaHoy() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static String dinero(double valor) {
        return String.format(Locale.US, "$%,.2f", valor);
    }

    // Sigue el diagrama de secuencia 5.2: primero se verifican las existencias de TODOS los
    // artículos; si alguno falla, la compra se rechaza completa y no se modifica nada.
    private static void ejecutarCompra(String titulo, Socio socio, CodigoDescuento codigo,
            Producto[] productos, int[] cantidades, CuentaPuntos cuenta,
            GestorDescuentos gestor, Impuestos impuestos, CalculadorPuntos calculador) {

        System.out.println("\n" + titulo);
        System.out.println("Comprador: " + socio.getNombre());

        // 1. Verificar existencias de cada artículo
        for (int i = 0; i < productos.length; i++) {
            System.out.println("  Pide " + cantidades[i] + " x " + productos[i].getNombre()
                    + " (disponibles: " + productos[i].getCantidadDisponible() + ")");
            if (!productos[i].hayExistencias(cantidades[i])) {
                System.out.println("  >> COMPRA RECHAZADA: no hay existencias suficientes de "
                        + productos[i].getNombre() + ". No se modificó el inventario.");
                return;
            }
        }

        // 2. Crear la compra y agregar los detalles
        Compra compra = new Compra(fechaHoy(), 0, 0, 0, 0);
        compra.setComprador(socio);
        for (int i = 0; i < productos.length; i++) {
            compra.agregarDetalle(productos[i], cantidades[i]);
        }

        // 3. Descuento, impuesto, total y puntos
        double descuento = gestor.calcularDescuento(codigo, compra);
        double impuesto = impuestos.calcularMontos(compra);
        compra.calcularTotal(descuento, impuesto);
        int puntos = calculador.calcularPuntos(compra);
        cuenta.sumarPuntos(puntos);

        // 4. Descontar del inventario
        for (int i = 0; i < productos.length; i++) {
            productos[i].descontar(cantidades[i]);
        }

        System.out.println("  Subtotal:  " + dinero(compra.getSubtotal()));
        System.out.println("  Descuento: " + dinero(descuento));
        System.out.println("  IVA:       " + dinero(impuesto));
        System.out.println("  TOTAL:     " + dinero(compra.getTotal()));
        System.out.println("  Puntos ganados: " + puntos + " | Saldo de puntos: " + cuenta.getSaldo());
        System.out.println("  >> COMPRA ACEPTADA.");
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================================================");
        System.out.println("   PRUEBA 9: COMPRA EN LA TIENDA CON DESCUENTO, IVA Y PUNTOS, Y RECHAZO POR FALTA DE EXISTENCIAS");
        System.out.println("==========================================================================================================");

        // Parámetros financieros: IVA 19%, impuesto al consumo 8%, puntos 2%, descuento compartido 8
        ParametroFinanciero parametros = new ParametroFinanciero(0.19, 0.08, 0.02, 8.0);
        GestorDescuentos gestor = new GestorDescuentos(parametros);
        Impuestos impuestos = new Impuestos(parametros);
        CalculadorPuntos calculador = new CalculadorPuntos(parametros);

        // Empleado dueño del código y socio con quien lo comparte (8%)
        Empleado empleado = new Empleado("emp1", "pass", "Laura Gómez");
        CodigoDescuento codigo = new CodigoDescuento(15, 8);
        codigo.setPropietario(empleado);

        Socio socio = new Socio("s1", "pass", "Carlos Pérez", fechaHoy());
        CuentaPuntos cuenta = new CuentaPuntos(0);
        cuenta.setSocio(socio);

        // Inventario inicial
        Producto raqueta = new Producto("Raqueta de pádel", "Raquetas", 200000, 5);
        Producto pelotas = new Producto("Tubo de pelotas", "Pelotas", 5000, 3);

        System.out.println("\nINVENTARIO INICIAL");
        System.out.println("  " + raqueta.getNombre() + ": " + raqueta.getCantidadDisponible());
        System.out.println("  " + pelotas.getNombre() + ": " + pelotas.getCantidadDisponible());

        // Caso A: compra válida con código compartido (8%)
        ejecutarCompra("CASO A: compra válida con código de descuento compartido",
                socio, codigo,
                new Producto[] { raqueta, pelotas }, new int[] { 1, 2 },
                cuenta, gestor, impuestos, calculador);

        System.out.println("\nINVENTARIO TRAS LA COMPRA A");
        System.out.println("  " + raqueta.getNombre() + ": " + raqueta.getCantidadDisponible());
        System.out.println("  " + pelotas.getNombre() + ": " + pelotas.getCantidadDisponible());

        // Caso B: se piden más pelotas de las que quedan -> rechazo completo
        ejecutarCompra("CASO B: compra rechazada por falta de existencias",
                socio, codigo,
                new Producto[] { raqueta, pelotas }, new int[] { 1, 10 },
                cuenta, gestor, impuestos, calculador);

        System.out.println("\nINVENTARIO TRAS EL INTENTO B (debe ser igual al anterior)");
        System.out.println("  " + raqueta.getNombre() + ": " + raqueta.getCantidadDisponible());
        System.out.println("  " + pelotas.getNombre() + ": " + pelotas.getCantidadDisponible());

        // Caso C: el propietario del código (empleado que también es socio) obtiene el 15%
        Socio socioEmpleado = new Socio("emp1", "pass", "Laura Gómez (socia)", fechaHoy());
        CuentaPuntos cuentaEmpleado = new CuentaPuntos(0);
        cuentaEmpleado.setSocio(socioEmpleado);
        ejecutarCompra("CASO C: el propietario del código lo usa (descuento de empleado 15%)",
                socioEmpleado, codigo,
                new Producto[] { raqueta }, new int[] { 1 },
                cuentaEmpleado, gestor, impuestos, calculador);

        System.out.println("\n==========================================================================================================");
        System.out.println("Valores esperados del caso A: subtotal $210,000.00 | descuento $16,800.00 | IVA $39,900.00 | total $233,100.00 | puntos 4662 | caso C: descuento $30,000.00 (15%)");
        System.out.println("==========================================================================================================");
    }
}
