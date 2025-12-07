import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Singleton para compartir datos entre las ventanas de la simulación.
 * Contiene los números aleatorios generados, los datos reales cargados y la lógica matemática.
 */
public class SimulacionDatos {
    private static SimulacionDatos instancia;

    private List<Double> conjunto1RiEn; // Para tiempos entre llegadas
    private List<Double> conjunto2RiSn; // Para tiempos de servicio
    private List<Double> datosRealesLlegadas; // NUEVO: Datos cargados del CSV
    private int nGenerados;

    private SimulacionDatos() {
        conjunto1RiEn = new ArrayList<>();
        conjunto2RiSn = new ArrayList<>();
        datosRealesLlegadas = new ArrayList<>(); // Inicializar lista nueva
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

    // --- NUEVOS MÉTODOS PARA DATOS REALES ---
    public void setDatosRealesLlegadas(List<Double> datos) {
        this.datosRealesLlegadas = new ArrayList<>(datos);
    }

    public List<Double> getDatosRealesLlegadas() {
        return datosRealesLlegadas;
    }
    // ----------------------------------------

    public List<Double> getConjunto1RiEn() { return conjunto1RiEn; }
    public List<Double> getConjunto2RiSn() { return conjunto2RiSn; }
    public int getNGenerados() { return nGenerados; }
    public boolean hayDatos() { return nGenerados > 0 && !conjunto1RiEn.isEmpty(); }

    // --- LÓGICA MATEMÁTICA ---

    public double calcularUniforme(double ri, double a, double b) {
        return a + (b - a) * ri;
    }

    public double calcularExponencial(double ri, double media) {
        // X = -media * ln(1 - Ri)
        return -media * Math.log(1 - ri);
    }

    public double calcularNormal(double ri1, double media, double desvStd) {
        double ri2Aux = new Random().nextDouble();
        double z = Math.sqrt(-2 * Math.log(ri1)) * Math.cos(2 * Math.PI * ri2Aux);
        return media + desvStd * z;
    }
    
     public double calcularNormalPrecisa(double ri1, double ri2, double media, double desvStd) {
        double z = Math.sqrt(-2 * Math.log(ri1)) * Math.cos(2 * Math.PI * ri2);
        return media + desvStd * z;
    }
}