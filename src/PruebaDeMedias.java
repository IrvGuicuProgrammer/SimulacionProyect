import java.util.ArrayList;

public class PruebaDeMedias {
    private double media, li, ls;
    private boolean acepta;
    private double z = 0;

    public void calcular(ArrayList<Double> numeros, double nivelConfianza) {
        int n = numeros.size();
        double suma = 0;
        for (double num : numeros) suma += num;
        media = suma / n;

        z = getZ(nivelConfianza);
        li = 0.5 - z * (1 / Math.sqrt(12 * n));
        ls = 0.5 + z * (1 / Math.sqrt(12 * n));

        acepta = (media >= li && media <= ls);
    }

    double getZ(double nc) {
        if (nc == 0.99) z = 2.575;
        else if (nc == 0.95) z = 1.96;
        else if (nc == 0.90) z = 1.645;
        else if (nc == 0.87) z = 1.51;
        else if (nc == 0.85) z = 1.28;
        else z = 1.96; // Default
        return z;
    }

    public String getResultado(double zCritico) {
        return String.format(
            "<html>" +
            "<b>Media:</b> %.5f<br>" +
            "Límite inferior: %.5f<br>" +
            "Límite superior: %.5f<br>" +
            "Z crítico: %.3f<br>" +
            "%s" +
            "</html>",
            media, li, ls,
            zCritico,
            (acepta ? "✅ Se acepta H₀" : "❌ Se rechaza H₀")
        );
    }

    // --- GETTERS AGREGADOS PARA LA INTEGRACIÓN ---
    public double getMedia() { return media; }
    public double getLi() { return li; }
    public double getLs() { return ls; }
    public boolean isAcepta() { return acepta; }
    // Este getter devuelve el Z usado, no el getter getZ(nc)
    public double getZ() { return z; }
}