import java.util.List;
import java.util.ArrayList;

public class PruebaDeCorridas {
    private int corridas;       // h
    private double mediaEsperada; // E(h)
    private double varianza;    // V(h)
    private double zCalculado;  // Z
    private double zCritico;    // Valor de tabla
    private boolean acepta;
    private String secuenciaSignos; // Para mostrar visualmente "+ + - +"

    public void calcular(List<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        if (n < 2) return;

        // Paso 1: Clasificar signos (S_i)
        // Comparamos r_i con r_{i-1}
        StringBuilder sb = new StringBuilder();
        List<Boolean> signos = new ArrayList<>(); // true = (+), false = (-)
        
        for (int i = 1; i < n; i++) {
            double actual = numeros.get(i);
            double anterior = numeros.get(i-1);
            
            if (actual <= anterior) {
                signos.add(false); // Menos (-)
                sb.append("- ");
            } else {
                signos.add(true);  // Mas (+)
                sb.append("+ ");
            }
        }
        this.secuenciaSignos = sb.toString();

        // Paso 2: Calcular el número de corridas (h)
        // Una corrida termina cuando el signo cambia
        this.corridas = 1; // Empezamos con la primera corrida
        for (int i = 1; i < signos.size(); i++) {
            // Si el signo actual es diferente al anterior, hay cambio -> nueva corrida
            if (!signos.get(i).equals(signos.get(i-1))) {
                this.corridas++;
            }
        }

        // Paso 3: Calcular E(h) y V(h)
        // E(h) = (2n - 1) / 3
        this.mediaEsperada = (2.0 * n - 1.0) / 3.0;

        // V(h) = (16n - 29) / 90
        this.varianza = (16.0 * n - 29.0) / 90.0;

        // Paso 4: Calcular el estadístico Z
        // Z = (h - E(h)) / sqrt(V(h))
        this.zCalculado = (this.corridas - this.mediaEsperada) / Math.sqrt(this.varianza);

        // Paso 5: Comparación con Z crítico
        // Es una prueba bilateral, buscamos Z para (1 - alfa/2)
        this.zCritico = normalInversa(1.0 - (1.0 - nivelConfianza) / 2.0);

        // Si |Z_calc| < Z_critico, se acepta
        this.acepta = Math.abs(zCalculado) < zCritico;
    }

    // Aproximación de Hastings para la Normal Estándar Inversa (Z)
    private double normalInversa(double p) {
        if (p < 0.5) return -rationalApproximation(Math.sqrt(-2.0 * Math.log(p)));
        else return rationalApproximation(Math.sqrt(-2.0 * Math.log(1.0 - p)));
    }

    private double rationalApproximation(double t) {
        double[] c = {2.515517, 0.802853, 0.010328};
        double[] d = {1.432788, 0.189269, 0.001308};
        return t - ((c[2]*t + c[1])*t + c[0]) / (((d[2]*t + d[1])*t + d[0])*t + 1.0);
    }

    // Getters
    public int getCorridas() { return corridas; }
    public double getMediaEsperada() { return mediaEsperada; }
    public double getVarianza() { return varianza; }
    public double getZCalculado() { return zCalculado; }
    public double getZCritico() { return zCritico; }
    public boolean isAcepta() { return acepta; }
    public String getSecuenciaSignos() { return secuenciaSignos; }
}