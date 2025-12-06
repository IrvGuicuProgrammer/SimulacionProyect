import java.util.*;

public class EstadisticasCalculador {
    
    public static class ResultadoPrueba {
        public boolean pasa;
        public String mensaje;
        public double estadistico;
        public int gradosLibertad;
        public double valorCritico;
        public Object[][] datosTabla;
        public double[][] datosGrafico; // [0] = FO, [1] = FE
        public double[] datosAdicionales;
        public double nivelConfianza;
        
        public ResultadoPrueba(boolean pasa, String mensaje, double estadistico) {
            this.pasa = pasa;
            this.mensaje = mensaje;
            this.estadistico = estadistico;
            this.nivelConfianza = 0.95; // Valor por defecto
        }
        
        public ResultadoPrueba(boolean pasa, String mensaje, double estadistico, double nivelConfianza) {
            this.pasa = pasa;
            this.mensaje = mensaje;
            this.estadistico = estadistico;
            this.nivelConfianza = nivelConfianza;
        }
    }
    
    public static double calcularMedia(List<Double> numeros) {
        return numeros.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    public static double calcularVarianza(List<Double> numeros) {
        double media = calcularMedia(numeros);
        double sumaCuadrados = numeros.stream()
            .mapToDouble(x -> Math.pow(x - media, 2))
            .sum();
        return sumaCuadrados / numeros.size();
    }
    
    public static double calcularDesviacionEstandar(List<Double> numeros) {
        return Math.sqrt(calcularVarianza(numeros));
    }
    
    // Tabla de valores críticos Z para diferentes niveles de confianza
    private static double obtenerValorZ(double nivelConfianza) {
        Map<Double, Double> tablaZ = new HashMap<>();
        tablaZ.put(0.80, 1.282);  // 80%
        tablaZ.put(0.85, 1.440);  // 85%
        tablaZ.put(0.90, 1.645);  // 90%
        tablaZ.put(0.95, 1.96);   // 95% (default)
        tablaZ.put(0.99, 2.576);  // 99%
        
        return tablaZ.getOrDefault(nivelConfianza, 1.96);
    }
    
    // Tabla de valores críticos Chi-cuadrado para diferentes niveles de confianza
    private static Map<Integer, Map<Double, Double>> tablaChiCritico = new HashMap<>();
    
    static {
        // Grados de libertad: 6 (para prueba Poker)
        Map<Double, Double> chiGL6 = new HashMap<>();
        chiGL6.put(0.80, 8.5581);   // 80%
        chiGL6.put(0.85, 9.4460);   // 85%
        chiGL6.put(0.90, 10.6446);  // 90%
        chiGL6.put(0.95, 12.5916);  // 95%
        chiGL6.put(0.99, 16.8119);  // 99%
        tablaChiCritico.put(6, chiGL6);
        
        // Grados de libertad: 24 (para prueba Series)
        Map<Double, Double> chiGL24 = new HashMap<>();
        chiGL24.put(0.80, 29.5533);  // 80%
        chiGL24.put(0.85, 31.4170);  // 85%
        chiGL24.put(0.90, 33.1962);  // 90%
        chiGL24.put(0.95, 36.4150);  // 95%
        chiGL24.put(0.99, 42.9798);  // 99%
        tablaChiCritico.put(24, chiGL24);
        
        // Grados de libertad: 4 (para prueba Uniformidad con 5 clases)
        Map<Double, Double> chiGL4 = new HashMap<>();
        chiGL4.put(0.80, 5.9886);   // 80%
        chiGL4.put(0.85, 6.7454);   // 85%
        chiGL4.put(0.90, 7.7794);   // 90%
        chiGL4.put(0.95, 9.4877);   // 95%
        chiGL4.put(0.99, 13.2767);  // 99%
        tablaChiCritico.put(4, chiGL4);
        
        // Grados de libertad: 3 (para prueba Uniformidad con 4 clases)
        Map<Double, Double> chiGL3 = new HashMap<>();
        chiGL3.put(0.80, 4.6416);   // 80%
        chiGL3.put(0.85, 5.3170);   // 85%
        chiGL3.put(0.90, 6.2514);   // 90%
        chiGL3.put(0.95, 7.8147);   // 95%
        chiGL3.put(0.99, 11.3449);  // 99%
        tablaChiCritico.put(3, chiGL3);
        
        // Grados de libertad: 5 (para prueba Uniformidad con 6 clases)
        Map<Double, Double> chiGL5 = new HashMap<>();
        chiGL5.put(0.80, 7.2893);   // 80%
        chiGL5.put(0.85, 8.1152);   // 85%
        chiGL5.put(0.90, 9.2364);   // 90%
        chiGL5.put(0.95, 11.0705);  // 95%
        chiGL5.put(0.99, 15.0863);  // 99%
        tablaChiCritico.put(5, chiGL5);
    }
    
    private static double obtenerValorCriticoChi(int gl, double nivelConfianza) {
        Map<Double, Double> valoresGL = tablaChiCritico.get(gl);
        if (valoresGL != null) {
            return valoresGL.getOrDefault(nivelConfianza, valoresGL.get(0.95));
        }
        
        // Si no está en la tabla, usar aproximación (solo para mostrar)
        return 12.5916; // Valor por defecto para 6 gl, 95%
    }
    
    public static ResultadoPrueba pruebaPoker(List<Double> numeros) {
        return pruebaPoker(numeros, 0.95);
    }
    
    public static ResultadoPrueba pruebaPoker(List<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        
        // Categorías de poker
        int[] frecObservadas = new int[7]; // TD, 1P, 2P, T, TP, P, Q
        double[] frecEsperadas = {0.3024, 0.5040, 0.1080, 0.0720, 0.0090, 0.0045, 0.0001};
        
        for (Double num : numeros) {
            String str = String.format("%.4f", num).substring(2, 6);
            int categoria = clasificarMano(str); // CORRECCIÓN: cambiado de clasificanMano a clasificarMano
            frecObservadas[categoria]++;
        }
        
        // Calcular Chi²
        double chiCuadrado = 0;
        double[][] datosGrafico = new double[2][7];
        Object[][] datosTabla = new Object[7][4];
        String[] manos = {"TD", "1P", "2P", "T", "TP", "P", "Q"};
        
        for (int i = 0; i < 7; i++) {
            double fe = frecEsperadas[i] * n;
            double fo = frecObservadas[i];
            double chi = Math.pow(fo - fe, 2) / fe;
            chiCuadrado += chi;
            
            datosGrafico[0][i] = fo;
            datosGrafico[1][i] = fe;
            datosTabla[i] = new Object[]{manos[i], (int)fo, String.format("%.4f", fe), 
                String.format("%.4f", chi)};
        }
        
        int gl = 6; // 7 categorías - 1
        double chiCritico = obtenerValorCriticoChi(gl, nivelConfianza);
        
        ResultadoPrueba resultado = new ResultadoPrueba(
            chiCuadrado < chiCritico,
            chiCuadrado < chiCritico ? "Se Rechaza H0" : "Se Acepta H0",
            chiCuadrado,
            nivelConfianza
        );
        resultado.gradosLibertad = gl;
        resultado.valorCritico = chiCritico;
        resultado.datosTabla = datosTabla;
        resultado.datosGrafico = datosGrafico;
        
        return resultado;
    }
    
    private static int clasificarMano(String digitos) {
        Map<Character, Integer> conteo = new HashMap<>();
        for (char c : digitos.toCharArray()) {
            conteo.put(c, conteo.getOrDefault(c, 0) + 1);
        }
        
        List<Integer> frecuencias = new ArrayList<>(conteo.values());
        Collections.sort(frecuencias, Collections.reverseOrder());
        
        if (frecuencias.get(0) == 4) return 6; // Q (Poker)
        if (frecuencias.get(0) == 3) {
            if (frecuencias.size() > 1 && frecuencias.get(1) == 2) return 5; // P (Full)
            return 3; // T (Tercia)
        }
        if (frecuencias.get(0) == 2) {
            if (frecuencias.size() > 1 && frecuencias.get(1) == 2) return 2; // 2P (Dos pares)
            return 1; // 1P (Un par)
        }
        if (frecuencias.size() == 4) return 4; // TP (Tres pares - dos cartas iguales)
        return 0; // TD (Todas diferentes)
    }
    
    public static ResultadoPrueba pruebaCorridas(List<Double> numeros) {
        return pruebaCorridas(numeros, 0.95);
    }
    
    public static ResultadoPrueba pruebaCorridas(List<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        double media = calcularMedia(numeros);
        
        // Contar corridas
        int corridas = 1;
        boolean arribaDeLaMedia = numeros.get(0) >= media;
        
        for (int i = 1; i < n; i++) {
            boolean actualArribaDeLaMedia = numeros.get(i) >= media;
            if (actualArribaDeLaMedia != arribaDeLaMedia) {
                corridas++;
                arribaDeLaMedia = actualArribaDeLaMedia;
            }
        }
        
        // Calcular estadísticas
        double corridasEsperadas = (2.0 * n - 1.0) / 3.0;
        double varianza = (16.0 * n - 29.0) / 90.0;
        double z = (corridas - corridasEsperadas) / Math.sqrt(varianza);
        
        double valorZCritico = obtenerValorZ(nivelConfianza);
        
        ResultadoPrueba resultado = new ResultadoPrueba(
            Math.abs(z) < valorZCritico,
            Math.abs(z) < valorZCritico ? "Se Acepta H0 (Independencia)" : "Se Rechaza H0",
            z,
            nivelConfianza
        );
        resultado.datosAdicionales = new double[]{corridas, corridasEsperadas, varianza, valorZCritico};
        
        return resultado;
    }
    
    public static ResultadoPrueba pruebaSeries(List<Double> numeros) {
        return pruebaSeries(numeros, 0.95);
    }
    
    public static ResultadoPrueba pruebaSeries(List<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        int nPares = n - 1;
        
        // Crear matriz 5x5 para intervalos
        int[][] matriz = new int[5][5];
        
        for (int i = 0; i < nPares; i++) {
            int fila = Math.min((int)(numeros.get(i) * 5), 4);
            int col = Math.min((int)(numeros.get(i + 1) * 5), 4);
            matriz[fila][col]++;
        }
        
        // Calcular Chi²
        double chiCuadrado = 0;
        double fe = nPares / 25.0;
        
        List<Object[]> listaTabla = new ArrayList<>();
        List<Double> listaFO = new ArrayList<>();
        List<Double> listaFE = new ArrayList<>();
        
        int contador = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int fo = matriz[i][j];
                double chi = Math.pow(fo - fe, 2) / fe;
                chiCuadrado += chi;
                
                String intervalo = String.format("[0.0-0.2)x(0.%-1d-0.%-1d)", 
                    j * 2, (j + 1) * 2);
                listaTabla.add(new Object[]{intervalo, fo, String.format("%.2f", fe), 
                    String.format("%.4f", chi)});
                listaFO.add((double)fo);
                listaFE.add(fe);
                contador++;
            }
        }
        
