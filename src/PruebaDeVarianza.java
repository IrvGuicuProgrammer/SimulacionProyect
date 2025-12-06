import java.util.ArrayList;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

public class PruebaDeVarianza {
    private double varianza;
    private double li; // Límite inferior
    private double ls; // Límite superior
    private boolean acepta;
    private double chiIzquierda, chiDerecha;

    public void calcular(ArrayList<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        double media = 0;

        // Calcular la media
        for (double num : numeros)
            media += num;
        media /= n;

        // Calcular la varianza muestral
        double suma = 0;
        for (double num : numeros)
            suma += Math.pow(num - media, 2);
        varianza = suma / (n - 1);

        // Grados de libertad
        int gl = n - 1;

        // Calcular chi² izquierda y derecha con la librería Apache Commons Math
        double alpha = 1 - nivelConfianza;
        ChiSquaredDistribution chi = new ChiSquaredDistribution(gl);

        chiIzquierda = chi.inverseCumulativeProbability(alpha / 2);
        chiDerecha = chi.inverseCumulativeProbability(1 - alpha / 2);

        // Cálculo de límites inferior y superior de aceptación
        li = chiIzquierda / (12 * (n - 1));
        ls = chiDerecha / (12 * (n - 1));

        // Evaluar hipótesis H₀: V(x) = 1/12
        acepta = (varianza >= li && varianza <= ls);
    }

    public void calcularChiCriticos(int n, double nivelConfianza) {
        int gl = n - 1;
        double alpha = 1 - nivelConfianza;
        ChiSquaredDistribution chi = new ChiSquaredDistribution(gl);

        chiIzquierda = chi.inverseCumulativeProbability(alpha / 2);
        chiDerecha = chi.inverseCumulativeProbability(1 - alpha / 2);
    }

    public String getResultado() {
        return String.format(
            "<html><center>" +
            "<b>Varianza calculada:</b> %.5f<br>" +
            "<b>Chi² izquierda:</b> %.5f<br>" +
            "<b>Chi² derecha:</b> %.5f<br>" +
            "<b>Límite inferior:</b> %.5f<br>" +
            "<b>Límite superior:</b> %.5f<br>" +
            "%s" +
            "</center></html>",
            varianza, chiIzquierda, chiDerecha, li, ls,
            (acepta ? "✅ Se acepta H₀: V(x) = 1/12" : "❌ Se rechaza H₀: V(x) ≠ 1/12")
        );
    }

    public boolean isAcepta() { return acepta; }
    
    // --- GETTERS AGREGADOS PARA LA INTEGRACIÓN ---
    public double getVarianza() { return varianza; }
    public double getLi() { return li; }
    public double getLs() { return ls; }
}