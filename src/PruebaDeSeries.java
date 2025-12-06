
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class PruebaDeSeries {
    private double chiCalc;
    private double chiTabla;
    private boolean acepta;
    private Object[][] tablaDatos; 
    private int gradosLibertad;
    private int totalPares;

    public void calcular(List<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        if (n < 2) return;

        // --- CRITICAL CORRECTION ---
        // Matrix 4x4 = 16 cells.
        // Degrees of freedom = 16 - 1 = 15.
        // For 95% confidence, Chi Critical (df=15) is approx 25.00.
        int m = 4; 
        
        int celdas = m * m; // 16 cells
        
        // Use circular pairing so Total Pairs = N
        // This ensures FE = 24 / 16 = 1.5 exactly.
        this.totalPares = n; 
        
        int[][] fo = new int[m][m];
        double fe = (double) totalPares / celdas; // Expected Frequency

        // 1. Classify pairs (ri, ri+1) using circular logic
        for (int i = 0; i < totalPares; i++) {
            double val1 = numeros.get(i);
            // The % n operator connects the last number to the first
            double val2 = numeros.get((i + 1) % n); 
            
            // Determine row and column (0 to 3)
            int fila = (int) (val1 * m);
            int col = (int) (val2 * m);
            
            // Boundary adjustment
            if (fila == m) fila = m - 1;
            if (col == m) col = m - 1;
            
            fo[fila][col]++;
        }

        // 2. Calculate Chi Square
        chiCalc = 0;
        
        // Prepare data for visual table
        tablaDatos = new Object[celdas][4];
        int idxTabla = 0;
        double paso = 1.0 / m; // 0.25

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                double chiParcial = 0;
                if (fe > 0) {
                    chiParcial = Math.pow(fo[i][j] - fe, 2) / fe;
                }
                chiCalc += chiParcial;

                // Visual format: "X[0.00-0.25], Y[0.25-0.50]"
                String intervalo = String.format("X[%.2f-%.2f], Y[%.2f-%.2f]", 
                        i*paso, (i+1)*paso, j*paso, (j+1)*paso);

                tablaDatos[idxTabla][0] = intervalo;
                tablaDatos[idxTabla][1] = fo[i][j];
                tablaDatos[idxTabla][2] = String.format("%.4f", fe);
                tablaDatos[idxTabla][3] = String.format("%.4f", chiParcial);
                idxTabla++;
            }
        }

        // 3. Compare with tables
        // Degrees of freedom = (cells - 1) = 15
        this.gradosLibertad = celdas - 1;
        this.chiTabla = calcularChiCritico(gradosLibertad, 1.0 - nivelConfianza);
        
        // Evaluate H0
        this.acepta = (chiCalc < chiTabla);
    }

    // Wilson-Hilferty Approximation
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
    public int getTotalPares() { return totalPares; }
    
    public DefaultTableModel getModeloTabla() {
        String[] columnas = {"Coordenada (Intervalos)", "FO", "FE", "(FO-FE)²/FE"};
        return new DefaultTableModel(tablaDatos, columnas);
    }
}