package fachada;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import autenticacion.Socio;
import comercio.AlimentoCafeteria;
import comercio.CalculadorPuntos;
import comercio.CodigoDescuento;
import comercio.Compra;
import comercio.CuentaPuntos;
import comercio.DetalleVentaCafeteria;
import comercio.GeneradorReportes;
import comercio.GestorDescuentos;
import comercio.Impuestos;
import comercio.Producto;
import comercio.RegistroTurnos;
import comercio.ReglaBebidaCaliente;
import comercio.Venta;
import comercio.VentaCafeteria;
import deportivo.SolicitudCambioTurno;
import deportivo.Turno;

/**
 * Lógica de tienda, cafetería, turnos y reportes del Proyecto 1.
 * Antes estaba mezclada con los menús en CoordinadorAplicacion; aquí no se lee
 * ni se imprime nada: las consolas piden los datos y muestran el resultado.
 * Si una regla no se cumple se lanza OperacionRechazadaException.
 */
public class OperacionesComercio {

    private final GestorDescuentos gestorDescuentos;
    private final Impuestos impuestos;
    private final CalculadorPuntos calculadorPuntos;
    private final ReglaBebidaCaliente reglaBebidaCaliente;
    private final RegistroTurnos registroTurnos;
    private final GeneradorReportes generadorReportes;

    private final Map<String, Producto> catalogo = new LinkedHashMap<>();
    private final List<AlimentoCafeteria> alimentos = new ArrayList<>();
    private final List<CodigoDescuento> codigosDescuento = new ArrayList<>();
    private final Map<String, Socio> socios = new HashMap<>();
    private final Map<String, CuentaPuntos> cuentasPuntos = new HashMap<>();
    private final List<Venta> ventasNuevas = new ArrayList<>();

    public OperacionesComercio(GestorDescuentos gestorDescuentos, Impuestos impuestos,
            CalculadorPuntos calculadorPuntos, ReglaBebidaCaliente reglaBebidaCaliente,
            RegistroTurnos registroTurnos, GeneradorReportes generadorReportes) {
        this.gestorDescuentos = gestorDescuentos;
        this.impuestos = impuestos;
        this.calculadorPuntos = calculadorPuntos;
        this.reglaBebidaCaliente = reglaBebidaCaliente;
        this.registroTurnos = registroTurnos;
        this.generadorReportes = generadorReportes;
    }

    // ------------------------------------------------------------------
    // Carga de datos (la usa la persistencia al iniciar)
    // ------------------------------------------------------------------

    public void registrarProducto(Producto producto) {
        catalogo.put(producto.getNombre().trim().toLowerCase(), producto);
    }

    public void registrarAlimento(AlimentoCafeteria alimento) {
        alimentos.add(alimento);
    }

    public void registrarCodigoDescuento(CodigoDescuento codigo) {
        codigosDescuento.add(codigo);
    }

    public void registrarSocio(Socio socio) {
        socios.put(socio.getLogin(), socio);
    }

    public void registrarCuentaPuntos(Socio socio, CuentaPuntos cuenta) {
        cuenta.setSocio(socio);
        cuentasPuntos.put(socio.getLogin(), cuenta);
    }

    public void registrarTurno(Turno turno) {
        registroTurnos.agregarTurno(turno);
    }

    public void registrarVentaAnterior(Venta venta) {
        generadorReportes.getVentas().add(venta);
    }

    public Collection<Producto> darProductos() {
        return catalogo.values();
    }

    public Collection<CuentaPuntos> darCuentasPuntos() {
        return cuentasPuntos.values();
    }

    /** Ventas hechas en esta ejecución, para que la persistencia las agregue al archivo. */
    public List<Venta> darVentasNuevas() {
        return ventasNuevas;
    }

    public List<Turno> darTurnos() {
        return registroTurnos.getTurnos();
    }

    public boolean esSocio(String login) {
        return socios.containsKey(login);
    }

    // ------------------------------------------------------------------
    // Listados para los menús
    // ------------------------------------------------------------------

