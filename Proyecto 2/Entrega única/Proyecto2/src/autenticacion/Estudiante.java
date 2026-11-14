package autenticacion;

import java.time.LocalDate;
import java.time.Period;

import deportivo.Nivel;

public class Estudiante extends Usuario {
    private String nombre;
    private String documentoIdentidad;
    private LocalDate fechaNacimiento;
    private Nivel nivel;
    private String codigoEstudiantil;

    public Estudiante(String login, String password, String nombre, String documentoIdentidad,
            LocalDate fechaNacimiento, Nivel nivel, String codigoEstudiantil) {
        super(login, password);
        this.nombre = nombre;
        this.documentoIdentidad = documentoIdentidad;
        this.fechaNacimiento = fechaNacimiento;
        this.nivel = nivel;
        this.codigoEstudiantil = codigoEstudiantil;
    }

    @Override
    public String darNombre() { return nombre; }

    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getCodigoEstudiantil() { return codigoEstudiantil; }

    @Override
    public Nivel darNivel() { return nivel; }

    @Override
    public int calcularEdad() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}
