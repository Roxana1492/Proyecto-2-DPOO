package pruebas;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import autenticacion.Autenticador;
import autenticacion.ConsolaMenu;
import autenticacion.CoordinadorAplicacion;
import autenticacion.Empleado;
import autenticacion.Socio;
import comercio.AlimentoCafeteria;
import comercio.Bebida;
import comercio.CalculadorPuntos;
import comercio.CodigoDescuento;
import comercio.CuentaPuntos;
import comercio.GeneradorReportes;
import comercio.GestorDescuentos;
import comercio.Impuestos;
import comercio.ParametroFinanciero;
import comercio.Producto;
import comercio.ReglaBebidaCaliente;
import comercio.RegistroTurnos;
import comercio.Snack;
import deportivo.Turno;
import persistencia.AlmacenadorArchivos;

/**
 * Punto de entrada de la aplicación de consola, con datos de ejemplo.
 * Usuarios de prueba: socio1 / 1234 (socio) y empleado1 / 1234 (empleado).
 */
public class Principal {

    public static void main(String[] args) {
        ConsolaMenu consola = new ConsolaMenu();
        Autenticador autenticador = new Autenticador();

        ParametroFinanciero parametros = new ParametroFinanciero(0.19, 0.08, 0.02, 8.0);
        GestorDescuentos gestorDescuentos = new GestorDescuentos(parametros);
        Impuestos impuestos = new Impuestos(parametros);
        CalculadorPuntos calculadorPuntos = new CalculadorPuntos(parametros);
        ReglaBebidaCaliente reglaBebidaCaliente = new ReglaBebidaCaliente();
        RegistroTurnos registroTurnos = new RegistroTurnos();
        GeneradorReportes generadorReportes = new GeneradorReportes();

        CoordinadorAplicacion coordinador = new CoordinadorAplicacion(consola, autenticador,
                gestorDescuentos, impuestos, calculadorPuntos, reglaBebidaCaliente,
                registroTurnos, generadorReportes);

        // Usuarios
        Socio socio1 = new Socio("socio1", "1234", "Carlos Pérez", new Date());
        Socio socio2 = new Socio("socio2", "1234", "Ana Torres", new Date());
        Empleado empleado1 = new Empleado("empleado1", "1234", "Laura Gómez");
        autenticador.registrarUsuario(socio1);
        autenticador.registrarUsuario(socio2);
        autenticador.registrarUsuario(empleado1);

        coordinador.registrarSocio(socio1);
        coordinador.registrarSocio(socio2);

        Map<String, Socio> sociosPorLogin = new HashMap<>();
        sociosPorLogin.put(socio1.getLogin(), socio1);
        sociosPorLogin.put(socio2.getLogin(), socio2);

        // Puntos y productos: se cargan de los archivos si existen; si no, datos de ejemplo
        List<CuentaPuntos> cuentas = new ArrayList<>();
        List<Producto> productos = new ArrayList<>();
        try {
            cuentas = AlmacenadorArchivos.cargarCuentasPuntos("datos/cuentas.txt", sociosPorLogin);
            productos = AlmacenadorArchivos.cargarProductos("datos/productos.txt");
        } catch (Exception e) {
            consola.mostrar("Aviso: no se pudieron cargar los archivos de datos. Se usan datos de ejemplo.");
            cuentas = new ArrayList<>();
            productos = new ArrayList<>();
        }

        if (cuentas.isEmpty()) {
            CuentaPuntos cuenta1 = new CuentaPuntos(0);
            cuenta1.setSocio(socio1);
            CuentaPuntos cuenta2 = new CuentaPuntos(0);
            cuenta2.setSocio(socio2);
            cuentas.add(cuenta1);
            cuentas.add(cuenta2);
        }
        for (CuentaPuntos cuenta : cuentas) {
            coordinador.registrarCuentaPuntos(cuenta.getSocio(), cuenta);
        }

        if (productos.isEmpty()) {
            productos.add(new Producto("Raqueta de pádel", "Raquetas", 200000, 5));
            productos.add(new Producto("Tubo de pelotas", "Pelotas", 5000, 20));
            productos.add(new Producto("Camiseta del club", "Ropa", 60000, 10));
        }
        for (Producto producto : productos) {
            coordinador.registrarProducto(producto);
        }

        // Cafetería
        AlimentoCafeteria cafe = new Bebida("Café americano", 5000, "caliente");
        AlimentoCafeteria limonada = new Bebida("Limonada", 6000, "fria");
        AlimentoCafeteria galletas = new Snack("Galletas", 3000);
        coordinador.registrarAlimento(cafe);
        coordinador.registrarAlimento(limonada);
        coordinador.registrarAlimento(galletas);

        // Código de descuento del empleado (15 % para él, 8 % compartido)
        CodigoDescuento codigo = new CodigoDescuento(15, 8);
        codigo.setPropietario(empleado1);
        coordinador.registrarCodigoDescuento(codigo);

        // Turnos: sábado con 3 entrenadores y 1 fisioterapeuta
        coordinador.registrarTurno(new Turno("T1", "e1", "Entrenador 1", "Sabado", "08:00-12:00", "Gimnasio", "ENTRENADOR"));
        coordinador.registrarTurno(new Turno("T2", "e2", "Entrenador 2", "Sabado", "08:00-12:00", "Gimnasio", "ENTRENADOR"));
        coordinador.registrarTurno(new Turno("T3", "e3", "Entrenador 3", "Sabado", "08:00-12:00", "Gimnasio", "ENTRENADOR"));
        coordinador.registrarTurno(new Turno("T4", "f1", "Fisio 1", "Sabado", "08:00-12:00", "Fisioterapia", "FISIOTERAPEUTA"));

        coordinador.iniciar();
    }
}
