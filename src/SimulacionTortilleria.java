import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SimulacionTortilleria extends JFrame {

    // Paleta de colores
    private final Color COLOR_FONDO = new Color(15, 15, 20);
    private final Color COLOR_CARD = new Color(30, 30, 40);
    private final Color COLOR_PRIMARIO = new Color(99, 102, 241);
    private final Color COLOR_TEXTO = new Color(240, 240, 245);
    private final Color COLOR_TEXTO_SECUNDARIO = new Color(160, 165, 180);
    private final Color COLOR_BORDE = new Color(50, 55, 65);
    private final Color COLOR_HOVER = new Color(40, 45, 55);
    private final Color COLOR_VERDE = new Color(16, 185, 129);

    // Componentes Llegadas
    private JComboBox<String> cbDistLlegadas;
    private JPanel panelParamsLlegadas;
    private JTextField txtLlegP1, txtLlegP2;
    
    // Componentes Servicio
    private JComboBox<String> cbDistServicio;
    private JPanel panelParamsServicio;
    private JTextField txtSerP1, txtSerP2;

    private JTextField txtHoraInicio;
    private JButton btnCargarCSV; // Botón nuevo
    
    // Tablas y Gráficas
    private JTabbedPane tabbedPaneResultados;
    private JTable tablaSimulacion;
    private DefaultTableModel modeloTabla;
    private PanelGraficaComparativa panelGraficaComparativa; // Panel nuevo

    private DecimalFormat dfRi = new DecimalFormat("0.####");
    private DecimalFormat dfTime = new DecimalFormat("0.##");
    
    private double mediaLlegadasReal = 0.0; // Variable para almacenar la media calculada

    public SimulacionTortilleria() {
        setTitle("Simulación Tabular - Tortillería La Providencia");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);

        if (!SimulacionDatos.getInstancia().hayDatos()) {
            JOptionPane.showMessageDialog(this,
                "¡Alerta!\nNo se han detectado números pseudoaleatorios generados.\n" +
                "Por favor, vaya al módulo 'Generación y Pruebas' primero.",
                "Faltan Datos", JOptionPane.WARNING_MESSAGE);
        }

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);

        // Inicializar paneles de parámetros (labels temporales)
        // Se necesitan labels dummies para inicializar el método, aunque no los usemos como variables de clase
        JLabel l1 = new JLabel(), l2 = new JLabel(), l3 = new JLabel(), l4 = new JLabel();
        actualizarInputsParams(cbDistLlegadas, panelParamsLlegadas, l1, txtLlegP1, l2, txtLlegP2);
        actualizarInputsParams(cbDistServicio, panelParamsServicio, l3, txtSerP1, l4, txtSerP2);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Simulación del Flujo de Clientes");
        titulo.setFont(new Font("Inter", Font.BOLD, 22));
        titulo.setForeground(COLOR_TEXTO);
        panel.add(titulo, BorderLayout.WEST);

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelControles.setBackground(COLOR_FONDO);
        
        // Botón Cargar CSV
        btnCargarCSV = crearBoton("Cargar CSV Reales", COLOR_PRIMARIO, true);
        btnCargarCSV.setPreferredSize(new Dimension(180, 35));
        btnCargarCSV.addActionListener(e -> cargarDatosCSV());
        
        JLabel lblHora = new JLabel("Hora Inicio (HH:MM): ");
        lblHora.setFont(new Font("Inter", Font.BOLD, 14));
        lblHora.setForeground(COLOR_TEXTO_SECUNDARIO);
        txtHoraInicio = crearInput("11:00");
        txtHoraInicio.setPreferredSize(new Dimension(80, 35));
        
        panelControles.add(btnCargarCSV);
        panelControles.add(lblHora);
        panelControles.add(txtHoraInicio);
        
        panel.add(panelControles, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 15, 15);

        // Panel Configuración Llegadas
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; gbc.weighty = 0.0;
        panel.add(crearPanelConfigDistribucion("Tiempos entre Llegadas (Conjunto 1)", true), gbc);

        // Panel Configuración Servicio
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.3;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(crearPanelConfigDistribucion("Tiempos de Servicio (Conjunto 2)", false), gbc);

        // Botón Ejecutar
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.fill = GridBagConstraints.NONE;
        JButton btnSimular = crearBoton("Ejecutar Simulación", COLOR_VERDE, true);
        btnSimular.setPreferredSize(new Dimension(300, 45));
        btnSimular.addActionListener(e -> ejecutarSimulación());
        panel.add(btnSimular, gbc);

        // TabbedPane con Tabla y Gráfica (Sustituye al panel tabla directo)
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        
        tabbedPaneResultados = new JTabbedPane();
        tabbedPaneResultados.addTab("Tabla de Simulación", crearPanelTabla());
        tabbedPaneResultados.addTab("Gráfica Comparativa (Real vs Simulado)", crearPanelGrafica());
        
        panel.add(tabbedPaneResultados, gbc);

        return panel;
    }

    private JPanel crearPanelConfigDistribucion(String titulo, boolean esLlegadas) {
        JPanel card = new JPanel(new BorderLayout());
        estilizarCard(card);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_BORDE), titulo);
        border.setTitleColor(COLOR_TEXTO);
        border.setTitleFont(new Font("Inter", Font.BOLD, 14));
        card.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 15, 15, 15)));

        JComboBox<String> combo = new JComboBox<>(new String[]{"Exponencial", "Uniforme", "Normal"});
        estilizarCombo(combo);
        
        JPanel panelParams = new JPanel();
        panelParams.setLayout(new BoxLayout(panelParams, BoxLayout.Y_AXIS));
        panelParams.setBackground(COLOR_CARD);

        // Labels internos para pasar al listener (aunque no se guarden en clase)
        JLabel lblP1 = crearLabel("P1"); 
        JLabel lblP2 = crearLabel("P2"); 
        JTextField txtP1 = crearInput("");
        JTextField txtP2 = crearInput("");

        if (esLlegadas) {
            cbDistLlegadas = combo; panelParamsLlegadas = panelParams;
            txtLlegP1 = txtP1; txtLlegP2 = txtP2;
        } else {
            cbDistServicio = combo; panelParamsServicio = panelParams;
            txtSerP1 = txtP1; txtSerP2 = txtP2;
        }

        combo.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED)
                actualizarInputsParams(combo, panelParams, lblP1, txtP1, lblP2, txtP2);
        });

        card.add(combo, BorderLayout.NORTH);
        card.add(panelParams, BorderLayout.CENTER);
        return card;
    }

    private void actualizarInputsParams(JComboBox<String> combo, JPanel panel, JLabel lbl1, JTextField txt1, JLabel lbl2, JTextField txt2) {
        panel.removeAll();
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        String seleccion = (String) combo.getSelectedItem();

        if ("Exponencial".equals(seleccion)) {
            agregarCampo(panel, lbl1, txt1, "Media (μ):", "15");
        } else if ("Uniforme".equals(seleccion)) {
            agregarCampo(panel, lbl1, txt1, "Mínimo (a):", "10");
            agregarCampo(panel, lbl2, txt2, "Máximo (b):", "25");
        } else if ("Normal".equals(seleccion)) {
            agregarCampo(panel, lbl1, txt1, "Media (μ):", "20");
            agregarCampo(panel, lbl2, txt2, "Desv. Std (σ):", "5");
        }
        panel.revalidate(); panel.repaint();
    }

    private void agregarCampo(JPanel panel, JLabel lbl, JTextField txt, String texto, String def) {
        lbl.setText(texto);
        // Si el texto está vacío (recién creado), poner default. Si no, conservar valor (útil al cambiar pestañas o cargar csv)
        if(txt.getText().isEmpty()) txt.setText(def);
        panel.add(lbl); panel.add(txt);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JPanel crearPanelTabla() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD); // Sin borde extra
        
        String[] columnas = {
            "# Cliente", "# Aleat (Lleg)", "T. Lleg", "H. Lleg",
            "# Aleat (Ser)", "T. Ser",
            "Ini Serv", "Fin Serv", "Esp (Cola)", "I. Cola", "Ocio (Serv)"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaSimulacion = new JTable(modeloTabla);
        estilizarTabla(tablaSimulacion);
        tablaSimulacion.getColumnModel().getColumn(0).setPreferredWidth(70);
        tablaSimulacion.getColumnModel().getColumn(9).setPreferredWidth(60);

        JScrollPane scroll = new JScrollPane(tablaSimulacion);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COLOR_CARD);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }
    
    private JPanel crearPanelGrafica() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        panelGraficaComparativa = new PanelGraficaComparativa();
        card.add(panelGraficaComparativa, BorderLayout.CENTER);
        return card;
    }
    
    // --- LÓGICA DE CARGA CSV ---
    private void cargarDatosCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            List<Double> tiemposReales = new ArrayList<>();
            double suma = 0;
            
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    try {
                        // Asumimos 1 columna con tiempos entre llegadas
                        double valor = Double.parseDouble(linea.trim().replace(",", "."));
                        tiemposReales.add(valor);
                        suma += valor;
                    } catch (NumberFormatException ex) { /* Ignorar encabezados */ }
                }
                
                if (!tiemposReales.isEmpty()) {
                    SimulacionDatos.getInstancia().setDatosRealesLlegadas(tiemposReales);
                    
                    // CÁLCULO DE MEDIA REAL
                    mediaLlegadasReal = suma / tiemposReales.size();
                    
                    // Actualizar interfaz automáticamente
                    cbDistLlegadas.setSelectedItem("Exponencial"); // Modelo usual para llegadas
                    txtLlegP1.setText(String.format("%.4f", mediaLlegadasReal).replace(",", "."));
                    
                    JOptionPane.showMessageDialog(this, 
                        "Datos cargados: " + tiemposReales.size() + "\n" +
                        "Media calculada: " + String.format("%.4f", mediaLlegadasReal) + "\n\n" +
                        "La distribución de llegadas se ha configurado automáticamente\n" +
                        "con la media de los datos reales.", 
                        "Carga Exitosa", JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al leer archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- NÚCLEO DE LA SIMULACIÓN ---
    private void ejecutarSimulación() {
        SimulacionDatos datos = SimulacionDatos.getInstancia();
        if (!datos.hayDatos()) {
            JOptionPane.showMessageDialog(this, "No hay datos de Ri generados. Vuelva a la etapa 1.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        modeloTabla.setRowCount(0);
        List<Double> riLlegadas = datos.getConjunto1RiEn();
        List<Double> riServicio = datos.getConjunto2RiSn();
        int N = datos.getNGenerados();
        
        // Listas para la gráfica
        List<Double> simuladosAcumulados = new ArrayList<>();

        try {
            // 1. Obtener parámetros desde la interfaz (que ya tiene la media real si se cargó CSV)
            // Se usa el valor ACTUAL de los text fields, permitiendo ajuste manual post-carga
            Parametros paramsLleg = obtenerParametros(cbDistLlegadas, txtLlegP1, txtLlegP2);
            Parametros paramsSer = obtenerParametros(cbDistServicio, txtSerP1, txtSerP2);
            int horaBaseMinutos = parsearHoraInicio(txtHoraInicio.getText());

            double finServicioAnterior = horaBaseMinutos;
            double hLlegadaAcumulada = horaBaseMinutos;

            // 2. Bucle principal
            for (int i = 0; i < N; i++) {
                // A. Llegadas (Transformada Inversa aplicada aquí)
                double rLleg = riLlegadas.get(i);
                double tLleg = calcularVariable(rLleg, paramsLleg); 
                hLlegadaAcumulada += tLleg;
                
                // Guardar acumulado (restando hora base para iniciar en 0)
                simuladosAcumulados.add(hLlegadaAcumulada - horaBaseMinutos);

                // B. Servicio
                double rSer = riServicio.get(i);
                double tSer = calcularVariable(rSer, paramsSer);

                // C. Lógica Colas
                double iniServ = Math.max(hLlegadaAcumulada, finServicioAnterior);
                double finServ = iniServ + tSer;
                double espera = iniServ - hLlegadaAcumulada;
                int indicadorCola = (espera > 0.01) ? 1 : 0;
                double ocio = iniServ - finServicioAnterior;

                modeloTabla.addRow(new Object[]{
                    i + 1, dfRi.format(rLleg), dfTime.format(tLleg), minutosAHoraStr(hLlegadaAcumulada),
                    dfRi.format(rSer), dfTime.format(tSer),
                    minutosAHoraStr(iniServ), minutosAHoraStr(finServ), dfTime.format(espera), indicadorCola, dfTime.format(ocio)
                });

                finServicioAnterior = finServ;
            }
            
            // 3. Actualizar Gráfica
            List<Double> realesRaw = datos.getDatosRealesLlegadas();
            List<Double> realesAcumulados = new ArrayList<>();
            if(!realesRaw.isEmpty()) {
                double acc = 0;
                for(Double val : realesRaw) {
                    acc += val;
                    realesAcumulados.add(acc);
                }
                // Si hay muchos más simulados que reales, la gráfica se ajusta en el panel
                panelGraficaComparativa.setDatos(realesAcumulados, simuladosAcumulados);
            } else {
                // Si no hay reales, solo mandamos simulados (la gráfica manejará el null)
                panelGraficaComparativa.setDatos(new ArrayList<>(), simuladosAcumulados);
            }
            
            // Auto-cambiar a la pestaña de gráfica si hay datos reales para comparar
            if(!realesAcumulados.isEmpty()) {
                tabbedPaneResultados.setSelectedIndex(1); 
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage() + "\nVerifique parámetros numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // --- Helpers ---
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        panel.setBackground(COLOR_FONDO);
        JButton btnVolver = crearBoton("Volver al Menú", COLOR_TEXTO_SECUNDARIO, false);
        btnVolver.addActionListener(e -> { new MenuPrincipal().setVisible(true); dispose(); });
        panel.add(btnVolver);
        return panel;
    }

    private double calcularVariable(double ri, Parametros p) {
        SimulacionDatos math = SimulacionDatos.getInstancia();
        switch (p.tipo) {
            case "Exponencial": return math.calcularExponencial(ri, p.p1);
            case "Uniforme":    return math.calcularUniforme(ri, p.p1, p.p2);
            case "Normal":      return math.calcularNormal(ri, p.p1, p.p2); 
            default: return 0;
        }
    }

    private static class Parametros {
        String tipo; double p1, p2;
        Parametros(String t, double pa1, double pa2) { tipo = t; p1 = pa1; p2 = pa2; }
    }

    private Parametros obtenerParametros(JComboBox<String> cb, JTextField t1, JTextField t2) {
        String tipo = (String) cb.getSelectedItem();
        double p1 = 0, p2 = 0;
        try { if(!t1.getText().isEmpty()) p1 = Double.parseDouble(t1.getText()); } catch(Exception e){}
        try { if(t2.isShowing() && !t2.getText().isEmpty()) p2 = Double.parseDouble(t2.getText()); } catch(Exception e){}
        return new Parametros(tipo, p1, p2);
    }

    private int parsearHoraInicio(String horaStr) throws Exception {
        String[] partes = horaStr.split(":");
        if (partes.length != 2) throw new Exception("Formato inválido");
        return Integer.parseInt(partes[0]) * 60 + Integer.parseInt(partes[1]);
    }

    private String minutosAHoraStr(double minutosTotales) {
        int totalMin = (int) Math.round(minutosTotales);
        int hh = (totalMin / 60) % 24;
        int mm = totalMin % 60;
        return String.format("%02d:%02d", hh, mm);
    }

    // Estilos UI
    private void estilizarCard(JPanel p) { p.setBackground(COLOR_CARD); p.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1)); }
    private JLabel crearLabel(String t) { JLabel l = new JLabel(t); l.setFont(new Font("Inter", Font.BOLD, 12)); l.setForeground(COLOR_TEXTO_SECUNDARIO); return l; }
    private JTextField crearInput(String t) {
        JTextField x = new JTextField(t); x.setFont(new Font("Inter", Font.PLAIN, 14));
        x.setBackground(COLOR_HOVER); x.setForeground(COLOR_TEXTO); x.setCaretColor(COLOR_TEXTO);
        x.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        x.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); return x;
    }
    private void estilizarCombo(JComboBox<String> c) {
        c.setFont(new Font("Inter", Font.PLAIN, 14)); c.setBackground(COLOR_HOVER); c.setForeground(Color.WHITE);
        c.setBorder(BorderFactory.createLineBorder(COLOR_BORDE)); c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }
    private void estilizarTabla(JTable t) {
        t.setFont(new Font("Inter", Font.PLAIN, 13)); t.setBackground(COLOR_CARD); t.setForeground(COLOR_TEXTO); t.setGridColor(COLOR_BORDE);
        t.setRowHeight(28); t.setShowVerticalLines(true); t.setShowHorizontalLines(true);
        t.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12)); t.getTableHeader().setBackground(COLOR_HOVER);
        t.getTableHeader().setForeground(COLOR_TEXTO);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));
        DefaultTableCellRenderer r = new DefaultTableCellRenderer(); r.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<t.getColumnCount(); i++) t.getColumnModel().getColumn(i).setCellRenderer(r);
    }
    private JButton crearBoton(String t, Color c, boolean r) {
        JButton b = new JButton(t) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if(getModel().isPressed()) g2.setColor(c.darker()); else if(getModel().isRollover()) g2.setColor(r?c.brighter():COLOR_HOVER); else g2.setColor(r?c:COLOR_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                if(!r){ g2.setColor(c); g2.setStroke(new BasicStroke(1.5f)); g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,8,8); }
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(new Font("Inter", Font.BOLD, 14)); b.setForeground(r?Color.WHITE:c); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}