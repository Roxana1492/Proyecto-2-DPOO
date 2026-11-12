package deportivo;

public class Ranking {
    private int puntajeActual;

    public Ranking(int puntajeInicial) {
        this.puntajeActual = puntajeInicial;
    }

    public int getPuntajeActual() {
        return puntajeActual;
    }

    // Fórmula ELO real basada en la expectativa de triunfo
    public void actualizar(double probabilidadEsperada, boolean gano, int kFactor) {
        double resultadoReal = gano ? 1.0 : 0.0;
        double cambio = kFactor * (resultadoReal - probabilidadEsperada);
        this.puntajeActual += (int) Math.round(cambio);
    }
}