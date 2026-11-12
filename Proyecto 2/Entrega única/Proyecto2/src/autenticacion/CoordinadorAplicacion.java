
package autenticacion;

import comercio.*;
import persistencia.AlmacenadorArchivos;
import deportivo.SolicitudCambioTurno;
import deportivo.Turno;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CoordinadorAplicacion {

    private static final int MAX_INTENTOS = 3;

    private ConsolaMenu consolaMenu;
    private Autenticador autenticador;
    private boolean sesionActiva;
    private Usuario usuarioActual;

    private GestorDescuentos gestorDescuentos;
    private Impuestos impuestos;
    private CalculadorPuntos calculadorPuntos;
    private ReglaBebidaCaliente reglaBebidaCaliente;
    private RegistroTurnos registroTurnos;
    private GeneradorReportes generadorReportes;

    private Map<String, Producto> catalogo;
    private List<Compra> compras;
    private List<VentaCafeteria> ventasCafeteria;

    private List<Socio> socios;
    private List<AlimentoCafeteria> alimentos;
    private List<CodigoDescuento> codigosDescuento;

    private Map<Socio, CuentaPuntos> cuentasPuntos;

    public CoordinadorAplicacion(
            ConsolaMenu consolaMenu,
            Autenticador autenticador) {
        this(consolaMenu, autenticador, null, null, null,
                null, null, null);
    }

    public CoordinadorAplicacion(
            ConsolaMenu consolaMenu,
            Autenticador autenticador,
            GestorDescuentos gestorDescuentos,
            Impuestos impuestos,
            CalculadorPuntos calculadorPuntos,
            ReglaBebidaCaliente reglaBebidaCaliente,
            RegistroTurnos registroTurnos,
            GeneradorReportes generadorReportes) {

        this.consolaMenu = consolaMenu;
        this.autenticador = autenticador;
        this.gestorDescuentos = gestorDescuentos;
        this.impuestos = impuestos;
        this.calculadorPuntos = calculadorPuntos;
        this.reglaBebidaCaliente = reglaBebidaCaliente;
        this.registroTurnos = registroTurnos;
        this.generadorReportes = generadorReportes;

        this.sesionActiva = false;

        this.catalogo = new LinkedHashMap<>();
        this.compras = new ArrayList<>();
        this.ventasCafeteria = new ArrayList<>();

        this.socios = new ArrayList<>();
        this.alimentos = new ArrayList<>();
        this.codigosDescuento = new ArrayList<>();

        this.cuentasPuntos = new HashMap<>();
    }

    public ConsolaMenu getConsolaMenu() {
        return consolaMenu;
    }

    public Autenticador getAutenticador() {
        return autenticador;
    }

    // Métodos para cargar datos en memoria

    public void registrarProducto(Producto producto) {
        if (producto != null && producto.getNombre() != null) {
            catalogo.put(
                    producto.getNombre().trim().toLowerCase(),
                    producto);
        }
    }

    public void registrarSocio(Socio socio) {
        if (socio != null && !socios.contains(socio)) {
            socios.add(socio);
        }
    }

    public void registrarAlimento(AlimentoCafeteria alimento) {
        if (alimento != null && !alimentos.contains(alimento)) {
            alimentos.add(alimento);
        }
    }

    public void registrarCodigoDescuento(CodigoDescuento codigo) {
        if (codigo != null) {
            codigosDescuento.add(codigo);
        }
    }

    public void registrarTurno(Turno turno) {
        if (turno != null && registroTurnos != null) {
            registroTurnos.agregarTurno(turno);
        }
    }

    public void registrarCuentaPuntos(Socio socio, CuentaPuntos cuenta) {
        if (socio != null && cuenta != null) {
            cuenta.setSocio(socio);
            cuentasPuntos.put(socio, cuenta);
        }
    }

    // Inicio de sesión y menú

    public void iniciar() {
        if (consolaMenu == null || autenticador == null) {
            return;
        }

        consolaMenu.mostrar("=== Bienvenido al sistema del club ===");

        Usuario usuario = null;
        int intentos = 0;

        while (usuario == null && intentos < MAX_INTENTOS) {
            consolaMenu.mostrar("Ingrese su usuario:");
            String login = consolaMenu.leerEntrada();

            consolaMenu.mostrar("Ingrese su contraseña:");
            String password = consolaMenu.leerEntrada();

            usuario = autenticador.autenticar(login, password);
            intentos++;

            if (usuario == null) {
                consolaMenu.mostrar(
                        "Inicio de sesión rechazado. Verifique sus credenciales.");
            }
        }

        if (usuario == null) {
            consolaMenu.mostrar(
                    "Se superó el número de intentos. Cerrando la aplicación.");
            return;
        }

        usuarioActual = usuario;
        sesionActiva = true;
        consolaMenu.mostrar("Inicio de sesión exitoso.");

        while (sesionActiva) {
            consolaMenu.mostrar("\n=== MENÚ PRINCIPAL ===");
            consolaMenu.mostrar("1. Comprar en la tienda");
            consolaMenu.mostrar("2. Venta en la cafetería");
            consolaMenu.mostrar("3. Solicitar cambio de turno");
            consolaMenu.mostrar("4. Generar reporte de ventas");
            consolaMenu.mostrar("0. Cerrar sesión");
            consolaMenu.mostrar("Seleccione una opción:");

            String entrada = consolaMenu.leerEntrada();

            try {
                int opcion = Integer.parseInt(entrada.trim());
                navegar(opcion);
            } catch (NumberFormatException e) {
                consolaMenu.mostrar("Opción inválida. Ingrese un número.");
            }
        }
    }

    public void navegar(int opcion) {
        if (!sesionActiva) {
            consolaMenu.mostrar("Debe iniciar sesión primero.");
            return;
        }

        switch (opcion) {
            case 1:
                comprarEnTienda();
                break;
            case 2:
                venderEnCafeteria();
                break;
            case 3:
                solicitarCambioTurno();
                break;
            case 4:
                generarReporte();
                break;
            case 0:
                sesionActiva = false;
                usuarioActual = null;
                consolaMenu.mostrar("Sesión cerrada correctamente.");
                break;
            default:
                consolaMenu.mostrar(
                        "Opción no válida. Seleccione una opción disponible.");
        }
    }

    // Compra en tienda

    private void comprarEnTienda() {
        if (!(usuarioActual instanceof Socio)) {
            consolaMenu.mostrar(
                    "Solo los socios pueden comprar en la tienda.");
            return;
        }

        if (gestorDescuentos == null || impuestos == null
                || calculadorPuntos == null) {
            consolaMenu.mostrar(
                    "Los servicios de compra no están configurados.");
            return;
        }

        Socio socio = (Socio) usuarioActual;

        if (catalogo.isEmpty()) {
            consolaMenu.mostrar("No hay productos registrados.");
            return;
        }

        consolaMenu.mostrar("=== PRODUCTOS DISPONIBLES ===");
        List<Producto> productos = new ArrayList<>(catalogo.values());

        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            consolaMenu.mostrar((i + 1) + ". " + p.getNombre()
                    + " | Precio: " + p.getPrecio()
                    + " | Existencias: " + p.getCantidadDisponible());
        }

        int seleccion = leerEntero("Seleccione el producto:");
        if (seleccion < 1 || seleccion > productos.size()) {
            consolaMenu.mostrar("Selección inválida.");
            return;
        }

        Producto producto = productos.get(seleccion - 1);
        int cantidad = leerEntero("Cantidad:");

        if (!producto.hayExistencias(cantidad)) {
            consolaMenu.mostrar(
                    "No hay existencias suficientes para esa cantidad.");
            return;
        }

        Compra compra = new Compra(new Date(), 0.0, 0.0, 0, 0.0);
        compra.setComprador(socio);
        compra.agregarDetalle(producto, cantidad);

        if (compra.getDetalles().isEmpty()) {
            consolaMenu.mostrar("No fue posible agregar el producto.");
            return;
        }

        double descuento = 0.0;

        if (!codigosDescuento.isEmpty()) {
            consolaMenu.mostrar("¿Desea usar un código de descuento?");
            consolaMenu.mostrar("0. No usar código");

            for (int i = 0; i < codigosDescuento.size(); i++) {
                consolaMenu.mostrar(""
                        + (i + 1) + ". Código disponible " + (i + 1));
            }

            int codigoElegido = leerEntero("Seleccione una opción:");

            if (codigoElegido > 0
                    && codigoElegido <= codigosDescuento.size()) {
                CodigoDescuento codigo =
                        codigosDescuento.get(codigoElegido - 1);
                descuento =
                        gestorDescuentos.calcularDescuento(codigo, compra);
            }
        }

        double impuesto = impuestos.calcularMontos(compra);

        compra.setIva(impuesto);
        compra.calcularTotal(descuento, impuesto);

        int puntos = calculadorPuntos.calcularPuntos(compra);
        compra.setPuntosFidelidadGenerados(puntos);

        // El inventario solo cambia después de validar y calcular la compra.
        producto.descontar(cantidad);

        CuentaPuntos cuenta = cuentasPuntos.get(socio);
        if (cuenta != null) {
            cuenta.sumarPuntos(puntos);
        }

        compras.add(compra);
        registrarVenta(compra);

        consolaMenu.mostrar("=== COMPRA REGISTRADA ===");
        consolaMenu.mostrar("Socio: " + socio.getNombre());
        consolaMenu.mostrar("Producto: " + producto.getNombre());
        consolaMenu.mostrar("Cantidad: " + cantidad);
        consolaMenu.mostrar("Subtotal: " + compra.getSubtotal());
        consolaMenu.mostrar("Descuento: " + descuento);
        consolaMenu.mostrar("Impuesto: " + impuesto);
        consolaMenu.mostrar("Total: " + compra.getTotal());
        consolaMenu.mostrar("Puntos generados: " + puntos);
    }

    // Venta en cafetería: un alimento por operación

    private void venderEnCafeteria() {
        if (reglaBebidaCaliente == null
                || impuestos == null
                || calculadorPuntos == null) {
            consolaMenu.mostrar(
                    "Los servicios de cafetería no están configurados.");
            return;
        }

        if (socios.isEmpty()) {
            consolaMenu.mostrar("No hay socios registrados.");
            return;
        }

        if (alimentos.isEmpty()) {
            consolaMenu.mostrar("No hay alimentos registrados.");
            return;
        }

        consolaMenu.mostrar("=== SELECCIONE EL SOCIO ===");
        for (int i = 0; i < socios.size(); i++) {
            consolaMenu.mostrar((i + 1) + ". "
                    + socios.get(i).getNombre());
        }

        int indiceSocio = leerEntero("Número del socio:");
        if (indiceSocio < 1 || indiceSocio > socios.size()) {
            consolaMenu.mostrar("Selección inválida.");
            return;
        }

        Socio socio = socios.get(indiceSocio - 1);

        consolaMenu.mostrar("=== MENÚ DE CAFETERÍA ===");
        for (int i = 0; i < alimentos.size(); i++) {
            AlimentoCafeteria alimento = alimentos.get(i);
            consolaMenu.mostrar((i + 1) + ". "
                    + alimento.getNombre()
                    + " | Precio: " + alimento.getPrecio());
        }

        int indiceAlimento = leerEntero("Número del alimento:");
        if (indiceAlimento < 1 || indiceAlimento > alimentos.size()) {
            consolaMenu.mostrar("Selección inválida.");
            return;
        }

        AlimentoCafeteria alimento = alimentos.get(indiceAlimento - 1);
        int cantidad = leerEntero("Cantidad:");

        if (cantidad <= 0) {
            consolaMenu.mostrar("La cantidad debe ser mayor que cero.");
            return;
        }

        List<AlimentoCafeteria> orden = new ArrayList<>();
        orden.add(alimento);

        if (!reglaBebidaCaliente.evaluar(socio, orden)) {
            consolaMenu.mostrar(
                    "Venta rechazada por la regla de bebidas calientes.");
            return;
        }

        double subtotal = alimento.getPrecio() * cantidad;
        double propina = subtotal * 0.10;

        VentaCafeteria venta = new VentaCafeteria(
                new Date(), subtotal, 0.0, 0, 0.0, propina);
        venta.setComprador(socio);

        DetalleVentaCafeteria detalle =
                new DetalleVentaCafeteria(cantidad, alimento.getPrecio());
        detalle.setAlimentoCafeteria(alimento);
        venta.agregarDetalleVentaCafeteria(detalle);

        double impuesto = impuestos.calcularMontos(venta);
        venta.setImpuestoConsumo(impuesto);
        venta.setPropina(propina);
        venta.calcularTotal(0.0, impuesto + propina);

        int puntos = calculadorPuntos.calcularPuntos(venta);
        venta.setPuntosFidelidadGenerados(puntos);

        CuentaPuntos cuenta = cuentasPuntos.get(socio);
        if (cuenta != null) {
            cuenta.sumarPuntos(puntos);
        }

        ventasCafeteria.add(venta);
        registrarVenta(venta);

        consolaMenu.mostrar("=== VENTA REGISTRADA ===");
        consolaMenu.mostrar("Socio: " + socio.getNombre());
        consolaMenu.mostrar("Producto: " + alimento.getNombre());
        consolaMenu.mostrar("Cantidad: " + cantidad);
        consolaMenu.mostrar("Subtotal: " + subtotal);
        consolaMenu.mostrar("Impuesto: " + impuesto);
        consolaMenu.mostrar("Propina: " + propina);
        consolaMenu.mostrar("Total: " + venta.getTotal());
        consolaMenu.mostrar("Puntos generados: " + puntos);
    }

    // Solicitud de cambio de turno

    private void solicitarCambioTurno() {
        if (registroTurnos == null) {
            consolaMenu.mostrar(
                    "El servicio de registro de turnos no está configurado.");
            return;
        }

        List<Turno> turnos = registroTurnos.getTurnos();

        if (turnos.isEmpty()) {
            consolaMenu.mostrar("No hay turnos registrados.");
            return;
        }

        consolaMenu.mostrar("=== TURNOS REGISTRADOS ===");
        for (int i = 0; i < turnos.size(); i++) {
            Turno turno = turnos.get(i);
            consolaMenu.mostrar((i + 1) + ". "
                    + turno.getIdTurno()
                    + " | Empleado: " + turno.getNombreEmpleado()
                    + " | Día: " + turno.getDiaSemana()
                    + " | Horario: " + turno.getFranjaHoraria()
                    + " | Área: " + turno.getArea());
        }

        int indiceTurno = leerEntero("Seleccione el turno que desea cambiar:");
        if (indiceTurno < 1 || indiceTurno > turnos.size()) {
            consolaMenu.mostrar("Selección inválida.");
            return;
        }

        Turno turnoOrigen = turnos.get(indiceTurno - 1);

        String solicitante = turnoOrigen.getEmpleadoId();

        consolaMenu.mostrar("ID del empleado de reemplazo:");
        String reemplazo = consolaMenu.leerEntrada().trim();

        if (solicitante == null || solicitante.isEmpty() || reemplazo.isEmpty()) {
            consolaMenu.mostrar("El ID del reemplazo no puede estar vacío.");
            return;
        }

        if (solicitante.equalsIgnoreCase(reemplazo)) {
            consolaMenu.mostrar(
                    "El empleado de reemplazo debe ser diferente.");
            return;
        }

        SolicitudCambioTurno solicitud = new SolicitudCambioTurno(
                "SOL-" + System.currentTimeMillis(),
                turnoOrigen,
                solicitante,
                reemplazo);

        registroTurnos.resolverSolicitud(solicitud);

        consolaMenu.mostrar("Estado de la solicitud: "
                + solicitud.getEstado());
    }

    // Generación de reportes

    private void generarReporte() {
        if (generadorReportes == null) {
            consolaMenu.mostrar(
                    "El generador de reportes no está configurado.");
            return;
        }

        consolaMenu.mostrar(
                "Tipo de reporte (tienda, cafeteria, ventas o todos):");
        String tipo = consolaMenu.leerEntrada().trim().toLowerCase();

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        formato.setLenient(false);

        Date desde;
        Date hasta;

        try {
            consolaMenu.mostrar("Fecha inicial (dd/MM/yyyy):");
            desde = formato.parse(consolaMenu.leerEntrada().trim());

            consolaMenu.mostrar("Fecha final (dd/MM/yyyy):");
            hasta = formato.parse(consolaMenu.leerEntrada().trim());
        } catch (ParseException e) {
            consolaMenu.mostrar(
                    "Fecha inválida. Use el formato dd/MM/yyyy.");
            return;
        }

        if (desde.after(hasta)) {
            consolaMenu.mostrar(
                    "La fecha inicial no puede ser posterior a la final.");
            return;
        }

        String reporte =
                generadorReportes.generarReporte(tipo, desde, hasta);

        consolaMenu.mostrar("=== REPORTE ===");
        consolaMenu.mostrar(reporte);
    }

    // Registra la venta para los reportes y guarda en archivo la venta, los productos y las cuentas de puntos
    private void registrarVenta(Venta venta) {
        if (generadorReportes != null) {
            generadorReportes.getVentas().add(venta);
        }

        try {
            AlmacenadorArchivos.guardarVenta("datos/ventas.txt", venta);
            AlmacenadorArchivos.guardarProductos("datos/productos.txt", catalogo.values());
            AlmacenadorArchivos.guardarCuentaPuntos("datos/cuentas.txt", cuentasPuntos.values());
        } catch (Exception e) {
            consolaMenu.mostrar("Aviso: no se pudo guardar la información en archivo.");
        }
    }

    // Utilidad para leer números enteros

    private int leerEntero(String mensaje) {
        while (true) {
            consolaMenu.mostrar(mensaje);

            try {
                return Integer.parseInt(
                        consolaMenu.leerEntrada().trim());
            } catch (NumberFormatException e) {
                consolaMenu.mostrar(
                        "Valor inválido. Ingrese un número entero.");
            }
        }
    }
}
