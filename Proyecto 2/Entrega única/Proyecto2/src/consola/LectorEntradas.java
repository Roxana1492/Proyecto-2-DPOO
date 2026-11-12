package consola;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Lee y valida lo que el usuario escribe en la consola.
 * Cada método repite la pregunta hasta que la entrada tenga el formato correcto,
 * así las consolas no tienen que validar nada por su cuenta.
 */
public class LectorEntradas {

    private final Scanner scanner;
    private final PrintStream salida;

    public LectorEntradas() {
        this(System.in, System.out);
    }

    public LectorEntradas(InputStream entrada, PrintStream salida) {
        this.scanner = new Scanner(entrada);
        this.salida = salida;
    }

    public void mostrar(String mensaje) {
        salida.println(mensaje);
    }

    /** Devuelve el texto sin espacios al inicio ni al final. Puede ser vacío. */
    public String leerTexto(String mensaje) {
        salida.print(mensaje + " ");
        if (!scanner.hasNextLine()) {
            throw new IllegalStateException("No hay más entradas disponibles.");
        }
        return scanner.nextLine().trim();
    }

    public String leerTextoNoVacio(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            if (!texto.isEmpty()) {
                return texto;
            }
            mostrar("Este campo no puede quedar vacío.");
        }
    }

    public int leerEntero(String mensaje, int min, int max) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                mostrar("Ingrese un número entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                mostrar("Debe ingresar un número entero.");
            }
        }
    }

    public double leerDecimal(String mensaje, double min) {
        while (true) {
            String texto = leerTexto(mensaje).replace(",", ".");
            try {
                double valor = Double.parseDouble(texto);
                if (valor >= min) {
                    return valor;
                }
                mostrar("El valor no puede ser menor que " + min + ".");
            } catch (NumberFormatException e) {
                mostrar("Debe ingresar un número (por ejemplo 50000 o 12.5).");
            }
        }
    }

    /** Fecha en formato AAAA-MM-DD. */
    public LocalDate leerFecha(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje + " (AAAA-MM-DD):");
            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                mostrar("Fecha inválida. Use el formato AAAA-MM-DD, por ejemplo 2026-12-05.");
            }
        }
    }

    /**
     * Muestra las opciones numeradas y devuelve la elegida.
     * Sirve para tipo de torneo, modalidad, nivel, etc.
     */
    public String leerOpcion(String mensaje, List<String> opciones) {
        mostrar(mensaje);
        for (int i = 0; i < opciones.size(); i++) {
            mostrar("  " + (i + 1) + ". " + opciones.get(i));
        }
        int elegida = leerEntero("Opción:", 1, opciones.size());
        return opciones.get(elegida - 1);
    }

    public boolean leerSiNo(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje + " (s/n):").toLowerCase();
            if (texto.equals("s") || texto.equals("si") || texto.equals("sí")) {
                return true;
            }
            if (texto.equals("n") || texto.equals("no")) {
                return false;
            }
            mostrar("Responda s o n.");
        }
    }
}
