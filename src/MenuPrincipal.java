import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MenuPrincipal extends JFrame {
    
    private final Color COLOR_FONDO = new Color(18, 18, 20);
    private final Color COLOR_CARD = new Color(30, 30, 32);
    private final Color COLOR_AZUL = new Color(0, 120, 215);
    private final Color COLOR_TEXTO = new Color(240, 240, 240);
    private final Color COLOR_TEXTO_SECUNDARIO = new Color(180, 180, 180);
    
    public MenuPrincipal() {
        setTitle("SIMULACIÓN - MENÚ PRINCIPAL");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);
        
        // Eliminar decoraciones y hacer ventana redondeada
        setUndecorated(true);
        
        // Panel de título mejorado
        JPanel panelTitulo = crearPanelTitulo();
        
        // Panel central con tarjetas
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(COLOR_FONDO);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        
        // Crear tarjetas de módulos
        panelCentral.add(crearTarjetaModulo(
            "1. GENERACIÓN Y PRUEBAS ESTADÍSTICAS",
            "Genera números pseudoaleatorios y ejecuta pruebas de calidad",
            COLOR_AZUL,
            e -> abrirVentana(new Pseudoaleatorios())
        ), gbc);
        
        panelCentral.add(crearTarjetaModulo(
            "2. TRANSFORMADAS INVERSAS",
            "Aplica transformadas inversas a diferentes distribuciones",
            COLOR_AZUL,
            e -> abrirVentana(new TransformadasInversas())
        ), gbc);
        
        panelCentral.add(crearTarjetaModulo(
            "3. SIMULACIÓN TORTILLERÍA",
            "Simula el flujo de clientes con datos reales de La Providencia",
            COLOR_AZUL,
            e -> abrirVentana(new SimulacionTortilleria())
        ), gbc);
        
        panelCentral.add(crearTarjetaModulo(
            "4. INFORMACIÓN DEL PROYECTO",
            "Detalles técnicos y documentación del sistema",
            COLOR_AZUL,
            e -> mostrarInformacion()
        ), gbc);
        
        // Panel inferior con botón salir
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(COLOR_FONDO);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(0, 40, 30, 40));
        
        JButton btnSalir = crearBotonModerno("SALIR", COLOR_AZUL);
        btnSalir.addActionListener(e -> System.exit(0));
        
        panelInferior.add(btnSalir, BorderLayout.CENTER);
        
        add(panelTitulo, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
        
        // Agregar botón de cerrar personalizado
        agregarBotonCerrar();
    }
    
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 20, 40));
        
        // Título principal
        JLabel titulo = new JLabel("PRÁCTICA 3 - SIMULACIÓN");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Subtítulo
        JLabel subtitulo = new JLabel("Tortillería La Providencia");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitulo.setForeground(COLOR_TEXTO_SECUNDARIO);
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Línea divisoria
        JSeparator separador = new JSeparator();
        separador.setForeground(new Color(60, 60, 60));
        separador.setBackground(new Color(60, 60, 60));
        
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.CENTER);
        panel.add(subtitulo, BorderLayout.CENTER);
        panel.add(Box.createRigidArea(new Dimension(0, 20)), BorderLayout.CENTER);
        panel.add(separador, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearTarjetaModulo(String titulo, String descripcion, Color color, java.awt.event.ActionListener accion) {
        JPanel tarjeta = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo de tarjeta
                g2.setColor(COLOR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Borde con efecto hover
                if (getClientProperty("hover") == Boolean.TRUE) {
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
                }
                
                g2.dispose();
            }
        };
        
        tarjeta.setOpaque(false);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tarjeta.setPreferredSize(new Dimension(600, 100));
        
        // Título de la tarjeta
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_TEXTO);
        
        // Descripción
        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesc.setForeground(COLOR_TEXTO_SECUNDARIO);
        
        // Icono
        JLabel icono = new JLabel("›");
        icono.setFont(new Font("Segoe UI", Font.BOLD, 24));
        icono.setForeground(color);
        icono.setHorizontalAlignment(SwingConstants.RIGHT);
        
        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblDesc, BorderLayout.CENTER);
        tarjeta.add(icono, BorderLayout.EAST);
        
        // Efectos hover
        tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                tarjeta.putClientProperty("hover", true);
                tarjeta.repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                tarjeta.putClientProperty("hover", false);
                tarjeta.repaint();
            }
            
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                accion.actionPerformed(null);
            }
        });
        
        return tarjeta;
    }
    
    private JButton crearBotonModerno(String texto, Color color) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setForeground(Color.WHITE);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return boton;
    }
    
    private void abrirVentana(JFrame ventana) {
        ventana.setVisible(true);
        this.dispose();
    }
    
    private void agregarBotonCerrar() {
        JButton btnCerrar = new JButton("×");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnCerrar.setForeground(COLOR_TEXTO_SECUNDARIO);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> System.exit(0));
        
        btnCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnCerrar.setForeground(COLOR_AZUL);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnCerrar.setForeground(COLOR_TEXTO_SECUNDARIO);
            }
        });
        
        JPanel panelCerrar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelCerrar.setBackground(COLOR_FONDO);
        panelCerrar.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        panelCerrar.add(btnCerrar);
        
        add(panelCerrar, BorderLayout.NORTH);
    }
    
    private void mostrarInformacion() {
        String info = "<html><div style='font-family: Segoe UI; color: #f0f0f0; text-align: center;'>" +
            "<h2 style='color: #0078d7; margin-bottom: 10px;'>PRÁCTICA 3 - SIMULACIÓN</h2>" +
            "<h3 style='color: #0078d7; margin-top: 0;'>Tortillería La Providencia</h3>" +
            "<div style='border-bottom: 1px solid #3e3e42; margin: 20px 0;'></div>" +
            
            "<div style='text-align: left; margin: 0 auto; max-width: 400px;'>" +
            "<p style='margin: 10px 0;'><span style='color: #0078d7; font-weight: bold;'>Institución:</span><br>Tecnológico Superior de Tacámbaro</p>" +
            "<p style='margin: 10px 0;'><span style='color: #0078d7; font-weight: bold;'>Carrera:</span><br>Ingeniería en Sistemas Computacionales</p>" +
            "<p style='margin: 10px 0;'><span style='color: #0078d7; font-weight: bold;'>Materia:</span><br>Simulación - 5° Semestre</p>" +
            "<p style='margin: 10px 0;'><span style='color: #0078d7; font-weight: bold;'>Profesor:</span><br>Oscar Alvarez Arriaga</p>" +
            "</div>" +
            
            "<div style='border-bottom: 1px solid #3e3e42; margin: 20px 0;'></div>" +
            
            "<div style='text-align: left; margin: 0 auto; max-width: 400px;'>" +
            "<p style='color: #0078d7; font-weight: bold; margin-bottom: 10px;'>Integrantes:</p>" +
            "<p style='margin: 5px 0;'>• Irving Guijoza Cuellar (23940030)</p>" +
            "<p style='margin: 5px 0;'>• Victor Saul Franco Maldonado (23940019)</p>" +
            "<p style='margin: 5px 0;'>• Eduardo Salvador Ambriz Ortega (22940024)</p>" +
            "</div>" +
            
            "<div style='border-bottom: 1px solid #3e3e42; margin: 20px 0;'></div>" +
            
            "<p style='font-size: 11px; color: #999999; margin-top: 20px;'>" +
            "Proyecto desarrollado como parte de la Práctica 3 de Simulación<br>" +
            "Datos reales tomados en Tortillería La Providencia</p>" +
            "</div></html>";
        
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(info);
        textPane.setEditable(false);
        textPane.setBackground(COLOR_CARD);
        textPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(500, 550));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Información del Proyecto", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(COLOR_FONDO);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));
        super.paint(g2);
        g2.dispose();
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.background", new Color(30, 30, 32));
            UIManager.put("Panel.background", new Color(30, 30, 32));
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
            UIManager.put("TextPane.background", new Color(30, 30, 32));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
        });
    }
}