    public List<String> listarProductos() {
        List<String> lineas = new ArrayList<>();
        int i = 1;
        for (Producto p : catalogo.values()) {
            lineas.add(i++ + ". " + p.getNombre() + " | $" + p.getPrecio() + " | Existencias: " + p.getCantidadDisponible());
        }
        return lineas;
    }

    public List<String> listarAlimentos() {
        List<String> lineas = new ArrayList<>();
        for (int i = 0; i < alimentos.size(); i++) {
            lineas.add((i + 1) + ". " + alimentos.get(i).getNombre() + " | $" + alimentos.get(i).getPrecio());
        }
        return lineas;
    }

    public int cantidadCodigosDescuento() {
        return codigosDescuento.size();
    }

    public List<String> listarTurnos() {
        List<String> lineas = new ArrayList<>();
        List<Turno> turnos = registroTurnos.getTurnos();
        for (int i = 0; i < turnos.size(); i++) {
            Turno t = turnos.get(i);
            lineas.add((i + 1) + ". " + t.getIdTurno() + " | " + t.getNombreEmpleado() + " | " + t.getDiaSemana()
                    + " " + t.getFranjaHoraria() + " | " + t.getArea());
        }
        return lineas;
    }

    // ------------------------------------------------------------------
    // Operaciones
    // ------------------------------------------------------------------

    /**
     * Subtotal antes de impuestos; lo usa la consola para calcular el bono.
     * Revisa las existencias antes, para no gastar un bono en una compra que no se puede hacer.
     */
    public double calcularSubtotalTienda(int numeroProducto, int cantidad) {
        Producto producto = buscarProducto(numeroProducto);
        if (cantidad <= 0 || !producto.hayExistencias(cantidad)) {
            throw new OperacionRechazadaException("No hay existencias suficientes de " + producto.getNombre() + ".");
        }
        return producto.getPrecio() * cantidad;
    }

    /**
     * Compra en la tienda.
     * @param numeroCodigo 0 si no usa código de descuento de empleado.
     * @param descuentoBono valor ya calculado del bono de torneo (0 si no usa).
     */
    public String comprarEnTienda(String loginSocio, int numeroProducto, int cantidad,
            int numeroCodigo, double descuentoBono) {
        Socio socio = buscarSocio(loginSocio);
        Producto producto = buscarProducto(numeroProducto);
        if (cantidad <= 0 || !producto.hayExistencias(cantidad)) {
            throw new OperacionRechazadaException("No hay existencias suficientes de " + producto.getNombre() + ".");
        }
        if (numeroCodigo > 0 && descuentoBono > 0) {
            throw new OperacionRechazadaException("El bono no se puede combinar con un código de descuento.");
        }

        Compra compra = new Compra(new Date(), 0.0, 0.0, 0, 0.0);
        compra.setComprador(socio);
        compra.agregarDetalle(producto, cantidad);

        double descuento = descuentoBono;
        if (numeroCodigo > 0) {
            if (numeroCodigo > codigosDescuento.size()) {
                throw new OperacionRechazadaException("El código de descuento no existe.");
            }
            descuento = gestorDescuentos.calcularDescuento(codigosDescuento.get(numeroCodigo - 1), compra);
        }
        double impuesto = impuestos.calcularMontos(compra);
        compra.setIva(impuesto);
        compra.calcularTotal(descuento, impuesto);
        int puntos = calculadorPuntos.calcularPuntos(compra);
        compra.setPuntosFidelidadGenerados(puntos);

        // El inventario solo cambia cuando la compra ya está validada
        producto.descontar(cantidad);
        sumarPuntos(loginSocio, puntos);
        registrarVenta(compra);

        return "Compra registrada: " + cantidad + " x " + producto.getNombre()
                + "\nSubtotal: " + compra.getSubtotal() + " | Descuento: " + descuento
                + " | IVA: " + impuesto + "\nTotal: " + compra.getTotal() + " | Puntos generados: " + puntos;
    }

