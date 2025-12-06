import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.text.DecimalFormat;
import java.util.List;

public class SimulacionTortilleria extends JFrame {

    // Paleta de colores (Consistente)
    private final Color COLOR_FONDO = new Color(15, 15, 20);
    private final Color COLOR_CARD = new Color(30, 30, 40);
    private final Color COLOR_PRIMARIO = new Color(99, 102, 241); // Indigo
    private final Color COLOR_TEXTO = new Color(240, 240, 245);
    private final Color COLOR_TEXTO_SECUNDARIO = new Color(160, 165, 180);
    private final Color COLOR_BORDE = new Color(50, 55, 65);
    private final Color COLOR_HOVER = new Color(40, 45, 55);
    private final Color COLOR_VERDE = new Color(16, 185, 129);

    // Componentes de Entrada Llegadas
    private JComboBox<String> cbDistLlegadas;
    private JPanel panelParamsLlegadas;
    private JTextField txtLlegP1, txtLlegP2;
    private JLabel lblLlegP1, lblLlegP2;

    // Componentes de Entrada Servicio
    private JComboBox<String> cbDistServicio;
    private JPanel panelParamsServicio;
    private JTextField txtSerP1, txtSerP2;
    private JLabel lblSerP1, lblSerP2;

    private JTextField txtHoraInicio;
    private JTable tablaSimulacion;
    private DefaultTableModel modeloTabla;
    private DecimalFormat dfRi = new DecimalFormat("0.####");
    private DecimalFormat dfTime = new DecimalFormat("0.##");

    public SimulacionTortilleria() {
        setTitle("Simulación Tabular - Tortillería La Providencia");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);

