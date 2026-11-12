package deportivo;

import autenticacion.Socio;

public class VerificacionSancion {
    public boolean estaHabilitado(Socio socio) {
        return !SancionDeportiva.estaSuspendido(socio);
    }
}