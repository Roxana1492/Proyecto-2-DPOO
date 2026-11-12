package autenticacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import deportivo.Deporte;
import deportivo.InscripcionJugador;

public class Socio extends Usuario {
    private String nombre;
    private Date fechaNacimiento;
    private int puntos;
    private List<InscripcionJugador> inscripciones;

    public Socio(String login, String password, String nombre, Date fechaNacimiento) {
        super(login, password);
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.puntos = 0;
        this.inscripciones = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public int getPuntos() {
        return puntos;
    }

    public List<InscripcionJugador> getInscripciones() {
        return inscripciones;
    }

    public void agregarInscripcion(InscripcionJugador inscripcion) {
        this.inscripciones.add(inscripcion);
    }

    public int calcularEdad() {
        Date fechaActual = new Date();
        long diff = fechaActual.getTime() - fechaNacimiento.getTime();
        long anos = diff / (1000L * 60 * 60 * 24 * 365);
        return (int) anos;
    }

    public boolean estaInscritoEn(Deporte deporte) {
        for (InscripcionJugador ij : inscripciones) {
            if (ij.getEquipo() != null && ij.getEquipo().getDeporte().getNombre().equalsIgnoreCase(deporte.getNombre())) {
                return true;
            }
        }
        return false;
    }
}