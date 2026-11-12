package deportivo;

import autenticacion.Socio;

public class PartidoPractica {
    private String disciplina;
    private Socio jugador1;
    private String nivel1;
    private Socio jugador2;
    private String nivel2;
    private boolean jugador1EsEntrenador; // Excepción: Se permite si uno de los jugadores es entrenador

    public PartidoPractica(String disciplina, Socio jugador1, String nivel1, Socio jugador2, String nivel2, boolean jugador1EsEntrenador) {
        this.disciplina = disciplina;
        this.jugador1 = jugador1;
        this.nivel1 = nivel1;
        this.jugador2 = jugador2;
        this.nivel2 = nivel2;
        this.jugador1EsEntrenador = jugador1EsEntrenador;
    }

    public boolean esValido() {
        // Si uno de los jugadores es entrenador, se permite sin importar la distancia de niveles
        if (jugador1EsEntrenador) {
            return true;
        }

        // Mapeo de niveles para validar adyacencia (Principiante=1, Intermedio=2, Avanzado=3)
        int n1 = convertirNivelANumero(nivel1);
        int n2 = convertirNivelANumero(nivel2);

        // Se permite si los niveles son adyacentes (diferencia <= 1)
        return Math.abs(n1 - n2) <= 1;
    }

    private int convertirNivelANumero(String nivel) {
        switch (nivel.toLowerCase()) {
            case "principiante": return 1;
            case "intermedio": return 2;
            case "avanzado": return 3;
            default: return 1;
        }
    }
}