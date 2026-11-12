
package comercio;

import java.util.ArrayList;
import java.util.List;

import autenticacion.Socio;
import deportivo.ReservaInstalacion;

public class ReglaBebidaCaliente {

    private List<ReservaInstalacion> reservasActivas;

    public ReglaBebidaCaliente() {
        this.reservasActivas = new ArrayList<>();
    }

    public ReglaBebidaCaliente(List<ReservaInstalacion> reservasActivas) {
        this.reservasActivas = (reservasActivas == null)
                ? new ArrayList<>()
                : new ArrayList<>(reservasActivas);
    }

    public List<ReservaInstalacion> getReservasActivas() {
        return reservasActivas;
    }

    public void setReservasActivas(List<ReservaInstalacion> reservasActivas) {
        this.reservasActivas = (reservasActivas == null)
                ? new ArrayList<>()
                : new ArrayList<>(reservasActivas);
    }

    public boolean evaluar(Socio socio, List<AlimentoCafeteria> orden) {
        if (socio == null || orden == null || orden.isEmpty()) {
            return false;
        }

        if (!tieneBebidaCaliente(orden)) {
            return true;
        }

        for (ReservaInstalacion reserva : reservasActivas) {
            if (reserva == null) {
                continue;
            }

            boolean participa = false;

            if (socio.equals(reserva.getSocioResponsable())) {
                participa = true;
            } else if (reserva.getJugadores() != null
                    && reserva.getJugadores().contains(socio)) {
                participa = true;
            }

            if (participa
                    && reserva.getInstalacion() != null
                    && reserva.getInstalacion().restringeBebidasCalientes()) {
                return false;
            }
        }

        return true;
    }

    public boolean tieneBebidaCaliente(List<AlimentoCafeteria> orden) {
        if (orden == null || orden.isEmpty()) {
            return false;
        }

        for (AlimentoCafeteria alimento : orden) {
            if (alimento instanceof Bebida
                    && "caliente".equalsIgnoreCase(
                            ((Bebida) alimento).getTipo())) {
                return true;
            }
        }

        return false;
    }
}
