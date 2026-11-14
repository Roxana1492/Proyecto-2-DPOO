package autenticacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import deportivo.Deporte;
import deportivo.InscripcionJugador;
import deportivo.Nivel;

public class Socio extends Usuario {
    private String nombre;
    private Date fechaNacimiento;
    private int puntos;
    private Nivel nivel;
    private List<InscripcionJugador> inscripciones;

    public Socio(String login, String password, String nombre, Date fechaNacimiento) {
        super(login, password);
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.puntos = 0;
        this.inscripciones = new ArrayList<>();
    }

    @Override
    public String darNombre() {
        return nombre;
    }

    @Override
    public Nivel darNivel() {
        return nivel;
    }

    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

    public String getNombre() {
        return nombre;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
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

    // Método para calcular la edad usando Calendar (compatible con Date)
    @Override
    public int calcularEdad() {
        if (this.fechaNacimiento == null) {
            return 0; 
        }
        
        Calendar fechaNac = Calendar.getInstance();
        fechaNac.setTime(this.fechaNacimiento);
        
        Calendar fechaActual = Calendar.getInstance();
        
        int edad = fechaActual.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);
        
        // Validar si aún no ha cumplido años en el año actual
        if (fechaActual.get(Calendar.DAY_OF_YEAR) < fechaNac.get(Calendar.DAY_OF_YEAR)) {
            edad--;
        }
        
        return edad;
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