
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;

public class PruebaDePoker {
    private double chiCalc;
    private double chiTabla;
    private boolean acepta;
    private Object[][] tablaDatos;
    private int gradosLibertad;

    // Probabilidades EXACTAS de tu imagen
    private final double PROB_PACHUCA = 0.3024;
    private final double PROB_UN_PAR = 0.5040;
    private final double PROB_DOS_PARES = 0.1080;
    private final double PROB_TERCIA = 0.0720;
    private final double PROB_FULL = 0.0090;
    private final double PROB_POKER = 0.0045;
    private final double PROB_QUINTILLA = 0.0001;

    public void calcular(List<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        if (n == 0) return;

        // Contadores (Frecuencia Observada)
        int foPachuca = 0, foPar = 0, foDosPares = 0, foTercia = 0, foFull = 0, foPoker = 0, foQuintilla = 0;

        // Paso 3: Determinar la mano de cada número
        for (Double num : numeros) {
            String mano = clasificarMano(num);
            switch (mano) {
                case "Pachuca": foPachuca++; break;
                case "Un Par": foPar++; break;
                case "Dos Pares": foDosPares++; break;
                case "Tercia": foTercia++; break;
                case "Full": foFull++; break;
                case "Póker": foPoker++; break;
                case "Quintilla": foQuintilla++; break;
            }
        }

        // Paso 2: Calcular FE (n * probabilidad)
        double[] fe = {
            n * PROB_PACHUCA, n * PROB_UN_PAR, n * PROB_DOS_PARES, 
            n * PROB_TERCIA, n * PROB_FULL, n * PROB_POKER, n * PROB_QUINTILLA
        };
        int[] fo = {foPachuca, foPar, foDosPares, foTercia, foFull, foPoker, foQuintilla};
        String[] nombres = {"Pachuca", "Un Par", "Dos Pares", "Tercia", "Full", "Póker", "Quintilla"};

        // Paso 4: Calcular Chi Cuadrada
        chiCalc = 0;
        tablaDatos = new Object[7][4];

        for (int i = 0; i < 7; i++) {
            double chiParcial = 0;
            if (fe[i] > 0) {
                chiParcial = Math.pow(fo[i] - fe[i], 2) / fe[i];
            }
            chiCalc += chiParcial;

            tablaDatos[i][0] = nombres[i];
            tablaDatos[i][1] = fo[i];
            tablaDatos[i][2] = String.format("%.4f", fe[i]);
            tablaDatos[i][3] = String.format("%.4f", chiParcial);
        }

        // Paso 5: Comparar con tablas (gl = 6)
        this.gradosLibertad = 6;
        this.chiTabla = calcularChiCritico(gradosLibertad, 1.0 - nivelConfianza);
        this.acepta = (chiCalc < chiTabla);
    }

    private String clasificarMano(double numero) {
        String s = String.format("%.5f", numero).replace(",", ".");
        if (s.contains(".")) s = s.substring(s.indexOf(".") + 1);
        if (s.length() > 5) s = s.substring(0, 5);
        
        Map<Character, Integer> conteo = new HashMap<>();
        for (char c : s.toCharArray()) conteo.put(c, conteo.getOrDefault(c, 0) + 1);

        boolean tiene5 = conteo.containsValue(5);
        boolean tiene4 = conteo.containsValue(4);
        boolean tiene3 = conteo.containsValue(3);
        
        int pares = 0;
        for (int val : conteo.values()) if (val == 2) pares++;

        if (tiene5) return "Quintilla";
        if (tiene4) return "Póker";
        if (tiene3 && pares == 1) return "Full";
        if (tiene3) return "Tercia";
        if (pares == 2) return "Dos Pares";
        if (pares == 1) return "Un Par";
        return "Pachuca";
    }

    private double calcularChiCritico(int gl, double alpha) {
        double z = normalInversa(1 - alpha);
        double factor = 1.0 - (2.0 / (9.0 * gl)) + (z * Math.sqrt(2.0 / (9.0 * gl)));
        return gl * Math.pow(factor, 3);
    }

    private double normalInversa(double p) {
        if (p < 0.5) return -rationalApproximation(Math.sqrt(-2.0 * Math.log(p)));
        else return rationalApproximation(Math.sqrt(-2.0 * Math.log(1.0 - p)));
    }

    private double rationalApproximation(double t) {
        double[] c = {2.515517, 0.802853, 0.010328};
        double[] d = {1.432788, 0.189269, 0.001308};
        return t - ((c[2]*t + c[1])*t + c[0]) / (((d[2]*t + d[1])*t + d[0])*t + 1.0);
    }

    public double getChiCalc() { return chiCalc; }
    public double getChiTabla() { return chiTabla; }
    public boolean isAcepta() { return acepta; }
    
    public DefaultTableModel getModeloTabla() {
        String[] columnas = {"Mano", "FO", "FE", "(FO-FE)²/FE"};
        return new DefaultTableModel(tablaDatos, columnas);
    }
}