        // Verificar si hay datos
        if (!SimulacionDatos.getInstancia().hayDatos()) {
            JOptionPane.showMessageDialog(this,
                "¡Alerta!\nNo se han detectado números pseudoaleatorios generados.\n" +
                "Por favor, vaya al módulo 'Generación y Pruebas' primero.",
                "Faltan Datos", JOptionPane.WARNING_MESSAGE);
        }

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);

        // Inicializar paneles de parámetros
        actualizarInputsParams(cbDistLlegadas, panelParamsLlegadas, lblLlegP1, txtLlegP1, lblLlegP2, txtLlegP2);
        actualizarInputsParams(cbDistServicio, panelParamsServicio, lblSerP1, txtSerP1, lblSerP2, txtSerP2);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Simulación del Flujo de Clientes");
        titulo.setFont(new Font("Inter", Font.BOLD, 22));
        titulo.setForeground(COLOR_TEXTO);
        panel.add(titulo, BorderLayout.WEST);

        JPanel panelHora = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelHora.setBackground(COLOR_FONDO);
        JLabel lblHora = new JLabel("Hora Inicio Simulación (HH:MM): ");
        lblHora.setFont(new Font("Inter", Font.BOLD, 14));
        lblHora.setForeground(COLOR_TEXTO_SECUNDARIO);
        txtHoraInicio = crearInput("11:00");
        txtHoraInicio.setPreferredSize(new Dimension(80, 35));
        panelHora.add(lblHora);
        panelHora.add(txtHoraInicio);
        panel.add(panelHora, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 15, 15);

        // Panel Configuración Llegadas (Izquierda Superior)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; gbc.weighty = 0.0;
        panel.add(crearPanelConfigDistribucion("Tiempos entre Llegadas (Conjunto Ri 1)", true), gbc);

        // Panel Configuración Servicio (Derecha Superior)
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.3;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(crearPanelConfigDistribucion("Tiempos de Servicio (Conjunto Ri 2)", false), gbc);

        // Botón Ejecutar (Centro Medio)
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.fill = GridBagConstraints.NONE;
        JButton btnSimular = crearBoton("Ejecutar Simulación Tabular", COLOR_VERDE, true);
        btnSimular.setPreferredSize(new Dimension(300, 45));
        btnSimular.addActionListener(e -> ejecutarSimulación());
        panel.add(btnSimular, gbc);

        // Panel Tabla (Inferior abarcando todo)
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(crearPanelTabla(), gbc);

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

        JLabel lblP1 = crearLabel("P1"); JTextField txtP1 = crearInput("");
        JLabel lblP2 = crearLabel("P2"); JTextField txtP2 = crearInput("");

        if (esLlegadas) {
            cbDistLlegadas = combo; panelParamsLlegadas = panelParams;
            lblLlegP1 = lblP1; txtLlegP1 = txtP1;
            lblLlegP2 = lblP2; txtLlegP2 = txtP2;
        } else {
            cbDistServicio = combo; panelParamsServicio = panelParams;
            lblSerP1 = lblP1; txtSerP1 = txtP1;
            lblSerP2 = lblP2; txtSerP2 = txtP2;
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
        lbl.setText(texto); txt.setText(def);
        panel.add(lbl); panel.add(txt);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JPanel crearPanelTabla() {
        JPanel card = new JPanel(new BorderLayout());
        estilizarCard(card);
        
        // Columnas basadas exactamente en la imagen proporcionada
        String[] columnas = {
            "# Cliente", "# Aleat (Lleg)", "T. Lleg", "H. Lleg",
            "# Aleat (Ser)", "T. Ser",
            "Ini Serv", "Fin Serv", "Esp (Cola)", "I. Cola", "Ocio (Serv)"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                if(columnIndex == 0 || columnIndex == 9) return Integer.class; // Cliente e Indice cola
                return String.class; // El resto como strings formateados
            }
        };

        tablaSimulacion = new JTable(modeloTabla);
        estilizarTabla(tablaSimulacion);
        
        // Ajustar anchos de columna específicos
        tablaSimulacion.getColumnModel().getColumn(0).setPreferredWidth(70); // # Cliente
        tablaSimulacion.getColumnModel().getColumn(9).setPreferredWidth(60); // I. Cola

        JScrollPane scroll = new JScrollPane(tablaSimulacion);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COLOR_CARD);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        panel.setBackground(COLOR_FONDO);
        JButton btnVolver = crearBoton("Volver al Menú", COLOR_TEXTO_SECUNDARIO, false);
        btnVolver.addActionListener(e -> { new MenuPrincipal().setVisible(true); dispose(); });
        panel.add(btnVolver);
        return panel;
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

        try {
            // 1. Parsear parámetros de entrada y hora inicial
            Parametros paramsLleg = obtenerParametros(cbDistLlegadas, txtLlegP1, txtLlegP2);
            Parametros paramsSer = obtenerParametros(cbDistServicio, txtSerP1, txtSerP2);
            int horaBaseMinutos = parsearHoraInicio(txtHoraInicio.getText());

            // 2. Variables de estado de la simulación
            double finServicioAnterior = horaBaseMinutos;
            double hLlegadaAcumulada = horaBaseMinutos;

            // 3. Bucle principal de simulación
            for (int i = 0; i < N; i++) {
                // A. Llegadas
                double rLleg = riLlegadas.get(i);
                double tLleg = calcularVariable(rLleg, paramsLleg);
                hLlegadaAcumulada += tLleg;

                // B. Servicio
                double rSer = riServicio.get(i);
                double tSer = calcularVariable(rSer, paramsSer);

                // C. Lógica de Colas
                // El servicio inicia cuando llega el cliente O cuando el servidor se libera, lo que ocurra último.
                double iniServ = Math.max(hLlegadaAcumulada, finServicioAnterior);
                double finServ = iniServ + tSer;
                double espera = iniServ - hLlegadaAcumulada;
                int indicadorCola = (espera > 0.01) ? 1 : 0; // 1 si tuvo que esperar
                // El ocio es la diferencia entre cuando inicia este servicio y cuando terminó el anterior
                double ocio = iniServ - finServicioAnterior;

                // 4. Formateo y agregar a tabla
                modeloTabla.addRow(new Object[]{
                    i + 1, // # Cliente
                    dfRi.format(rLleg),
                    dfTime.format(tLleg),
                    minutosAHoraStr(hLlegadaAcumulada), // H. Lleg formateada
                    dfRi.format(rSer),
                    dfTime.format(tSer),
                    minutosAHoraStr(iniServ), // Ini Serv formateada
                    minutosAHoraStr(finServ), // Fin Serv formateada
                    dfTime.format(espera),
                    indicadorCola,
                    dfTime.format(ocio)
                });

                // Actualizar estado para el siguiente cliente
                finServicioAnterior = finServ;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en la simulación: " + e.getMessage() + 
                "\nVerifique los parámetros numéricos y el formato de hora (HH:MM).", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // --- Helpers Matemáticos y de Formato ---
    
    private double calcularVariable(double ri, Parametros p) throws Exception {
        SimulacionDatos math = SimulacionDatos.getInstancia();
        switch (p.tipo) {
            case "Exponencial": return math.calcularExponencial(ri, p.p1);
            case "Uniforme":    return math.calcularUniforme(ri, p.p1, p.p2);
            // Nota: Para Normal en tabla estricta se necesitarían 2 Ri. Usamos la versión simplificada con auxiliar interno.
            case "Normal":      return math.calcularNormal(ri, p.p1, p.p2); 
            default: return 0;
        }
    }

    private static class Parametros {
        String tipo; double p1, p2;
        Parametros(String t, double pa1, double pa2) { tipo = t; p1 = pa1; p2 = pa2; }
    }

    private Parametros obtenerParametros(JComboBox<String> cb, JTextField t1, JTextField t2) throws NumberFormatException {
        String tipo = (String) cb.getSelectedItem();
        double p1 = Double.parseDouble(t1.getText());
        double p2 = t2.isShowing() ? Double.parseDouble(t2.getText()) : 0;
        return new Parametros(tipo, p1, p2);
    }

    private int parsearHoraInicio(String horaStr) throws Exception {
        String[] partes = horaStr.split(":");
        if (partes.length != 2) throw new Exception("Formato de hora inválido");
        int hh = Integer.parseInt(partes[0]);
        int mm = Integer.parseInt(partes[1]);
        return hh * 60 + mm; // Retorna minutos totales desde media noche
    }

    private String minutosAHoraStr(double minutosTotales) {
        int totalMin = (int) Math.round(minutosTotales);
        int hh = (totalMin / 60) % 24; // Maneja cambio de día si excede 24h
        int mm = totalMin % 60;
        return String.format("%02d:%02d", hh, mm);
    }

    // --- Estilos UI (Reutilizados para consistencia) ---
    private void estilizarCard(JPanel panel) {
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
    }
    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Inter", Font.BOLD, 12));
        lbl.setForeground(COLOR_TEXTO_SECUNDARIO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT); return lbl;
    }
    private JTextField crearInput(String texto) {
        JTextField txt = new JTextField(texto);
        txt.setFont(new Font("Inter", Font.PLAIN, 14));
        txt.setBackground(COLOR_HOVER);
        txt.setForeground(COLOR_TEXTO);
        txt.setCaretColor(COLOR_TEXTO);
        txt.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); txt.setAlignmentX(Component.LEFT_ALIGNMENT); return txt;
    }
    private void estilizarCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Inter", Font.PLAIN, 14));
        combo.setBackground(COLOR_HOVER);
        combo.setForeground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); combo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
     private void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Inter", Font.PLAIN, 13));
        tabla.setBackground(COLOR_CARD);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setGridColor(COLOR_BORDE);
        tabla.setRowHeight(28);
        tabla.setShowVerticalLines(true); tabla.setShowHorizontalLines(true);
        tabla.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(COLOR_HOVER);
        tabla.getTableHeader().setForeground(COLOR_TEXTO);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }
    private JButton crearBoton(String texto, Color colorBase, boolean relleno) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(colorBase.darker());
                else if (getModel().isRollover()) g2.setColor(relleno ? colorBase.brighter() : COLOR_HOVER);
                else g2.setColor(relleno ? colorBase : COLOR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                if (!relleno) { g2.setColor(colorBase); g2.setStroke(new BasicStroke(1.5f)); g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8); }
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Inter", Font.BOLD, 14));
        btn.setForeground(relleno ? Color.WHITE : colorBase);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}