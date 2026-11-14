package autenticacion;

import java.time.LocalDate;
import java.time.Period;

import deportivo.Nivel;

public class Externo extends Usuario {
    private String nombre;
    private String documentoIdentidad;
    private LocalDate fechaNacimiento;
    private Nivel nivel;

    public Externo(String login, String password, String nombre, String documentoIdentidad,
            LocalDate fechaNacimiento, Nivel nivel) {
        super(login, password);
        this.nombre = nombre;
        this.documentoIdentidad = documentoIdentidad;
        this.fechaNacimiento = fechaNacimiento;
        this.nivel = nivel;
    }

    @Override
    public String darNombre() { return nombre; }

    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }

    @Override
    public Nivel darNivel() { return nivel; }

    @Override
    public int calcularEdad() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}
