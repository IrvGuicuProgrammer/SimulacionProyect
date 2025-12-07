import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.geom.Path2D;

public class PanelGraficaComparativa extends JPanel {
    private List<Double> tiemposReales; // Acumulados
    private List<Double> tiemposSimulados; // Acumulados

    public PanelGraficaComparativa() {
        setBackground(new Color(30, 30, 40)); // Fondo oscuro por defecto
    }

    public void setDatos(List<Double> reales, List<Double> simulados) {
        this.tiemposReales = reales;
        this.tiemposSimulados = simulados;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo
        g2.setColor(getBackground()); 
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (tiemposReales == null || tiemposSimulados == null || tiemposReales.isEmpty()) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Inter", Font.PLAIN, 14));
            String msg = "Ejecute la simulación (con CSV cargado) para ver la gráfica.";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (getWidth() - fm.stringWidth(msg))/2, getHeight()/2);
            return;
        }

        int padding = 50;
        int w = getWidth() - (padding * 2);
        int h = getHeight() - (padding * 2);

        // Encontrar máximos para escalar
        double maxTiempoReal = tiemposReales.isEmpty() ? 0 : tiemposReales.get(tiemposReales.size() - 1);
        double maxTiempoSim = tiemposSimulados.isEmpty() ? 0 : tiemposSimulados.get(tiemposSimulados.size() - 1);
        double maxX = Math.max(maxTiempoReal, maxTiempoSim);
        if (maxX == 0) maxX = 1; // Evitar división por cero
        
        int maxY = Math.max(tiemposReales.size(), tiemposSimulados.size());

        // Dibujar Ejes
        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding); // Eje X (Tiempo)
        g2.drawLine(padding, padding, padding, getHeight() - padding); // Eje Y (Clientes)

        // Etiquetas Ejes
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("Tiempo (min)", getWidth() - padding - 40, getHeight() - padding + 15);
        g2.drawString("Clientes", padding - 10, padding - 10);

        // Dibujar Curva Real (ROJA)
        dibujarLinea(g2, tiemposReales, maxX, maxY, padding, w, h, new Color(239, 68, 68));

        // Dibujar Curva Simulada (AZUL/INDIGO)
        dibujarLinea(g2, tiemposSimulados, maxX, maxY, padding, w, h, new Color(99, 102, 241));

        // Leyenda
        g2.setFont(new Font("Inter", Font.BOLD, 12));
        g2.setColor(Color.WHITE);
        g2.drawString("Comparativa de Llegadas Acumuladas", padding + 20, padding + 20);
        
        g2.setFont(new Font("Inter", Font.PLAIN, 12));
        g2.setColor(new Color(239, 68, 68));
        g2.drawString("— Datos Reales (CSV)", padding + 20, padding + 40);
        
        g2.setColor(new Color(99, 102, 241));
        g2.drawString("— Datos Simulados", padding + 20, padding + 60);
    }

    private void dibujarLinea(Graphics2D g2, List<Double> tiempos, double maxValX, int maxValY, int pad, int w, int h, Color c) {
        if (tiempos == null || tiempos.isEmpty()) return;
        
        g2.setColor(c);
        g2.setStroke(new BasicStroke(2f));
        Path2D poly = new Path2D.Double();
        
        // Iniciar en (0,0) relativo a la gráfica
        poly.moveTo(pad, getHeight() - pad);

        for (int i = 0; i < tiempos.size(); i++) {
            double tiempoAcumulado = tiempos.get(i);
            
            // Escalar coordenadas
            // X = Tiempo acumulado
            double x = pad + (tiempoAcumulado / maxValX) * w;
            // Y = Número de cliente (i+1)
            double y = (getHeight() - pad) - ((double)(i + 1) / maxValY) * h;
            
            poly.lineTo(x, y);
        }
        g2.draw(poly);
    }
}