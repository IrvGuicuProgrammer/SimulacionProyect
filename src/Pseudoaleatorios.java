import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.border.EmptyBorder;

public class Pseudoaleatorios extends JFrame {
    
    private final Color COLOR_FONDO = new Color(18, 18, 20);
    private final Color COLOR_CARD = new Color(30, 30, 32);
    private final Color COLOR_PRIMARIO = new Color(0, 150, 255);   // Más brillante
    private final Color COLOR_EXITO = new Color(100, 200, 100);    // Verde brillante
    private final Color COLOR_NARANJA = new Color(255, 165, 0);    // Naranja brillante
    private final Color COLOR_TEXTO = new Color(240, 240, 240);
    private final Color COLOR_TEXTO_SECUNDARIO = new Color(200, 200, 200);
    
    private JTextField txtN, txtX0, txtA, txtC, txtM;
    private JTable tablaGenerados;
    private DefaultTableModel modeloGenerados;
    private JComboBox<String> comboConfianza;
    private JButton btnGenerar, btnEjecutarPruebas, btnRegresar;
    private JTabbedPane panelPruebas;
    private List<Double> numeros = new ArrayList<>();
    
    public Pseudoaleatorios() {
        setTitle("GENERACIÓN Y PRUEBAS ESTADÍSTICAS");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1300, 850);  // Tamaño aumentado
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 0));
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Barra superior mejorada
        panelPrincipal.add(crearBarraSuperior(), BorderLayout.NORTH);
        
        // Panel central con pestañas
        panelPruebas = new JTabbedPane();
        panelPruebas.setBackground(COLOR_FONDO);
        panelPruebas.setForeground(COLOR_TEXTO);
        panelPruebas.setFont(new Font("Segoe UI", Font.BOLD, 14));  // Negrita
        panelPruebas.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARIO, 1));
        
        // Pestañas con íconos
        panelPruebas.addTab("🔢 Generación", crearPanelGeneracion());
        panelPruebas.addTab("📊 Pruebas Básicas", crearPanelPruebasBasicas());
        panelPruebas.addTab("🎯 Pruebas Avanzadas", crearPanelPruebasAvanzadas());
        
        panelPrincipal.add(panelPruebas, BorderLayout.CENTER);
        
        add(panelPrincipal);
        configurarAcciones();
        
        // Agregar sombra a la ventana
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
    }
    
    private JPanel crearBarraSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, COLOR_PRIMARIO),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        
        // Título con ícono
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTitulo.setBackground(COLOR_CARD);
        
        JLabel icono = new JLabel("🔢");
        icono.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        icono.setForeground(COLOR_PRIMARIO);
        
        JLabel titulo = new JLabel("GENERACIÓN DE NÚMEROS PSEUDOALEATORIOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));  // Tamaño aumentado
        titulo.setForeground(COLOR_TEXTO);
        
        panelTitulo.add(icono);
        panelTitulo.add(titulo);
        
        // Botón regresar mejorado
        btnRegresar = crearBotonConIcono("← Regresar", COLOR_PRIMARIO);
        btnRegresar.addActionListener(e -> {
            new MenuPrincipal().setVisible(true);
            this.dispose();
        });
        
        panel.add(panelTitulo, BorderLayout.CENTER);
        panel.add(btnRegresar, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelGeneracion() {
        JPanel panel = new JPanel(new BorderLayout(25, 25));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Panel de parámetros - Tarjeta con fondo
        JPanel panelParametros = crearTarjeta("PARÁMETROS DEL GENERADOR", 25);
        
        JPanel gridParams = new JPanel(new GridLayout(2, 5, 20, 20));
        gridParams.setBackground(COLOR_CARD);
        gridParams.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Crear campos con etiquetas mejoradas
        gridParams.add(crearCampoConEtiquetaMejorado("n:", "50"));
        txtN = ((JTextField) ((JPanel) gridParams.getComponent(0)).getComponent(1));
        
        gridParams.add(crearCampoConEtiquetaMejorado("Semilla X₀:", "97"));
        txtX0 = ((JTextField) ((JPanel) gridParams.getComponent(1)).getComponent(1));
        
        gridParams.add(crearCampoConEtiquetaMejorado("a:", "165"));
        txtA = ((JTextField) ((JPanel) gridParams.getComponent(2)).getComponent(1));
        
        gridParams.add(crearCampoConEtiquetaMejorado("c:", "21"));
        txtC = ((JTextField) ((JPanel) gridParams.getComponent(3)).getComponent(1));
        
        gridParams.add(crearCampoConEtiquetaMejorado("m:", "256"));
        txtM = ((JTextField) ((JPanel) gridParams.getComponent(4)).getComponent(1));
        
        panelParametros.add(gridParams, BorderLayout.CENTER);
        
        // Panel de controles mejorado
        JPanel panelControles = crearTarjeta("CONTROLES", 25);
        panelControles.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 20));
        
        btnGenerar = crearBotonGrande("🚀 GENERAR NÚMEROS", COLOR_PRIMARIO);
        panelControles.add(btnGenerar);
        
        JPanel panelConfianza = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelConfianza.setBackground(COLOR_CARD);
        
        JLabel lblConfianza = new JLabel("📊 Nivel de confianza:");
        lblConfianza.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblConfianza.setForeground(COLOR_TEXTO);
        panelConfianza.add(lblConfianza);
        
        comboConfianza = new JComboBox<>(new String[]{"99%", "95%", "90%", "87%", "85%"});
        comboConfianza.setSelectedIndex(1);
        estilizarComboBoxMejorado(comboConfianza);
        panelConfianza.add(comboConfianza);
        
        panelControles.add(panelConfianza);
        
        btnEjecutarPruebas = crearBotonGrande("✅ EJECUTAR PRUEBAS", COLOR_EXITO);
        panelControles.add(btnEjecutarPruebas);
        
        // Tabla de números generados - Tarjeta
        JPanel panelTabla = crearTarjeta("NÚMEROS GENERADOS", 25);
        
        modeloGenerados = new DefaultTableModel(new Object[]{"#", "Número Pseudoaleatorio"}, 0);
        tablaGenerados = new JTable(modeloGenerados);
        estilizarTablaMejorada(tablaGenerados);
        
        JScrollPane scrollTabla = new JScrollPane(tablaGenerados);
        scrollTabla.getViewport().setBackground(COLOR_CARD);
        scrollTabla.setBorder(null);
        
        panelTabla.add(scrollTabla, BorderLayout.CENTER);
        
        // Panel de estadísticas de la tabla
        JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelStats.setBackground(new Color(40, 40, 45));
        panelStats.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel lblStats = new JLabel("🔢 Total de números: 0");
        lblStats.setForeground(COLOR_TEXTO_SECUNDARIO);
        lblStats.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelStats.add(lblStats);
        
        panelTabla.add(panelStats, BorderLayout.SOUTH);
        
        // Layout principal
        JPanel panelSuperior = new JPanel(new BorderLayout(25, 25));
        panelSuperior.setBackground(COLOR_FONDO);
        panelSuperior.add(panelParametros, BorderLayout.NORTH);
        panelSuperior.add(panelControles, BorderLayout.CENTER);
        
        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(panelTabla, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearTarjeta(String titulo, int padding) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(COLOR_CARD);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
            BorderFactory.createEmptyBorder(padding, padding, padding, padding)
        ));
        
        if (titulo != null) {
            JLabel lblTitulo = new JLabel(titulo);
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblTitulo.setForeground(COLOR_PRIMARIO);
            lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            tarjeta.add(lblTitulo, BorderLayout.NORTH);
        }
        
        return tarjeta;
    }
    
    private JPanel crearCampoConEtiquetaMejorado(String etiqueta, String valor) {
        JPanel panel = new JPanel(new BorderLayout(15, 5));
        panel.setBackground(COLOR_CARD);
        
        JLabel lbl = new JLabel(etiqueta);
        lbl.setForeground(COLOR_TEXTO);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));  // Negrita
        
        JTextField campo = new JTextField(valor, 10);
        estilizarCampoTextoMejorado(campo);
        
        panel.add(lbl, BorderLayout.WEST);
        panel.add(campo, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelPruebasBasicas() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 20, 20));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        panel.add(crearTarjetaPruebaMejorada(
            "📈 PRUEBA DE MEDIA",
            "Verifica si la media está dentro del intervalo de confianza",
            COLOR_PRIMARIO,
            new String[]{"Media calculada:", "Límite inferior:", "Límite superior:", "Decisión:"}
        ));
        
        panel.add(crearTarjetaPruebaMejorada(
            "📊 PRUEBA DE VARIANZA",
            "Evalúa la varianza de la distribución uniforme",
            COLOR_EXITO,
            new String[]{"Varianza calculada:", "Límite inferior:", "Límite superior:", "Decisión:"}
        ));
        
        panel.add(crearTarjetaPruebaMejorada(
            "📐 PRUEBA DE FORMA",
            "Prueba de bondad de ajuste a distribución uniforme",
            COLOR_NARANJA,
            new String[]{"Chi² calculado:", "Chi² crítico:", "Grados libertad:", "Decisión:"}
        ));
        
        return panel;
    }
    
    private JPanel crearPanelPruebasAvanzadas() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 20, 20));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        panel.add(crearTarjetaPruebaMejorada(
            "🎲 PRUEBA DE POKER",
            "Prueba de independencia basada en dígitos",
            new Color(180, 100, 255),
            new String[]{"Chi² calculado:", "Chi² crítico:", "Manos diferentes:", "Decisión:"}
        ));
        
        panel.add(crearTarjetaPruebaMejorada(
            "📈 PRUEBA DE CORRIDAS",
            "Prueba de aleatoriedad en secuencias",
            new Color(255, 140, 100),
            new String[]{"Corridas totales:", "Esperadas:", "Estadístico Z:", "Decisión:"}
        ));
        
        panel.add(crearTarjetaPruebaMejorada(
            "🔢 PRUEBA DE SERIES",
            "Prueba de independencia en pares consecutivos",
            new Color(100, 200, 255),
            new String[]{"Chi² calculado:", "Chi² crítico:", "Pares analizados:", "Decisión:"}
        ));
        
        return panel;
    }
    
    private JPanel crearTarjetaPruebaMejorada(String titulo, String descripcion, 
                                            Color color, String[] labels) {
        JPanel tarjeta = new JPanel(new BorderLayout(15, 15));
        tarjeta.setBackground(COLOR_CARD);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Encabezado con color
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setBackground(COLOR_CARD);
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));  // Tamaño aumentado
        lblTitulo.setForeground(color);
        
        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(COLOR_TEXTO_SECUNDARIO);
        
        header.add(lblTitulo, BorderLayout.NORTH);
        header.add(lblDesc, BorderLayout.SOUTH);
        
        // Resultados en grid
        JPanel resultados = new JPanel(new GridLayout(4, 2, 15, 15));
        resultados.setBackground(COLOR_CARD);
        resultados.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        for (String label : labels) {
            resultados.add(crearEtiquetaMejorada(label));
            resultados.add(crearEtiquetaResultadoMejorada("--"));
        }
        
        tarjeta.add(header, BorderLayout.NORTH);
        tarjeta.add(resultados, BorderLayout.CENTER);
        
        return tarjeta;
    }
    
    private JLabel crearEtiquetaMejorada(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(COLOR_TEXTO);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));  // Negrita
        return label;
    }
    
    private JLabel crearEtiquetaResultadoMejorada(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        label.setBackground(new Color(45, 45, 48));
        label.setOpaque(true);
        return label;
    }
    
    private JButton crearBotonGrande(String texto, Color color) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradiente más pronunciado
                GradientPaint gradient;
                if (getModel().isPressed()) {
                    gradient = new GradientPaint(
                        0, 0, color.darker().darker(),
                        getWidth(), getHeight(), color.darker()
                    );
                } else if (getModel().isRollover()) {
                    gradient = new GradientPaint(
                        0, 0, color.brighter().brighter(),
                        getWidth(), getHeight(), color.brighter()
                    );
                } else {
                    gradient = new GradientPaint(
                        0, 0, color.brighter(),
                        getWidth(), getHeight(), color
                    );
                }
                
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Borde brillante
                g2.setColor(color.brighter().brighter());
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 25, 25);
                
                // Sombra exterior
                g2.setColor(new Color(0, 0, 0, 80));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        boton.setFont(new Font("Segoe UI", Font.BOLD, 16));  // Tamaño aumentado
        boton.setForeground(Color.WHITE);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));  // Más padding
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto de elevación
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setBorder(BorderFactory.createEmptyBorder(12, 27, 18, 33));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
            }
        });
        
        return boton;
    }
    
    private JButton crearBotonConIcono(String texto, Color color) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo del botón
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setForeground(Color.WHITE);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return boton;
    }
    
    private void estilizarCampoTextoMejorado(JTextField campo) {
        campo.setBackground(new Color(45, 45, 48));
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        campo.setFont(new Font("Segoe UI", Font.BOLD, 14));  // Negrita
        campo.setSelectionColor(COLOR_PRIMARIO);
        campo.setSelectedTextColor(Color.WHITE);
    }
    
    private void estilizarComboBoxMejorado(JComboBox<?> combo) {
        combo.setBackground(new Color(45, 45, 48));
        combo.setForeground(COLOR_TEXTO);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        combo.setFont(new Font("Segoe UI", Font.BOLD, 14));  // Negrita
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? COLOR_PRIMARIO : new Color(45, 45, 48));
                setForeground(isSelected ? Color.WHITE : COLOR_TEXTO);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                return this;
            }
        });
    }
    
    private void estilizarTablaMejorada(JTable tabla) {
        tabla.setRowHeight(40);
        tabla.setBackground(COLOR_CARD);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setGridColor(new Color(70, 70, 70));
        tabla.setFont(new Font("Segoe UI", Font.BOLD, 13));  // Negrita
        tabla.setShowGrid(true);
        tabla.setIntercellSpacing(new Dimension(1, 1));
        
        tabla.getTableHeader().setBackground(new Color(50, 50, 55));
        tabla.getTableHeader().setForeground(COLOR_TEXTO);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));  // Negrita
        tabla.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        tabla.getTableHeader().setReorderingAllowed(false);
        
        tabla.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(COLOR_PRIMARIO);
                    c.setForeground(Color.WHITE);
                } else {
                    if (row % 2 == 0) {
                        c.setBackground(COLOR_CARD);
                    } else {
                        c.setBackground(new Color(40, 40, 45));
                    }
                    c.setForeground(COLOR_TEXTO);
                }
                
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                return c;
            }
        });
    }
    
    private void configurarAcciones() {
        btnGenerar.addActionListener(e -> generarNumeros());
        btnEjecutarPruebas.addActionListener(e -> ejecutarTodasLasPruebas());
    }
    
    private void generarNumeros() {
        try {
            int n = Integer.parseInt(txtN.getText().trim());
            int x = Integer.parseInt(txtX0.getText().trim());
            int a = Integer.parseInt(txtA.getText().trim());
            int c = Integer.parseInt(txtC.getText().trim());
            int m = Integer.parseInt(txtM.getText().trim());
            
            if (n <= 0 || m <= 1) {
                mostrarError("❌ Verifica valores del generador (n>0, m>1).");
                return;
            }
            
            numeros.clear();
            modeloGenerados.setRowCount(0);
            
            GeneradorNumeros generador = GeneradorNumeros.getInstance();
            generador.setParametros(n, x, a, c, m);
            
            int currentX = x;
            for (int i = 1; i <= n; i++) {
                int raw = a * currentX + c;
                int entero = raw / m;
                int residuo = raw - (m * entero);
                residuo = ((residuo % m) + m) % m;
                
                double pseudo = (double) residuo / (double) (m - 1);
                
                numeros.add(pseudo);
                modeloGenerados.addRow(new Object[] { 
                    String.format("%03d", i), 
                    String.format("%.6f", pseudo) 
                });
                currentX = residuo;
            }
            
            generador.setNumeros(new ArrayList<>(numeros));
            
            mostrarExito(String.format("✅ Se generaron %d números pseudoaleatorios.", n));
            
        } catch (NumberFormatException ex) {
            mostrarError("❌ Verifica que todos los parámetros sean números válidos.");
        }
    }
    
    private void ejecutarTodasLasPruebas() {
        if (numeros.isEmpty()) {
            mostrarError("⚠️ Primero genera los números pseudoaleatorios.");
            return;
        }
        
        panelPruebas.setSelectedIndex(1);
        mostrarExito("✅ Ejecutando todas las pruebas estadísticas...");
        
        // Aquí iría el código de ejecución de pruebas
    }
    
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, 
            "<html><div style='font-family: Segoe UI; font-size: 14px; color: #64c864; padding: 10px;'>" +
            mensaje + "</div></html>",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, 
            "<html><div style='font-family: Segoe UI; font-size: 14px; color: #ff6b6b; padding: 10px;'>" +
            mensaje + "</div></html>",
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}