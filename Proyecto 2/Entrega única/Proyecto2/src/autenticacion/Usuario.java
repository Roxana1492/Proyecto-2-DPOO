package autenticacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deportivo.BonoDescuento;
import deportivo.Nivel;
import deportivo.Ranking;

public abstract class Usuario {
    protected String login;
    protected String password;
    protected List<BonoDescuento> bonos;
    // ELO por disciplina (se usa el Ranking del Proyecto 1)
    protected Map<String, Ranking> rankings;

    public Usuario(String login, String password) {
        this.login = login;
        this.password = password;
        this.bonos = new ArrayList<>();
        this.rankings = new HashMap<>();
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public abstract String darNombre();

    // Los que no tienen fecha de nacimiento o nivel registrados no quedan bloqueados
    public int calcularEdad() {
        return 0;
    }

    public Nivel darNivel() {
        return null;
    }

    public void agregarBono(BonoDescuento bono) {
        bonos.add(bono);
    }

    public List<BonoDescuento> darBonos() {
        return bonos;
    }

    public List<BonoDescuento> darBonosDisponibles() {
        List<BonoDescuento> disponibles = new ArrayList<>();
        for (BonoDescuento b : bonos) {
            if (!b.isUsado()) {
                disponibles.add(b);
            }
        }
        return disponibles;
    }

    /** ELO en la disciplina; 0 si nunca ha jugado un torneo de ella. */
    public int darElo(String disciplina) {
        Ranking r = rankings.get(disciplina.toLowerCase());
        if (r == null) {
            return 0;
        }
        return r.getPuntajeActual();
    }

    /** Ranking de la disciplina; si no tiene, se crea con 1000 puntos. */
    public Ranking darRanking(String disciplina) {
        Ranking r = rankings.get(disciplina.toLowerCase());
        if (r == null) {
            r = new Ranking(1000);
            rankings.put(disciplina.toLowerCase(), r);
        }
        return r;
    }
}
