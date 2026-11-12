package comercio;

import autenticacion.Socio;

public class CuentaPuntos {
    private int saldo;
    private Socio socio;

    public CuentaPuntos(int saldo) {
        this.saldo = saldo;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio socio) {
        this.socio = socio;
    }

    public void sumarPuntos(int puntos) {
        if (puntos > 0) {
            this.saldo += puntos;
        }
    }
}