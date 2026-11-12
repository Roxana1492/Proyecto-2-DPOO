package deportivo;

import java.util.ArrayList;
import java.util.List;

public class Equipo {
    private String nombre;
    private int cupoMaximo;
    private int edadMinima;
    private int edadMaxima;
    private Deporte deporte;
    private List<InscripcionJugador> inscripciones;

    public Equipo(String nombre, int cupoMaximo, int edadMinima, int edadMaxima, Deporte deporte) {
        this.nombre = nombre;
        this.cupoMaximo = cupoMaximo;
        this.edadMinima = edadMinima;
        this.edadMaxima = edadMaxima;
        this.deporte = deporte;
        this.inscripciones = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public Deporte getDeporte() {
        return deporte;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public int getEdadMinima() {
        return edadMinima;
    }

    public int getEdadMaxima() {
        return edadMaxima;
    }

    public List<InscripcionJugador> getInscripciones() {
        return inscripciones;
    }

    public boolean agregarInscripcion(InscripcionJugador inscripcion) {
        if (tieneCupo()) {
            this.inscripciones.add(inscripcion);
            inscripcion.getSocio().agregarInscripcion(inscripcion);
            return true;
        }
        return false;
    }

    public boolean tieneCupo() {
        return inscripciones.size() < cupoMaximo;
    }

    public boolean aceptaEdad(int edad) {
        return edad >= edadMinima && edad <= edadMaxima;
    }

    public boolean camisetaDisponible(int numero) {
        for (InscripcionJugador ij : inscripciones) {
            if (ij.getNumeroCamiseta() == numero) {
                return false;
            }
        }
        return true;
    }

    public void mostrarEstado() {
        System.out.println("  [Equipo: " + nombre + " | Deporte: " + deporte.getNombre() + 
                           " | Rango Edad: " + edadMinima + "-" + edadMaxima +
                           " | Cupo: " + inscripciones.size() + "/" + cupoMaximo + "]");
        if (inscripciones.isEmpty()) {
            System.out.println("    - Sin jugadores inscritos.");
        } else {
            for (InscripcionJugador ij : inscripciones) {
                System.out.println("    - Jugador: " + ij.getSocio().getNombre() + 
                                   " | Camiseta #" + ij.getNumeroCamiseta() + 
                                   " | Posición: " + ij.getPosicion());
            }
        }
    }
}