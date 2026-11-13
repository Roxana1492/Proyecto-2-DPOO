package persistencia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import autenticacion.Empleado;
import autenticacion.Socio;
import autenticacion.Usuario;
import comercio.AlimentoCafeteria;
import comercio.Bebida;
import comercio.CodigoDescuento;
import comercio.Compra;
import comercio.CuentaPuntos;
import comercio.Producto;
import comercio.Snack;
import comercio.Venta;
import comercio.VentaCafeteria;
import deportivo.Turno;
import fachada.OperacionesComercio;

/**
 * Carga y guarda la parte comercial (tienda, cafetería, puntos, códigos de
 * descuento, turnos y ventas) en los archivos de la carpeta datos/.
 * Los productos, las cuentas de puntos y las ventas usan los métodos de
 * AlmacenadorArchivos del Proyecto 1; aquí se agregan los archivos que antes
 * se creaban a mano en Principal.
 */
public class PersistenciaComercio {

    private final String carpeta;

    public PersistenciaComercio(String carpeta) {
        this.carpeta = carpeta;
    }

    private String ruta(String archivo) {
        return carpeta + File.separator + archivo;
    }

    // ------------------------------------------------------------------
    // Carga
    // ------------------------------------------------------------------

    /**
     * Llena OperacionesComercio con lo que hay en los archivos.
     * Los socios y empleados se toman de la lista de usuarios ya cargada.
     */
    public void cargar(OperacionesComercio comercio, List<Usuario> usuarios) throws Exception {
        Map<String, Socio> socios = new HashMap<>();
        Map<String, Empleado> empleados = new HashMap<>();
        for (Usuario u : usuarios) {
            if (u instanceof Socio) {
                socios.put(u.getLogin(), (Socio) u);
                comercio.registrarSocio((Socio) u);
            } else if (u instanceof Empleado) {
                empleados.put(u.getLogin(), (Empleado) u);
            }
        }

        for (Producto p : AlmacenadorArchivos.cargarProductos(ruta("productos.txt"))) {
            comercio.registrarProducto(p);
        }

        // Cuentas de puntos: si un socio no tiene cuenta en el archivo se le crea en 0
        Map<String, CuentaPuntos> cuentas = new HashMap<>();
        for (CuentaPuntos c : AlmacenadorArchivos.cargarCuentasPuntos(ruta("cuentas.txt"), socios)) {
            cuentas.put(c.getSocio().getLogin(), c);
        }
        for (Socio s : socios.values()) {
            CuentaPuntos cuenta = cuentas.get(s.getLogin());
            if (cuenta == null) {
                cuenta = new CuentaPuntos(0);
            }
            comercio.registrarCuentaPuntos(s, cuenta);
        }

        for (AlimentoCafeteria a : cargarAlimentos()) {
            comercio.registrarAlimento(a);
        }
        for (CodigoDescuento c : cargarCodigos(empleados)) {
            comercio.registrarCodigoDescuento(c);
        }
        for (Turno t : cargarTurnos()) {
            comercio.registrarTurno(t);
        }
        for (Venta v : cargarVentas(socios)) {
            comercio.registrarVentaAnterior(v);
        }
    }

    // Formato: BEBIDA;nombre;precio;caliente|fria  o  SNACK;nombre;precio
    private List<AlimentoCafeteria> cargarAlimentos() throws Exception {
        List<AlimentoCafeteria> alimentos = new ArrayList<>();
        for (String[] partes : leerLineas(ruta("alimentos.txt"))) {
            if (partes.length >= 4 && partes[0].equalsIgnoreCase("BEBIDA")) {
                alimentos.add(new Bebida(partes[1], Double.parseDouble(partes[2]), partes[3]));
            } else if (partes.length >= 3 && partes[0].equalsIgnoreCase("SNACK")) {
                alimentos.add(new Snack(partes[1], Double.parseDouble(partes[2])));
            }
        }
        return alimentos;
    }