    public String venderEnCafeteria(String loginSocio, int numeroAlimento, int cantidad) {
        Socio socio = buscarSocio(loginSocio);
        if (numeroAlimento < 1 || numeroAlimento > alimentos.size()) {
            throw new OperacionRechazadaException("El alimento seleccionado no existe.");
        }
        if (cantidad <= 0) {
            throw new OperacionRechazadaException("La cantidad debe ser mayor que cero.");
        }
        AlimentoCafeteria alimento = alimentos.get(numeroAlimento - 1);
        if (!reglaBebidaCaliente.evaluar(socio, List.of(alimento))) {
            throw new OperacionRechazadaException("Venta rechazada por la regla de bebidas calientes.");
        }

        double subtotal = alimento.getPrecio() * cantidad;
        double propina = subtotal * 0.10;
        VentaCafeteria venta = new VentaCafeteria(new Date(), subtotal, 0.0, 0, 0.0, propina);
        venta.setComprador(socio);
        DetalleVentaCafeteria detalle = new DetalleVentaCafeteria(cantidad, alimento.getPrecio());
        detalle.setAlimentoCafeteria(alimento);
        venta.agregarDetalleVentaCafeteria(detalle);

        double impuesto = impuestos.calcularMontos(venta);
        venta.setImpuestoConsumo(impuesto);
        venta.calcularTotal(0.0, impuesto + propina);
        int puntos = calculadorPuntos.calcularPuntos(venta);
        venta.setPuntosFidelidadGenerados(puntos);

        sumarPuntos(loginSocio, puntos);
        registrarVenta(venta);

        return "Venta registrada: " + cantidad + " x " + alimento.getNombre()
                + "\nSubtotal: " + subtotal + " | Impuesto: " + impuesto + " | Propina: " + propina
                + "\nTotal: " + venta.getTotal() + " | Puntos generados: " + puntos;
    }

    public String solicitarCambioTurno(int numeroTurno, String idReemplazo) {
        List<Turno> turnos = registroTurnos.getTurnos();
        if (numeroTurno < 1 || numeroTurno > turnos.size()) {
            throw new OperacionRechazadaException("El turno seleccionado no existe.");
        }
        Turno origen = turnos.get(numeroTurno - 1);
        if (idReemplazo == null || idReemplazo.isBlank()) {
            throw new OperacionRechazadaException("Debe indicar el empleado de reemplazo.");
        }
        if (origen.getEmpleadoId().equalsIgnoreCase(idReemplazo)) {
            throw new OperacionRechazadaException("El reemplazo debe ser otro empleado.");
        }
        SolicitudCambioTurno solicitud = new SolicitudCambioTurno("SOL-" + System.currentTimeMillis(), origen,
                origen.getEmpleadoId(), idReemplazo);
        registroTurnos.resolverSolicitud(solicitud);
        return "Estado de la solicitud: " + solicitud.getEstado();
    }

    public String generarReporte(String tipo, Date desde, Date hasta) {
        if (desde.after(hasta)) {
            throw new OperacionRechazadaException("La fecha inicial no puede ser posterior a la final.");
        }
        return generadorReportes.generarReporte(tipo, desde, hasta);
    }

    // ------------------------------------------------------------------

    private Socio buscarSocio(String login) {
        Socio socio = socios.get(login);
        if (socio == null) {
            throw new OperacionRechazadaException("Solo los socios del club pueden comprar en la tienda y la cafetería.");
        }
        return socio;
    }

    private Producto buscarProducto(int numero) {
        if (numero < 1 || numero > catalogo.size()) {
            throw new OperacionRechazadaException("El producto seleccionado no existe.");
        }
        return new ArrayList<>(catalogo.values()).get(numero - 1);
    }

    private void sumarPuntos(String login, int puntos) {
        CuentaPuntos cuenta = cuentasPuntos.get(login);
        if (cuenta != null) {
            cuenta.sumarPuntos(puntos);
        }
    }

    private void registrarVenta(Venta venta) {
        generadorReportes.getVentas().add(venta);
        ventasNuevas.add(venta);
    }
}
