import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TransformadasInversas extends JFrame {

    // --- Paleta de Colores ---
    private final Color COLOR_FONDO = new Color(15, 15, 20);
    private final Color COLOR_CARD = new Color(30, 30, 40);
    private final Color COLOR_PRIMARIO = new Color(99, 102, 241); // Indigo
    private final Color COLOR_TEXTO = new Color(240, 240, 245);
    private final Color COLOR_TEXTO_SECUNDARIO = new Color(160, 165, 180);
    private final Color COLOR_BORDE = new Color(50, 55, 65);
    private final Color COLOR_HOVER = new Color(40, 45, 55);
    
    // Componentes
    private JLabel lblCantidadDatos; 
    private DefaultTableModel modeloTabla1; // Llegadas
    private DefaultTableModel modeloTabla2; // Servicio
    
    // Datos calculados (opcional si se quieren guardar)
    private List<Double> transformadosLlegadas;
    private List<Double> transformadosServicio;

    public TransformadasInversas() {
        setTitle("Transformadas Inversas - Generación de Variables Aleatorias");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);

        // Validar datos previos
        if (!SimulacionDatos.getInstancia().hayDatos()) {
            JOptionPane.showMessageDialog(this, 
                "¡No hay datos generados!\nPor favor ve al módulo anterior primero.", 
                "Sin Datos", JOptionPane.WARNING_MESSAGE);
        }

        transformadosLlegadas = new ArrayList<>();
        transformadosServicio = new ArrayList<>();

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
        
        actualizarContadorDatos(); 
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel titulo = new JLabel("Método de la Transformada Inversa");
        titulo.setFont(new Font("Inter", Font.BOLD, 24));
        titulo.setForeground(COLOR_TEXTO);

        JLabel subtitulo = new JLabel("Simulación automática de Tiempos de Llegada y Servicio basada en Ri");
        subtitulo.setFont(new Font("Inter", Font.PLAIN, 14));
        subtitulo.setForeground(COLOR_TEXTO_SECUNDARIO);

        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setBackground(COLOR_FONDO);
        panelTextos.add(titulo);
        panelTextos.add(subtitulo);

        panel.add(panelTextos, BorderLayout.WEST);
        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 20);

        // Panel de Configuración (Izquierda - más estrecho)
        gbc.weightx = 0.20; 
        gbc.weighty = 1.0; 
        gbc.gridx = 0;
        panel.add(crearPanelConfiguracion(), gbc);

        // Panel de Tablas (Derecha - ocupa el resto)
        gbc.weightx = 0.80; 
        gbc.insets = new Insets(0, 0, 0, 0); 
        gbc.gridx = 1;
        panel.add(crearPanelTablas(), gbc);

        return panel;
    }

    private JPanel crearPanelConfiguracion() {
        JPanel card = new JPanel(new BorderLayout());
        estilizarCard(card);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(COLOR_CARD);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Indicador de datos
        contenido.add(crearLabel("Estado de Datos:"));
        contenido.add(Box.createRigidArea(new Dimension(0, 10)));
        
        lblCantidadDatos = new JLabel("Cargando...");
        lblCantidadDatos.setFont(new Font("Inter", Font.ITALIC, 13));
        lblCantidadDatos.setForeground(COLOR_PRIMARIO); 
        contenido.add(lblCantidadDatos);
        contenido.add(Box.createRigidArea(new Dimension(0, 30)));

        // Información
        JLabel lblInfo = new JLabel("<html><body>Se aplicarán distribuciones empíricas a ambos conjuntos:<br><br>" +
                                    "• <b>Conjunto 1:</b> Tiempos entre Llegadas<br>" +
                                    "• <b>Conjunto 2:</b> Tiempos de Servicio</body></html>");
        lblInfo.setFont(new Font("Inter", Font.PLAIN, 13));
        lblInfo.setForeground(COLOR_TEXTO_SECUNDARIO);
        contenido.add(lblInfo);
        
        contenido.add(Box.createVerticalGlue());

        // Botón
        JButton btnGenerar = crearBoton("Transformar Todo", COLOR_PRIMARIO, true);
        btnGenerar.addActionListener(e -> generarVariables());
        
        card.add(contenido, BorderLayout.CENTER);
        card.add(btnGenerar, BorderLayout.SOUTH);

        return card;
    }
    
    private void actualizarContadorDatos() {
        if (!SimulacionDatos.getInstancia().hayDatos()) {
            lblCantidadDatos.setText("Sin datos Ri (N=0)");
            return;
        }
        int n = SimulacionDatos.getInstancia().getNGenerados();
        lblCantidadDatos.setText("✓ Datos Ri disponibles: " + n);
    }

    // --- PANEL DOBLE TABLA ---
    private JPanel crearPanelTablas() {
        // Usamos GridLayout para dividir en 2 columnas iguales
        JPanel panelTablas = new JPanel(new GridLayout(1, 2, 15, 0));
        panelTablas.setBackground(COLOR_FONDO); // Transparente o fondo base

        // --- TABLA 1: LLEGADAS ---
        JPanel card1 = new JPanel(new BorderLayout());
        estilizarCard(card1);
        
        JLabel lblTitulo1 = new JLabel("Conjunto 1: Llegadas");
        lblTitulo1.setFont(new Font("Inter", Font.BOLD, 16));
        lblTitulo1.setForeground(COLOR_TEXTO);
        lblTitulo1.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        lblTitulo1.setHorizontalAlignment(SwingConstants.CENTER);
        
        String[] col1 = {"#", "Ri (Entrada)", "Xi (Llegada)"};
        modeloTabla1 = new DefaultTableModel(col1, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla1 = new JTable(modeloTabla1);
        estilizarTabla(tabla1);
        
        JScrollPane scroll1 = new JScrollPane(tabla1);
        scroll1.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
        scroll1.getViewport().setBackground(COLOR_CARD);

        card1.add(lblTitulo1, BorderLayout.NORTH);
        card1.add(scroll1, BorderLayout.CENTER);

        // --- TABLA 2: SERVICIO ---
        JPanel card2 = new JPanel(new BorderLayout());
        estilizarCard(card2);
        
        JLabel lblTitulo2 = new JLabel("Conjunto 2: Servicio");
        lblTitulo2.setFont(new Font("Inter", Font.BOLD, 16));
        lblTitulo2.setForeground(COLOR_TEXTO);
        lblTitulo2.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        lblTitulo2.setHorizontalAlignment(SwingConstants.CENTER);
        
        String[] col2 = {"#", "Ri (Salida)", "Xi (Servicio)"};
        modeloTabla2 = new DefaultTableModel(col2, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla2 = new JTable(modeloTabla2);
        estilizarTabla(tabla2);
        
        JScrollPane scroll2 = new JScrollPane(tabla2);
        scroll2.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
        scroll2.getViewport().setBackground(COLOR_CARD);

        card2.add(lblTitulo2, BorderLayout.NORTH);
        card2.add(scroll2, BorderLayout.CENTER);

        // Agregar al panel contenedor
        panelTablas.add(card1);
        panelTablas.add(card2);
        
        return panelTablas;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        panel.setBackground(COLOR_FONDO);
        JButton btnLimpiar = crearBoton("Limpiar", new Color(239,68,68), false);
        btnLimpiar.addActionListener(e -> limpiarDatos());
        JButton btnVolver = crearBoton("Volver al Menú", COLOR_TEXTO_SECUNDARIO, false);
        btnVolver.addActionListener(e -> { new MenuPrincipal().setVisible(true); dispose(); });
        panel.add(btnLimpiar); panel.add(btnVolver);
        return panel;
    }

    // --- LÓGICA PRINCIPAL ---

    private void generarVariables() {
        if (!SimulacionDatos.getInstancia().hayDatos()) {
            JOptionPane.showMessageDialog(this, "No hay datos Ri cargados.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            List<Double> riLlegadas = SimulacionDatos.getInstancia().getConjunto1RiEn();
            List<Double> riServicio = SimulacionDatos.getInstancia().getConjunto2RiSn();
                
            int n = SimulacionDatos.getInstancia().getNGenerados();
            limpiarDatos();
            
            // Transformar ambos conjuntos
            for (int i = 0; i < n; i++) {
                // --- CONJUNTO 1: LLEGADAS ---
                double ri1 = riLlegadas.get(i);
                double xi1;
                // Lógica Empírica Llegadas
                if (ri1 < 0.151) xi1 = 2.0;
                else if (ri1 < 0.930) xi1 = 3.0;
                else xi1 = 4.0;
                transformadosLlegadas.add(xi1);
                
                // --- CONJUNTO 2: SERVICIO ---
                double ri2 = riServicio.get(i);
                double xi2;
                // Lógica Empírica Servicio
                if (ri2 < 0.195) xi2 = 2.0;
                else if (ri2 < 0.954) xi2 = 3.0;
                else xi2 = 4.0;
                transformadosServicio.add(xi2);
                
                // Agregar filas (limitamos visualización a 5000 por rendimiento si es necesario)
                if(i < 5000) {
                    modeloTabla1.addRow(new Object[]{i+1, String.format("%.5f", ri1), String.format("%.2f min", xi1)});
                    modeloTabla2.addRow(new Object[]{i+1, String.format("%.5f", ri2), String.format("%.2f min", xi2)});
                }
            }
            
            JOptionPane.showMessageDialog(this, "¡Transformación completada para ambos conjuntos!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al transformar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarDatos() {
        transformadosLlegadas.clear();
        transformadosServicio.clear();
        modeloTabla1.setRowCount(0); 
        modeloTabla2.setRowCount(0);
    }

    // --- Helpers UI (Estilos) ---
    private void estilizarCard(JPanel p) {
        p.setBackground(COLOR_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(0,0,0,0))
        );
    }
    private JLabel crearLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Inter",Font.BOLD,12));
        l.setForeground(COLOR_TEXTO_SECUNDARIO); l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }
    
    private JButton crearBoton(String t, Color c, boolean r) {
        JButton b = new JButton(t) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if(getModel().isPressed()) g2.setColor(c.darker()); else if(getModel().isRollover()) g2.setColor(r?c.brighter():COLOR_HOVER); else g2.setColor(r?c:COLOR_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                if(!r){ g2.setColor(c); g2.setStroke(new BasicStroke(1.5f)); g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,8,8); }
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(new Font("Inter",Font.BOLD,13)); b.setForeground(r?Color.WHITE:c); b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); b.setPreferredSize(new Dimension(150,40)); return b;
    }
    private void estilizarTabla(JTable t) {
        t.setFont(new Font("Inter",Font.PLAIN,13)); t.setBackground(COLOR_CARD); t.setForeground(COLOR_TEXTO); t.setGridColor(COLOR_BORDE);
        t.setRowHeight(28); t.setShowVerticalLines(true); t.setShowHorizontalLines(true);
        t.getTableHeader().setFont(new Font("Inter",Font.BOLD,13)); t.getTableHeader().setBackground(COLOR_HOVER); t.getTableHeader().setForeground(COLOR_TEXTO);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,1,0,COLOR_BORDE));
        DefaultTableCellRenderer r = new DefaultTableCellRenderer(); r.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0;i<t.getColumnCount();i++) t.getColumnModel().getColumn(i).setCellRenderer(r);
    }
}