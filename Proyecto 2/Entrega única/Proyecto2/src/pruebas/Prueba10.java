package pruebas;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import autenticacion.Socio;
import comercio.AlimentoCafeteria;
import comercio.Bebida;
import comercio.CalculadorPuntos;
import comercio.CuentaPuntos;
import comercio.DetalleVentaCafeteria;
import comercio.Impuestos;
import comercio.ParametroFinanciero;
import comercio.ReglaBebidaCaliente;
import comercio.Snack;
import comercio.VentaCafeteria;
import deportivo.CanchaPadel;
import deportivo.Instalacion;
import deportivo.ReservaInstalacion;

public class Prueba10 {

    private static Date fechaHoy() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static String dinero(double valor) {
        return String.format(Locale.US, "$%,.2f", valor);
    }

    private static void vender(String titulo, Socio socio, AlimentoCafeteria alimento, int cantidad,
            ReglaBebidaCaliente regla, Impuestos impuestos, CalculadorPuntos calculador,
            CuentaPuntos cuenta) {

        System.out.println("\n" + titulo);
        System.out.println("  Socio: " + socio.getNombre() + " pide " + cantidad + " x " + alimento.getNombre());

        List<AlimentoCafeteria> orden = new ArrayList<>();
        orden.add(alimento);

        if (!regla.evaluar(socio, orden)) {
            System.out.println("  >> VENTA RECHAZADA: bebida caliente no permitida con reserva en instalación restringida.");
            return;
        }

        double subtotal = alimento.getPrecio() * cantidad;
        double propina = subtotal * 0.10;
        VentaCafeteria venta = new VentaCafeteria(fechaHoy(), subtotal, 0, 0, 0, propina);
        venta.setComprador(socio);
        DetalleVentaCafeteria detalle = new DetalleVentaCafeteria(cantidad, alimento.getPrecio());
        detalle.setAlimentoCafeteria(alimento);
        venta.agregarDetalleVentaCafeteria(detalle);

        double impuesto = impuestos.calcularMontos(venta);
        venta.calcularTotal(0.0, impuesto + propina);
        int puntos = calculador.calcularPuntos(venta);
        cuenta.sumarPuntos(puntos);

        System.out.println("  Subtotal: " + dinero(subtotal) + " | Impuesto consumo 8%: " + dinero(impuesto)
                + " | Propina 10%: " + dinero(propina));
        System.out.println("  TOTAL: " + dinero(venta.getTotal()) + " | Puntos: " + puntos
                + " | Saldo: " + cuenta.getSaldo());
        System.out.println("  >> VENTA ACEPTADA.");
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================================================");
        System.out.println("   PRUEBA 10: VENTA EN CAFETERÍA, IMPUESTO AL CONSUMO, PROPINA Y REGLA DE BEBIDA CALIENTE");
        System.out.println("==========================================================================================================");

        ParametroFinanciero parametros = new ParametroFinanciero(0.19, 0.08, 0.02, 8.0);
        Impuestos impuestos = new Impuestos(parametros);
        CalculadorPuntos calculador = new CalculadorPuntos(parametros);

        Socio ana = new Socio("s1", "pass", "Ana Torres", fechaHoy());
        Socio luis = new Socio("s2", "pass", "Luis Mora", fechaHoy());
        Socio marta = new Socio("s3", "pass", "Marta Ríos", fechaHoy());
        CuentaPuntos cuentaAna = new CuentaPuntos(0);
        cuentaAna.setSocio(ana);
        CuentaPuntos cuentaLuis = new CuentaPuntos(0);
        cuentaLuis.setSocio(luis);
        CuentaPuntos cuentaMarta = new CuentaPuntos(0);
        cuentaMarta.setSocio(marta);

        // Cancha de pádel techada con bebidas restringidas; cancha abierta sin restricción
        Instalacion padelTechada = new CanchaPadel("Cancha pádel 1", "Pádel", true, true, 4);
        Instalacion padelAbierta = new CanchaPadel("Cancha pádel 2", "Pádel", false, true, 4);

        List<Socio> jugadoresAna = new ArrayList<>();
        jugadoresAna.add(ana);
        jugadoresAna.add(luis);
        List<Socio> jugadoresMarta = new ArrayList<>();
        jugadoresMarta.add(marta);

        List<ReservaInstalacion> reservas = new ArrayList<>();
        reservas.add(new ReservaInstalacion(padelTechada, ana, "08:00-09:00", jugadoresAna, false));
        reservas.add(new ReservaInstalacion(padelAbierta, marta, "08:00-09:00", jugadoresMarta, false));
        ReglaBebidaCaliente regla = new ReglaBebidaCaliente(reservas);

        Bebida cafe = new Bebida("Café americano", 5000, "caliente");
        Bebida limonada = new Bebida("Limonada", 6000, "fria");
        Snack galleta = new Snack("Galletas", 3000);

        vender("CASO 1: bebida fría con reserva en cancha techada (permitida)", ana, limonada, 2,
                regla, impuestos, calculador, cuentaAna);
        vender("CASO 2: snack con reserva en cancha techada (permitido)", luis, galleta, 1,
                regla, impuestos, calculador, cuentaLuis);
        vender("CASO 3: bebida caliente, jugador de cancha techada restringida (RECHAZO)", luis, cafe, 1,
                regla, impuestos, calculador, cuentaLuis);
        vender("CASO 4: bebida caliente, responsable de cancha techada restringida (RECHAZO)", ana, cafe, 1,
                regla, impuestos, calculador, cuentaAna);
        vender("CASO 5: bebida caliente, reserva en cancha NO techada (permitida)", marta, cafe, 2,
                regla, impuestos, calculador, cuentaMarta);

        System.out.println("\n==========================================================================================================");
        System.out.println("Valores esperados caso 1: subtotal $12,000.00 | impuesto $960.00 | propina $1,200.00 | total $14,160.00 | puntos 283");
        System.out.println("==========================================================================================================");
    }
}
