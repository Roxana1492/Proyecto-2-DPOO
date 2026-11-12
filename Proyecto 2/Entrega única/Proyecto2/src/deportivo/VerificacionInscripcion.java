package deportivo;

import autenticacion.Socio;

public class VerificacionInscripcion {

    public String verificar(Socio socio, Equipo equipo, int numeroCamiseta) {
        StringBuilder motivo = new StringBuilder();

        if (!equipo.tieneCupo()) {
            motivo.append("Cupo máximo alcanzado. ");
        }

        int edad = socio.calcularEdad();
        if (!equipo.aceptaEdad(edad)) {
            motivo.append("Edad (").append(edad).append(" años) fuera del rango del equipo (")
                  .append(equipo.getEdadMinima()).append("-").append(equipo.getEdadMaxima()).append("). ");
        }

        if (!equipo.camisetaDisponible(numeroCamiseta)) {
            motivo.append("Camiseta #").append(numeroCamiseta).append(" ya está ocupada. ");
        }

        if (socio.estaInscritoEn(equipo.getDeporte())) {
            motivo.append("El socio ya está inscrito en otro equipo del deporte ").append(equipo.getDeporte().getNombre()).append(". ");
        }

        return motivo.toString().trim();
    }
}