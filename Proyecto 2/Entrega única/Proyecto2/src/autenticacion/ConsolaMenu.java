package autenticacion;

import java.util.Scanner;

/**
 * Interfacer: única clase que muestra mensajes y lee entradas del usuario (RNF5).
 */
public class ConsolaMenu {
    private Scanner scanner;

    public ConsolaMenu() {
        this.scanner = new Scanner(System.in);
    }

    public void mostrar(String mensaje) {
        System.out.println(mensaje);
    }

    public String leerEntrada() {
        return scanner.nextLine();
    }
}