    // Formato: porcentajeEmpleado;porcentajeCompartido;loginPropietario
    private List<CodigoDescuento> cargarCodigos(Map<String, Empleado> empleados) throws Exception {
        List<CodigoDescuento> codigos = new ArrayList<>();
        for (String[] partes : leerLineas(ruta("codigos.txt"))) {
            if (partes.length >= 3) {
                CodigoDescuento codigo = new CodigoDescuento(Double.parseDouble(partes[0]),
                        Double.parseDouble(partes[1]));
                codigo.setPropietario(empleados.get(partes[2]));
                codigos.add(codigo);
            }
        }
        return codigos;
    }

    // Formato: idTurno;loginEmpleado;nombreEmpleado;dia;franja;area;rol
    private List<Turno> cargarTurnos() throws Exception {
        List<Turno> turnos = new ArrayList<>();
        for (String[] partes : leerLineas(ruta("turnos.txt"))) {
            if (partes.length >= 7) {
                turnos.add(new Turno(partes[0], partes[1], partes[2], partes[3],
                        partes[4], partes[5], partes[6]));
            }
        }
        return turnos;
    }

    // Formato (el mismo de AlmacenadorArchivos.guardarVenta):
    // tipo;fecha(yyyy-MM-dd HH:mm);comprador;subtotal;total;puntos
    private List<Venta> cargarVentas(Map<String, Socio> socios) throws Exception {
        List<Venta> ventas = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (String[] partes : leerLineas(ruta("ventas.txt"))) {
            if (partes.length < 6) {
                continue;
            }
            Date fecha = sdf.parse(partes[1]);
            double subtotal = Double.parseDouble(partes[3]);
            double total = Double.parseDouble(partes[4]);
            int puntos = Integer.parseInt(partes[5]);
            Venta venta;
            if (partes[0].equals("TIENDA")) {
                venta = new Compra(fecha, subtotal, total, puntos, 0);
            } else if (partes[0].equals("CAFETERIA")) {
                venta = new VentaCafeteria(fecha, subtotal, total, puntos, 0, 0);
            } else {
                venta = new Venta(fecha, subtotal, total, puntos);
            }
            venta.setComprador(socios.get(partes[2]));
            ventas.add(venta);
        }
        return ventas;
    }

    // ------------------------------------------------------------------
    // Guardado
    // ------------------------------------------------------------------

    /** Guarda lo que pudo cambiar durante la ejecución. */
    public void guardar(OperacionesComercio comercio) throws Exception {
        AlmacenadorArchivos.guardarProductos(ruta("productos.txt"), comercio.darProductos());
        AlmacenadorArchivos.guardarCuentaPuntos(ruta("cuentas.txt"), comercio.darCuentasPuntos());

        // Las ventas se agregan al final del archivo, solo las de esta ejecución
        for (Venta v : comercio.darVentasNuevas()) {
            AlmacenadorArchivos.guardarVenta(ruta("ventas.txt"), v);
        }
        comercio.darVentasNuevas().clear();

        // Los turnos cambian cuando se aprueba un cambio de turno
        try (PrintWriter pw = new PrintWriter(new FileWriter(ruta("turnos.txt")))) {
            for (Turno t : comercio.darTurnos()) {
                pw.println(t.getIdTurno() + ";" + t.getEmpleadoId() + ";" + t.getNombreEmpleado() + ";"
                        + t.getDiaSemana() + ";" + t.getFranjaHoraria() + ";" + t.getArea() + ";"
                        + t.getRol());
            }
        }
    }

    // ------------------------------------------------------------------

    /** Lee un archivo separado por ';' saltando las líneas vacías y los comentarios (#). */
    static List<String[]> leerLineas(String ruta) throws Exception {
        List<String[]> lineas = new ArrayList<>();
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            return lineas;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                String[] partes = linea.split(";", -1);
                for (int i = 0; i < partes.length; i++) {
                    partes[i] = partes[i].trim();
                }
                lineas.add(partes);
            }
        }
        return lineas;
    }
}
