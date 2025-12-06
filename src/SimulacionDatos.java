import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Singleton para compartir datos entre las ventanas de la simulación.
 * Contiene los números aleatorios generados y la lógica matemática de las transformadas.
 */
public class SimulacionDatos {
    private static SimulacionDatos instancia;

    private List<Double> conjunto1RiEn; // Para tiempos entre llegadas
    private List<Double> conjunto2RiSn; // Para tiempos de servicio
    private int nGenerados;

    private SimulacionDatos() {
        conjunto1RiEn = new ArrayList<>();
        conjunto2RiSn = new ArrayList<>();
    }

    public static synchronized SimulacionDatos getInstancia() {
        if (instancia == null) {
            instancia = new SimulacionDatos();
        }
        return instancia;
    }

    public void setDatosGenerados(List<Double> c1, List<Double> c2, int n) {
        this.conjunto1RiEn = new ArrayList<>(c1);
        this.conjunto2RiSn = new ArrayList<>(c2);
        this.nGenerados = n;
    }

    public List<Double> getConjunto1RiEn() { return conjunto1RiEn; }
    public List<Double> getConjunto2RiSn() { return conjunto2RiSn; }
    public int getNGenerados() { return nGenerados; }
    public boolean hayDatos() { return nGenerados > 0 && !conjunto1RiEn.isEmpty(); }

    // --- LÓGICA MATEMÁTICA DE TRANSFORMADAS INVERSAS ---
    // Movida aquí para ser accesible por la ventana de simulación tabular

    public double calcularUniforme(double ri, double a, double b) {
        return a + (b - a) * ri;
    }

    public double calcularExponencial(double ri, double media) {
        // X = -media * ln(1 - Ri)
        return -media * Math.log(1 - ri);
    }

    // Usamos Box-Muller, necesitamos un segundo Ri auxiliar si no tenemos pares
    public double calcularNormal(double ri1, double media, double desvStd) {
        // NOTA: Para una simulación tabular estricta, la normal consume 2 números Ri por variable.
        // Para simplificar este ejemplo y usar la tabla 1 a 1, usaremos un random auxiliar para el segundo componente.
        // En una simulación muy rigurosa, deberíamos consumir 2 del conjunto.
        double ri2Aux = new Random().nextDouble();
        double z = Math.sqrt(-2 * Math.log(ri1)) * Math.cos(2 * Math.PI * ri2Aux);
        return media + desvStd * z;
    }
    
     public double calcularNormalPrecisa(double ri1, double ri2, double media, double desvStd) {
        // Versión que usa dos Ri explícitos de la lista
        double z = Math.sqrt(-2 * Math.log(ri1)) * Math.cos(2 * Math.PI * ri2);
        return media + desvStd * z;
    }
}