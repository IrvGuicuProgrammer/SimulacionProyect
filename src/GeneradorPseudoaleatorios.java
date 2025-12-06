import java.util.ArrayList;
import java.util.List;

public class GeneradorPseudoaleatorios {
    
    /**
     * Método Congruencial Mixto (TU VERSIÓN CORREGIDA)
     * Ahora devuelve un array con 2 listas de números
     */
    public static List<Double>[] generarCongruencialMixto(int n, int x0, int a, int c, int m, int numConjuntos) {
        @SuppressWarnings("unchecked")
        List<Double>[] conjuntos = new ArrayList[numConjuntos];
        
        for (int conjunto = 0; conjunto < numConjuntos; conjunto++) {
            List<Double> numeros = new ArrayList<>();
            long currentX = x0 + conjunto; // Diferente semilla para cada conjunto
            
            for (int i = 0; i < n; i++) {
                long raw = a * currentX + c;
                long entero = raw / m;
                long residuo = raw - (m * entero);
                residuo = ((residuo % m) + m) % m; 

                double pseudo = (double) residuo / (double) (m - 1);
                numeros.add(Math.round(pseudo * 100000.0) / 100000.0);
                
                currentX = residuo;
            }
            conjuntos[conjunto] = numeros;
        }
        return conjuntos;
    }
    
    /**
     * Método de Cuadrados Medios (TU VERSIÓN CORREGIDA)
     * Ahora devuelve un array con 2 listas de números
     */
    public static List<Double>[] generarCuadradosMedios(int n, int semilla, int numConjuntos) {
        @SuppressWarnings("unchecked")
        List<Double>[] conjuntos = new ArrayList[numConjuntos];
        
        for (int conjunto = 0; conjunto < numConjuntos; conjunto++) {
            List<Double> numeros = new ArrayList<>();
            long currentSemilla = semilla + conjunto * 1000; // Diferente semilla para cada conjunto
            int digitosAExtraer = 5; // REGLA: Siempre extraer 5 dígitos

            for (int i = 0; i < n; i++) {
                long cuadrado = currentSemilla * currentSemilla;
                String cuadradoStr = String.valueOf(cuadrado);

                // 1. Asegurar longitud mínima
                while (cuadradoStr.length() < digitosAExtraer) {
                    cuadradoStr = "0" + cuadradoStr;
                }

                // 2. Lógica de BALANCEO
                int diferencia = cuadradoStr.length() - digitosAExtraer;
                if (diferencia % 2 != 0) {
                    cuadradoStr = "0" + cuadradoStr;
                }

                // 3. Extraer los dígitos centrales
                int longitud = cuadradoStr.length();
                int inicio = (longitud - digitosAExtraer) / 2;
                String extraidoStr = cuadradoStr.substring(inicio, inicio + digitosAExtraer);

                // 4. Actualizar semilla y guardar pseudoaleatorio
                currentSemilla = Long.parseLong(extraidoStr);
                
                // Dividimos entre 100,000 para que quede 0.xxxxx
                double pseudo = currentSemilla / 100000.0; 
                numeros.add(Math.round(pseudo * 100000.0) / 100000.0);
            }
            conjuntos[conjunto] = numeros;
        }
        return conjuntos;
    }
    
    /**
     * Métodos originales para compatibilidad
     */
    public static List<Double> generarCongruencialMixto(int n, int x0, int a, int c, int m) {
        return generarCongruencialMixto(n, x0, a, c, m, 1)[0];
    }
    
    public static List<Double> generarCuadradosMedios(int n, int semilla) {
        return generarCuadradosMedios(n, semilla, 1)[0];
    }
    
    // ... (el resto del código se mantiene igual) ...
    /**
     * Valida los parámetros del método congruencial mixto
     */
    public static String validarParametrosMixto(int m, int a, int c, int x0) {
        if (m <= 0) return "m debe ser > 0";
        if (a <= 0) return "a debe ser > 0";
        if (c < 0) return "c debe ser ≥ 0";
        if (x0 < 0) return "X₀ debe ser ≥ 0";
        if (x0 >= m) return "X₀ debe ser < m";
        if (c >= m) return "c debe ser < m";
        if (a >= m) return "a debe ser < m";
        
        // Para tu método, podemos hacer validaciones más simples
        // Condición 1: MCD(c, m) = 1 (para período máximo)
        if (mcd(c, m) != 1) {
            return "c y m deben ser coprimos (MCD(c, m) = 1)";
        }
        
        // Condición 2: (a-1) divisible por factores primos de m
        List<Integer> factoresPrimos = obtenerFactoresPrimos(m);
        for (int p : factoresPrimos) {
            if ((a - 1) % p != 0) {
                return "a-1 debe ser divisible por todos los factores primos de m (factor " + p + ")";
            }
        }
        
        // Condición 3: Si m es múltiplo de 4
        if (m % 4 == 0 && (a - 1) % 4 != 0) {
            return "Si m es múltiplo de 4, entonces a-1 debe ser múltiplo de 4";
        }
        
        return "OK";
    }
    
    /**
     * Valida los parámetros del método de cuadrados medios
     */
    public static String validarParametrosCuadradosMedios(int semilla) {
        if (semilla <= 0) return "La semilla debe ser > 0";
        
        // Para tu método que siempre extrae 5 dígitos, necesitamos:
        // 1. La semilla debe tener al menos 3 dígitos para que el cuadrado tenga al menos 5 dígitos
        // 2. No hay requerimiento de paridad de dígitos ya que balanceas con ceros
        
        int numDigitos = String.valueOf(semilla).length();
        if (numDigitos < 3) return "La semilla debe tener al menos 3 dígitos";
        
        // Verificar que no cause desbordamiento (semilla^2 no exceda Long.MAX_VALUE)
        long cuadrado = (long) semilla * (long) semilla;
        if (cuadrado < 0) { // Esto ocurre si hay desbordamiento
            return "Semilla demasiado grande, puede causar desbordamiento";
        }
        
        return "OK";
    }
    
    /**
     * Calcula el Máximo Común Divisor (MCD)
     */
    public static int mcd(int a, int b) {
        if (a == 0 && b == 0) return 1;
        if (b == 0) return Math.abs(a);
        
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }
    
    /**
     * Obtiene los factores primos únicos de un número - VERSIÓN CORREGIDA Y SIMPLE
     */
    private static List<Integer> obtenerFactoresPrimos(int n) {
        List<Integer> factores = new ArrayList<>();
        
        if (n <= 1) {
            return factores;
        }
        
        int temp = n;
        
        // Factor 2
        if (temp % 2 == 0) {
            factores.add(2);
            while (temp % 2 == 0) {
                temp /= 2;
            }
        }
        
        // Factores impares
        int i = 3;
        while (i * i <= n) {
            if (temp % i == 0) {
                factores.add(i);
                while (temp % i == 0) {
                    temp /= i;
                }
            }
            i += 2;
        }
        
        // Si queda algo mayor que 1, es primo
        if (temp > 1) {
            factores.add(temp);
        }
        
        // DEBUG: Para ver qué factores se encontraron
        System.out.println("Factores primos de " + n + ": " + factores);
        
        return factores;
    }
}