        Object[][] datosTabla = listaTabla.toArray(new Object[0][]);
        double[][] datosGrafico = new double[2][25];
        for (int i = 0; i < 25; i++) {
            datosGrafico[0][i] = listaFO.get(i);
            datosGrafico[1][i] = listaFE.get(i);
        }
        
        int gl = 24; // 25 celdas - 1
        double chiCritico = obtenerValorCriticoChi(gl, nivelConfianza);
        
        ResultadoPrueba resultado = new ResultadoPrueba(
            chiCuadrado < chiCritico,
            chiCuadrado < chiCritico ? "Se Rechaza H0" : "Se Acepta H0",
            chiCuadrado,
            nivelConfianza
        );
        resultado.gradosLibertad = gl;
        resultado.valorCritico = chiCritico;
        resultado.datosTabla = datosTabla;
        resultado.datosGrafico = datosGrafico;
        
        return resultado;
    }
    
    public static ResultadoPrueba pruebaMedia(List<Double> numeros) {
        return pruebaMedia(numeros, 0.95);
    }
    
    public static ResultadoPrueba pruebaMedia(List<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        double media = calcularMedia(numeros);
        
        double valorZ = obtenerValorZ(nivelConfianza);
        
        // Límites para la media (distribución normal)
        double limInferior = 0.5 - valorZ * Math.sqrt(1.0 / (12.0 * n));
        double limSuperior = 0.5 + valorZ * Math.sqrt(1.0 / (12.0 * n));
        
        boolean pasa = media >= limInferior && media <= limSuperior;
        
        ResultadoPrueba resultado = new ResultadoPrueba(
            pasa,
            pasa ? "Se Acepta H0" : "Se Rechaza H0",
            media,
            nivelConfianza
        );
        resultado.datosAdicionales = new double[]{media, limInferior, limSuperior, valorZ};
        
        return resultado;
    }
    
    public static ResultadoPrueba pruebaVarianza(List<Double> numeros) {
        return pruebaVarianza(numeros, 0.95);
    }
    
    public static ResultadoPrueba pruebaVarianza(List<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        double varianza = calcularVarianza(numeros);
        
        double valorZ = obtenerValorZ(nivelConfianza);
        
        // Límites para la varianza
        double limInferior = (1.0 / 12.0) - valorZ * Math.sqrt(1.0 / (12.0 * n));
        double limSuperior = (1.0 / 12.0) + valorZ * Math.sqrt(1.0 / (12.0 * n));
        
        boolean pasa = varianza >= limInferior && varianza <= limSuperior;
        
        ResultadoPrueba resultado = new ResultadoPrueba(
            pasa,
            pasa ? "Se Acepta H0" : "Se Rechaza H0",
            varianza,
            nivelConfianza
        );
        resultado.datosAdicionales = new double[]{varianza, limInferior, limSuperior, valorZ};
        
        return resultado;
    }
    
    public static ResultadoPrueba pruebaUniformidad(List<Double> numeros, int numClases) {
        return pruebaUniformidad(numeros, numClases, 0.95);
    }
    
    public static ResultadoPrueba pruebaUniformidad(List<Double> numeros, int numClases, double nivelConfianza) {
        int n = numeros.size();
        int[] frecObservadas = new int[numClases];
        
        // Clasificar números en intervalos
        for (Double num : numeros) {
            int clase = Math.min((int)(num * numClases), numClases - 1);
            frecObservadas[clase]++;
        }
        
        // Calcular Chi²
        double fe = (double)n / numClases;
        double chiCuadrado = 0;
        
        Object[][] datosTabla = new Object[numClases][4];
        double[][] datosGrafico = new double[2][numClases];
        
        for (int i = 0; i < numClases; i++) {
            int fo = frecObservadas[i];
            chiCuadrado += Math.pow(fo - fe, 2) / fe;
            
            double limInf = (double)i / numClases;
            double limSup = (double)(i + 1) / numClases;
            String intervalo = String.format("[%.3f, %.3f)", limInf, limSup);
            
            datosTabla[i] = new Object[]{i + 1, intervalo, fo, String.format("%.2f", fe)};
            datosGrafico[0][i] = fo;
            datosGrafico[1][i] = fe;
        }
        
        int gl = numClases - 1;
        double chiCritico = obtenerValorCriticoChi(gl, nivelConfianza);
        
        ResultadoPrueba resultado = new ResultadoPrueba(
            chiCuadrado < chiCritico,
            chiCuadrado < chiCritico ? "Se Acepta H0 (Uniforme)" : "Se Rechaza H0",
            chiCuadrado,
            nivelConfianza
        );
        resultado.gradosLibertad = gl;
        resultado.valorCritico = chiCritico;
        resultado.datosTabla = datosTabla;
        resultado.datosGrafico = datosGrafico;
        
        return resultado;
    }
}
