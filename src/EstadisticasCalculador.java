import java.util.*;

public class EstadisticasCalculador {
    
    // Clase interna para devolver resultados a la interfaz (No cambiar, la interfaz la necesita)
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
            this.nivelConfianza = 0.95; 
        }
        
        public ResultadoPrueba(boolean pasa, String mensaje, double estadistico, double nivelConfianza) {
            this.pasa = pasa;
            this.mensaje = mensaje;
            this.estadistico = estadistico;
            this.nivelConfianza = nivelConfianza;
        }
    }
    
    // --- MÉTODOS AUXILIARES MATEMÁTICOS (Extraídos de tus archivos de pruebas) ---

    // Aproximación de Hastings para la Normal Estándar Inversa (Z)
    // Usado en Corridas, Series, Poker, Medias y Varianza
    private static double normalInversa(double p) {
        if (p < 0.5) return -rationalApproximation(Math.sqrt(-2.0 * Math.log(p)));
        else return rationalApproximation(Math.sqrt(-2.0 * Math.log(1.0 - p)));
    }

    private static double rationalApproximation(double t) {
        double[] c = {2.515517, 0.802853, 0.010328};
        double[] d = {1.432788, 0.189269, 0.001308};
        return t - ((c[2]*t + c[1])*t + c[0]) / (((d[2]*t + d[1])*t + d[0])*t + 1.0);
    }

    // Aproximación Wilson-Hilferty para Chi-Cuadrada Crítica
    // Usado para Poker, Series y para calcular los límites de Varianza
    private static double calcularChiCritico(int gl, double alpha) {
        double z = normalInversa(1 - alpha);
        double factor = 1.0 - (2.0 / (9.0 * gl)) + (z * Math.sqrt(2.0 / (9.0 * gl)));
        return gl * Math.pow(factor, 3);
    }

    public static double calcularMedia(List<Double> numeros) {
        return numeros.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    public static double calcularVarianza(List<Double> numeros) {
        double media = calcularMedia(numeros);
        double sumaCuadrados = numeros.stream()
            .mapToDouble(x -> Math.pow(x - media, 2))
            .sum();
        // Nota: Usamos n (poblacional) o n-1 (muestral) según contexto. 
        // Tu archivo PruebaDeVarianza usa (n-1).
        return sumaCuadrados / (numeros.size() - 1); 
    }

    // -------------------------------------------------------------------------
    // 1. PRUEBA DE MEDIAS
    // Basado en PruebaDeMedias.java
    // -------------------------------------------------------------------------
    public static ResultadoPrueba pruebaMedia(List<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        double media = calcularMedia(numeros);
        
        // Calcular Z dependiente del nivel de confianza usando la normal inversa
        // Es una prueba de dos colas, así que buscamos (1 - alpha/2)
        double alpha = 1.0 - nivelConfianza;
        double z = normalInversa(1.0 - (alpha / 2.0));
        
        // Límites
        double raiz12n = Math.sqrt(12.0 * n);
        double li = 0.5 - z * (1.0 / raiz12n);
        double ls = 0.5 + z * (1.0 / raiz12n);
        
        boolean pasa = media >= li && media <= ls;
        
        ResultadoPrueba resultado = new ResultadoPrueba(
            pasa,
            pasa ? "Se Acepta H0" : "Se Rechaza H0",
            media,
            nivelConfianza
        );
        // Guardamos datos para la gráfica: media, limInf, limSup, zCritico
        resultado.datosAdicionales = new double[]{media, li, ls, z};
        
        return resultado;
    }
    
    // -------------------------------------------------------------------------
    // 2. PRUEBA DE VARIANZA
    // Basado en PruebaDeVarianza.java
    // -------------------------------------------------------------------------
    public static ResultadoPrueba pruebaVarianza(List<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        double varianza = calcularVarianza(numeros);
        int gl = n - 1;
        double alpha = 1.0 - nivelConfianza;

        // Calcular Chi Cuadrada Izquierda y Derecha usando Wilson-Hilferty
        // Chi Izquierda (probabilidad alpha/2)
        double chiIzquierda = calcularChiCritico(gl, 1.0 - (alpha / 2.0)); 
        // Chi Derecha (probabilidad 1 - alpha/2)
        double chiDerecha = calcularChiCritico(gl, alpha / 2.0);

        // Cálculo de límites
        double li = chiIzquierda / (12.0 * gl);
        double ls = chiDerecha / (12.0 * gl);
        
        boolean pasa = varianza >= li && varianza <= ls;
        
        // Para graficar necesitamos un valor Z equivalente para pintar el área
        double z = normalInversa(1.0 - (alpha / 2.0));

        ResultadoPrueba resultado = new ResultadoPrueba(
            pasa,
            pasa ? "Se Acepta H0" : "Se Rechaza H0",
            varianza,
            nivelConfianza
        );
        resultado.datosAdicionales = new double[]{varianza, li, ls, z};
        
        return resultado;
    }
    
    // -------------------------------------------------------------------------
    // 3. PRUEBA DE UNIFORMIDAD (PruebaForma)
    // Basado en PruebaForma.java (Usa 10 intervalos fijos)
    // -------------------------------------------------------------------------
    public static ResultadoPrueba pruebaUniformidad(List<Double> numeros, int numClasesIgnorado, double nivelConfianza) {
        // Tu código de PruebaForma usa siempre 10 intervalos
        int numClases = 10; 
        int n = numeros.size();
        int[] fo = new int[numClases];
        
        // Clasificación
        for (Double valor : numeros) {
            int indiceIntervalo = (int) (valor * 10);
            if (indiceIntervalo == 10) indiceIntervalo = 9;
            fo[indiceIntervalo]++;
        }
        
        double fe = n / 10.0;
        double chiCalculada = 0;
        
        Object[][] datosTabla = new Object[numClases][4];
        double[][] datosGrafico = new double[2][numClases];
        
        for (int i = 0; i < numClases; i++) {
            double chiParcial = Math.pow(fo[i] - fe, 2) / fe;
            chiCalculada += chiParcial;
            
            double limInf = i / 10.0;
            double limSup = (i + 1) / 10.0;
            String intervalo = String.format("[%.1f, %.1f)", limInf, limSup);
            
            datosTabla[i] = new Object[]{i + 1, intervalo, fo[i], String.format("%.2f", fe)};
            datosGrafico[0][i] = fo[i];
            datosGrafico[1][i] = fe;
        }
        
        // Grados de libertad = k - 1
        int gl = numClases - 1;
        
        // Chi Critico dinámico
        double chiCritico = calcularChiCritico(gl, 1.0 - nivelConfianza);
        
        ResultadoPrueba resultado = new ResultadoPrueba(
            chiCalculada <= chiCritico,
            chiCalculada <= chiCritico ? "Se Acepta H0 (Uniforme)" : "Se Rechaza H0",
            chiCalculada,
            nivelConfianza
        );
        resultado.gradosLibertad = gl;
        resultado.valorCritico = chiCritico;
        resultado.datosTabla = datosTabla;
        resultado.datosGrafico = datosGrafico;
        
        return resultado;
    }

    // -------------------------------------------------------------------------
    // 4. PRUEBA DE CORRIDAS
    // Basado en PruebaDeCorridas.java
    // -------------------------------------------------------------------------
    public static ResultadoPrueba pruebaCorridas(List<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        
        // Paso 1: Clasificar signos y contar corridas
        int corridas = 1;
        if (n > 1) {
            boolean signoAnterior = (numeros.get(1) > numeros.get(0)); // true(+), false(-)
            
            for (int i = 2; i < n; i++) {
                boolean signoActual = (numeros.get(i) > numeros.get(i-1));
                if (signoActual != signoAnterior) {
                    corridas++;
                    signoAnterior = signoActual;
                }
            }
        }
        
        // Paso 3: Calcular E(h) y V(h)
        double mediaEsperada = (2.0 * n - 1.0) / 3.0;
        double varianza = (16.0 * n - 29.0) / 90.0;
        
        // Paso 4: Estadístico Z
        double zCalculado = (corridas - mediaEsperada) / Math.sqrt(varianza);
        
        // Paso 5: Z Crítico (Bilateral)
        double alpha = 1.0 - nivelConfianza;
        double zCritico = normalInversa(1.0 - (alpha / 2.0));
        
        ResultadoPrueba resultado = new ResultadoPrueba(
            Math.abs(zCalculado) < zCritico,
            Math.abs(zCalculado) < zCritico ? "Se Acepta H0 (Independencia)" : "Se Rechaza H0",
            zCalculado,
            nivelConfianza
        );
        // Guardamos datos para mostrar en la interfaz
        resultado.datosAdicionales = new double[]{corridas, mediaEsperada, varianza, zCritico};
        
        return resultado;
    }

    // -------------------------------------------------------------------------
    // 5. PRUEBA DE SERIES
    // Basado en PruebaDeSeries.java (4x4 celdas, emparejamiento circular)
    // -------------------------------------------------------------------------
    public static ResultadoPrueba pruebaSeries(List<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        int m = 4; // Matriz 4x4 según tu clase
        int celdas = m * m; // 16 celdas
        
        int[][] fo = new int[m][m];
        double fe = (double) n / celdas; 
        
        // 1. Clasificar pares (circular)
        for (int i = 0; i < n; i++) {
            double val1 = numeros.get(i);
            double val2 = numeros.get((i + 1) % n); // Operador módulo para circularidad
            
            int fila = (int) (val1 * m);
            int col = (int) (val2 * m);
            
            if (fila == m) fila = m - 1;
            if (col == m) col = m - 1;
            
            fo[fila][col]++;
        }
        
        // 2. Calcular Chi Cuadrada
        double chiCalculada = 0;
        Object[][] datosTabla = new Object[celdas][4];
        double[][] datosGrafico = new double[2][celdas]; // Para pintar barras si se requiere
        
        int idx = 0;
        double paso = 1.0 / m;
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                double chiParcial = Math.pow(fo[i][j] - fe, 2) / fe;
                chiCalculada += chiParcial;
                
                String intervalo = String.format("X[%.2f-%.2f], Y[%.2f-%.2f]", 
                        i*paso, (i+1)*paso, j*paso, (j+1)*paso);
                
                datosTabla[idx] = new Object[]{intervalo, fo[i][j], String.format("%.2f", fe), String.format("%.4f", chiParcial)};
                datosGrafico[0][idx] = fo[i][j];
                datosGrafico[1][idx] = fe;
                idx++;
            }
        }
        
        int gl = celdas - 1; // 15
        double chiCritico = calcularChiCritico(gl, 1.0 - nivelConfianza);
        
        ResultadoPrueba resultado = new ResultadoPrueba(
            chiCalculada < chiCritico,
            chiCalculada < chiCritico ? "Se Acepta H0" : "Se Rechaza H0",
            chiCalculada,
            nivelConfianza
        );
        resultado.gradosLibertad = gl;
        resultado.valorCritico = chiCritico;
        resultado.datosTabla = datosTabla;
        resultado.datosGrafico = datosGrafico;
        
        return resultado;
    }

    // -------------------------------------------------------------------------
    // 6. PRUEBA DE POKER
    // Basado en PruebaDePoker.java (Probabilidades exactas, 5 decimales)
    // -------------------------------------------------------------------------
    public static ResultadoPrueba pruebaPoker(List<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        
        // Probabilidades exactas del archivo adjunto
        double[] frecEsperadas = {0.3024, 0.5040, 0.1080, 0.0720, 0.0090, 0.0045, 0.0001};
        // Pachuca, 1P, 2P, Tercia, Full, Poker, Quintilla
        int[] frecObservadas = new int[7]; 
        
        for (Double num : numeros) {
            String mano = clasificarManoPoker(num);
            switch (mano) {
                case "Pachuca": frecObservadas[0]++; break;
                case "Un Par": frecObservadas[1]++; break;
                case "Dos Pares": frecObservadas[2]++; break;
                case "Tercia": frecObservadas[3]++; break;
                case "Full": frecObservadas[4]++; break;
                case "Póker": frecObservadas[5]++; break;
                case "Quintilla": frecObservadas[6]++; break;
            }
        }
        
        double chiCalculada = 0;
        double[][] datosGrafico = new double[2][7];
        Object[][] datosTabla = new Object[7][4];
        String[] nombres = {"Pachuca", "Un Par", "Dos Pares", "Tercia", "Full", "Póker", "Quintilla"};
        
        for (int i = 0; i < 7; i++) {
            double fe = frecEsperadas[i] * n;
            double fo = frecObservadas[i];
            double chi = (fe > 0) ? Math.pow(fo - fe, 2) / fe : 0;
            chiCalculada += chi;
            
            datosGrafico[0][i] = fo;
            datosGrafico[1][i] = fe;
            datosTabla[i] = new Object[]{nombres[i], (int)fo, String.format("%.4f", fe), String.format("%.4f", chi)};
        }
        
        int gl = 6;
        double chiCritico = calcularChiCritico(gl, 1.0 - nivelConfianza);
        
        ResultadoPrueba resultado = new ResultadoPrueba(
            chiCalculada < chiCritico,
            chiCalculada < chiCritico ? "Se Rechaza H0" : "Se Acepta H0", // Nota: En Poker "Menor es Mejor" típicamente para aceptar uniformidad
            chiCalculada,
            nivelConfianza
        );
        resultado.gradosLibertad = gl;
        resultado.valorCritico = chiCritico;
        resultado.datosTabla = datosTabla;
        resultado.datosGrafico = datosGrafico;
        
        return resultado;
    }
    
    // Método auxiliar específico para Poker (Extrae 5 dígitos exactos)
    private static String clasificarManoPoker(double numero) {
        // Lógica de PruebaDePoker.java: convertir a string, quitar "0.", tomar 5 chars
        String s = String.format("%.5f", numero).replace(",", ".");
        if (s.contains(".")) s = s.substring(s.indexOf(".") + 1);
        if (s.length() > 5) s = s.substring(0, 5);
        // Rellenar si faltan dígitos (raro con format %.5f pero por seguridad)
        while (s.length() < 5) s += "0";
        
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
}