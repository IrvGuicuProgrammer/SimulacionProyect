import javax.swing.*;
import java.awt.*;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.List;

// Importaciones de JFreeChart (Asegúrate de tener la librería agregada)
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PanelGraficaComparativa extends JPanel {
    
    private XYSeries seriesReal;
    private XYSeries seriesSimulada;
    private ChartPanel chartPanel;
    private int horaInicioMinutos = 660; // Default 11:00 (11 * 60)

    public PanelGraficaComparativa() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 40));

        // 1. Inicializar series
        seriesReal = new XYSeries("Ocupación Real (CSV)");
        seriesSimulada = new XYSeries("Simulación Actual");
        
        // 2. Crear dataset
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesReal);
        dataset.addSeries(seriesSimulada);

        // 3. Crear gráfica
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Dinámica de la Fila (Hora Pico)", // Título
            "Hora del Día",                    // Eje X
            "Clientes en el Sistema",          // Eje Y
            dataset,                 
            PlotOrientation.VERTICAL,
            true, true, false                    
        );

        // 4. Personalizar estilo
        personalizarGrafica(chart);

        // 5. Panel
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        add(chartPanel, BorderLayout.CENTER);
    }

    public void setHoraInicio(int minutosDesdeMedianoche) {
        this.horaInicioMinutos = minutosDesdeMedianoche;
        if (chartPanel != null) {
            XYPlot plot = chartPanel.getChart().getXYPlot();
            NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
            // Actualizar el formateador del eje X
            domainAxis.setNumberFormatOverride(new FormatoHora(horaInicioMinutos));
        }
    }

    public void setDatos(List<double[]> real, List<double[]> simulado) {
        seriesReal.clear();
        seriesSimulada.clear();

        if (real != null) {
            for (double[] punto : real) seriesReal.add(punto[0], punto[1]);
        }
        if (simulado != null) {
            for (double[] punto : simulado) seriesSimulada.add(punto[0], punto[1]);
        }
    }

    private void personalizarGrafica(JFreeChart chart) {
        Color colorFondo = new Color(30, 30, 40);
        Color colorGrid = new Color(60, 60, 75);
        Color colorTexto = new Color(220, 220, 220);
        Color colorReal = new Color(239, 68, 68);    // Rojo
        Color colorSim = new Color(99, 102, 241);    // Indigo

        chart.setBackgroundPaint(colorFondo);
        chart.getTitle().setPaint(colorTexto);
        chart.getLegend().setBackgroundPaint(colorFondo);
        chart.getLegend().setItemPaint(colorTexto);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(colorFondo);
        plot.setDomainGridlinePaint(colorGrid);
        plot.setRangeGridlinePaint(colorGrid);
        plot.setOutlinePaint(null);

        // --- CONFIGURACIÓN EJE X (HORAS) ---
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setTickLabelPaint(colorTexto);
        domainAxis.setLabelPaint(colorTexto);
        domainAxis.setTickUnit(new NumberTickUnit(30)); // Intervalos de 30 minutos
        domainAxis.setNumberFormatOverride(new FormatoHora(horaInicioMinutos));

        // --- CONFIGURACIÓN EJE Y (CLIENTES) ---
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelPaint(colorTexto);
        rangeAxis.setLabelPaint(colorTexto);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // Solo enteros

        // --- RENDERER TIPO ESCALERA (XYStepRenderer) ---
        XYStepRenderer renderer = new XYStepRenderer();
        
        // Serie 0 (Real)
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesPaint(0, colorReal);
        
        // Serie 1 (Simulada)
        renderer.setSeriesShapesVisible(1, false);
        renderer.setSeriesStroke(1, new BasicStroke(2.5f));
        renderer.setSeriesPaint(1, colorSim);
        
        plot.setRenderer(renderer);
    }

    // Clase interna para convertir minutos acumulados (0, 30, 60...) a Horas (11:00, 11:30...)
    private static class FormatoHora extends NumberFormat {
        private int inicioMinutos;
        public FormatoHora(int inicio) { this.inicioMinutos = inicio; }

        @Override
        public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
            int totalMin = inicioMinutos + (int) number;
            int h = (totalMin / 60) % 24;
            int m = totalMin % 60;
            return toAppendTo.append(String.format("%02d:%02d", h, m));
        }
        @Override
        public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
            return format((double) number, toAppendTo, pos);
        }
        @Override
        public Number parse(String source, ParsePosition parsePosition) { return null; }
    }
}