import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PruebaForma {

    private double chiCalculada;
    private double chiCritico;
    private boolean acepta;

    // --- NUEVO MÉTODO PARA LÓGICA PURA ---
    public void calcular(List<Double> numeros, double nivelConfianza) {
        int[] fo = new int[10];
        double fe = numeros.size() / 10.0;

        for (double valor : numeros) {
            int indiceIntervalo = (int) (valor * 10);
            if (indiceIntervalo == 10) indiceIntervalo = 9;
            fo[indiceIntervalo]++;
        }

        chiCalculada = 0;
        for (int i = 0; i < 10; i++) {
            chiCalculada += Math.pow(fo[i] - fe, 2) / fe;
        }

        chiCritico = obtenerChiCritico(nivelConfianza);
        acepta = (chiCalculada <= chiCritico);
    }
    
    private double obtenerChiCritico(double nc) {
        if (nc >= 0.99) return 21.666;
        if (nc >= 0.95) return 16.919;
        if (nc >= 0.90) return 14.684;
        if (nc >= 0.87) return 13.985;
        if (nc >= 0.85) return 12.242;
        return 16.919; // Default 95%
    }

    // Getters para la ventana principal
    public double getChiCalculada() { return chiCalculada; }
    public double getChiCritico() { return chiCritico; }
    public boolean isAcepta() { return acepta; }

    // --- MANTENEMOS TU CÓDIGO VIEJO POR SI LO USAS EN OTRO LADO ---
    public JPanel crearPanelForma(List<Double> numeros, String ncStr) {
        // Llama a la lógica nueva para reutilizar código
        double nc = 0.95;
        if(ncStr.contains("99")) nc = 0.99; 
        else if(ncStr.contains("90")) nc = 0.90;
        
        calcular(numeros, nc); 
        
        // (El resto de tu código de JPanel sigue igual aquí, pero usando las variables de clase)
        JPanel panelResultados = new JPanel();
        panelResultados.setLayout(new BoxLayout(panelResultados, BoxLayout.Y_AXIS));
        JLabel lbl = new JLabel("Chi Calc: " + chiCalculada + " vs Tabla: " + chiCritico);
        panelResultados.add(lbl);
        return panelResultados;
    }
}