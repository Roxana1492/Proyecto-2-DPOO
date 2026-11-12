package deportivo;

import autenticacion.Socio;
import java.util.HashMap;
import java.util.Map;

public class SancionDeportiva {
    private int tarjetasAmarillas;
    private boolean tarjetaRoja;
    private boolean suspendido;
    
    private static Map<Socio, SancionDeportiva> registroSanciones = new HashMap<>();

    public SancionDeportiva(int tarjetasAmarillas, boolean tarjetaRoja) {
        this.tarjetasAmarillas = tarjetasAmarillas;
        this.tarjetaRoja = tarjetaRoja;
        // REGLA: Con 2 amarillas o 1 tarjeta roja queda suspendido
        this.suspendido = (tarjetasAmarillas >= 2 || tarjetaRoja);
    }

    public static void registrarTarjetas(Socio socio, int amarillas, boolean roja) {
        SancionDeportiva actual = registroSanciones.getOrDefault(socio, new SancionDeportiva(0, false));
        int totalAmarillas = actual.tarjetasAmarillas + amarillas;
        boolean tieneRoja = actual.tarjetaRoja || roja;
        
        registroSanciones.put(socio, new SancionDeportiva(totalAmarillas, tieneRoja));
    }

    public static boolean estaSuspendido(Socio socio) {
        SancionDeportiva actual = registroSanciones.get(socio);
        if (actual == null) return false;
        return actual.suspendido;
    }

    public int getTarjetasAmarillas() { return tarjetasAmarillas; }
    public boolean isTarjetaRoja() { return tarjetaRoja; }
    public boolean isSuspendido() { return suspendido; }
}