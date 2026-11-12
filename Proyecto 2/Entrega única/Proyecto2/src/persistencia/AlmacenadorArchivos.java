package persistencia;

import autenticacion.Socio;
import comercio.Compra;
import comercio.CuentaPuntos;
import comercio.Producto;
import comercio.Venta;
import comercio.VentaCafeteria;
import deportivo.Deporte;
import deportivo.Equipo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class AlmacenadorArchivos {

    public static Map<String, Socio> cargarSocios(String rutaArchivo) throws Exception {
        Map<String, Socio> socios = new HashMap<>();
        File file = new File(rutaArchivo);
        if (!file.exists()) return socios;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.startsWith("#")) continue;
                String[] partes = linea.split(";");
                // Formato: login;password;nombre;fechaNacimiento(yyyy-MM-dd)
                if (partes.length >= 4) {
                    Date fecha = sdf.parse(partes[3].trim());
                    Socio s = new Socio(partes[0].trim(), partes[1].trim(), partes[2].trim(), fecha);
                    socios.put(s.getLogin(), s);
                }
            }
        }
        return socios;
    }

    public static List<Equipo> cargarEquipos(String rutaArchivo, Map<String, Deporte> deportes) throws Exception {
        List<Equipo> equipos = new ArrayList<>();
        File file = new File(rutaArchivo);
        if (!file.exists()) return equipos;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.startsWith("#")) continue;
                String[] partes = linea.split(";");
                // Formato: nombreEquipo;cupoMax;edadMin;edadMax;nombreDeporte
                if (partes.length >= 5) {
                    String nomEq = partes[0].trim();
                    int cupo = Integer.parseInt(partes[1].trim());
                    int edadMin = Integer.parseInt(partes[2].trim());
                    int edadMax = Integer.parseInt(partes[3].trim());
                    String nomDep = partes[4].trim();

                    Deporte dep = deportes.computeIfAbsent(nomDep, k -> new Deporte(k, 5));
                    equipos.add(new Equipo(nomEq, cupo, edadMin, edadMax, dep));
                }
            }
        }
        return equipos;
    }

    // ---------------------------------------------------------------
    // Guardado y carga del componente comercial
    // ---------------------------------------------------------------

    private static PrintWriter abrirParaAgregar(String ruta, boolean agregar) throws Exception {
        File archivo = new File(ruta);
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }
        return new PrintWriter(new FileWriter(archivo, agregar));
    }

    // Formato: tipo;fecha(yyyy-MM-dd HH:mm);comprador;subtotal;total;puntos
    public static void guardarVenta(String ruta, Venta venta) throws Exception {
        if (venta == null) {
            return;
        }
        String tipo = (venta instanceof Compra) ? "TIENDA"
                : (venta instanceof VentaCafeteria) ? "CAFETERIA" : "VENTA";
        String comprador = (venta.getComprador() == null) ? "" : venta.getComprador().getLogin();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try (PrintWriter pw = abrirParaAgregar(ruta, true)) {
            pw.println(tipo + ";" + sdf.format(venta.getFecha()) + ";" + comprador + ";"
                    + venta.getSubtotal() + ";" + venta.getTotal() + ";"
                    + venta.getPuntosFidelidadGenerados());
        }
    }

    public static void guardarCompra(String ruta, Compra compra) throws Exception {
        guardarVenta(ruta, compra);
    }

    // Formato: nombre;categoria;precio;cantidadDisponible (reescribe el archivo)
    public static void guardarProductos(String ruta, Collection<Producto> productos) throws Exception {
        try (PrintWriter pw = abrirParaAgregar(ruta, false)) {
            if (productos == null) {
                return;
            }
            for (Producto p : productos) {
                pw.println(p.getNombre() + ";" + p.getCategoria() + ";"
                        + p.getPrecio() + ";" + p.getCantidadDisponible());
            }
        }
    }

    public static List<Producto> cargarProductos(String ruta) throws Exception {
        List<Producto> productos = new ArrayList<>();
        File file = new File(ruta);
        if (!file.exists()) return productos;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.startsWith("#")) continue;
                String[] partes = linea.split(";");
                if (partes.length >= 4) {
                    productos.add(new Producto(partes[0].trim(), partes[1].trim(),
                            Double.parseDouble(partes[2].trim()),
                            Integer.parseInt(partes[3].trim())));
                }
            }
        }
        return productos;
    }

    public static List<CuentaPuntos> cargarCuentasPuntos(String ruta, Map<String, Socio> socios) throws Exception {
        List<CuentaPuntos> cuentas = new ArrayList<>();
        File file = new File(ruta);
        if (!file.exists()) return cuentas;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.startsWith("#")) continue;
                String[] partes = linea.split(";");
                if (partes.length >= 2) {
                    Socio socio = socios.get(partes[0].trim());
                    if (socio != null) {
                        CuentaPuntos cuenta = new CuentaPuntos(Integer.parseInt(partes[1].trim()));
                        cuenta.setSocio(socio);
                        cuentas.add(cuenta);
                    }
                }
            }
        }
        return cuentas;
    }

    // Formato: loginSocio;saldo (reescribe el archivo)
    public static void guardarCuentaPuntos(String ruta, Collection<CuentaPuntos> cuentas) throws Exception {
        try (PrintWriter pw = abrirParaAgregar(ruta, false)) {
            if (cuentas == null) {
                return;
            }
            for (CuentaPuntos c : cuentas) {
                String login = (c.getSocio() == null) ? "" : c.getSocio().getLogin();
                pw.println(login + ";" + c.getSaldo());
            }
        }
    }
}
