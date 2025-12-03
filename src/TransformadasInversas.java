import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TransformadasInversas extends JFrame {
    
    private final Color COLOR_FONDO = new Color(18, 18, 20);
    private final Color COLOR_CARD = new Color(30, 30, 32);
    private final Color COLOR_PRIMARIO = new Color(0, 120, 215);
    private final Color COLOR_EXITO = new Color(87, 166, 74);
    private final Color COLOR_TEXTO = new Color(240, 240, 240);
    private final Color COLOR_TEXTO_SECUNDARIO = new Color(180, 180, 180);
    
    private JComboBox<String> comboDistribucion;
    private JTextField txtParam1, txtParam2, txtParam3;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private JLabel lblInfoDistribucion;
    private JLabel lblParam1, lblParam2, lblParam3;
    
    public TransformadasInversas() {
        setTitle("TRANSFORMADAS INVERSAS");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 0));
        panelPrincipal.setBackground(COLOR_FONDO);
        
        // Barra superior
        panelPrincipal.add(crearBarraSuperior(), BorderLayout.NORTH);
        
        // Panel central dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            crearPanelConfiguracion(), crearPanelResultados());
        splitPane.setDividerLocation(400);
        splitPane.setBackground(COLOR_FONDO);
        splitPane.setBorder(null);
        
        panelPrincipal.add(splitPane, BorderLayout.CENTER);
        
        add(panelPrincipal);
    }
    
    private JPanel crearBarraSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        // Título
        JLabel titulo = new JLabel("TRANSFORMADAS INVERSAS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(COLOR_TEXTO);
        
        // Botón regresar
        JButton btnRegresar = crearBotonIcono("←", COLOR_TEXTO_SECUNDARIO);
        btnRegresar.addActionListener(e -> {
            new MenuPrincipal().setVisible(true);
            this.dispose();
        });
        
        panel.add(btnRegresar, BorderLayout.WEST);
        panel.add(titulo, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelConfiguracion() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Tarjeta de configuración
        JPanel tarjeta = new JPanel(new BorderLayout(20, 20));
        tarjeta.setBackground(COLOR_CARD);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Título
        JLabel lblTitulo = new JLabel("CONFIGURACIÓN");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_PRIMARIO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Panel de selección
        JPanel panelSeleccion = new JPanel(new BorderLayout(10, 10));
        panelSeleccion.setBackground(COLOR_CARD);
        
        JLabel lblDist = new JLabel("Distribución:");
        lblDist.setForeground(COLOR_TEXTO);
        lblDist.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        comboDistribucion = new JComboBox<>(new String[]{
            "Uniforme Continua", "Exponencial", "Normal", "Triangular", "Bernoulli", "Binomial"
        });
        estilizarComboBox(comboDistribucion);
        comboDistribucion.addActionListener(e -> actualizarCamposParametros());
        
        panelSeleccion.add(lblDist, BorderLayout.WEST);
        panelSeleccion.add(comboDistribucion, BorderLayout.CENTER);
        
        // Panel de parámetros
        JPanel panelParametros = new JPanel(new GridLayout(3, 2, 10, 15));
        panelParametros.setBackground(COLOR_CARD);
        panelParametros.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Crear etiquetas y campos
        lblParam1 = new JLabel();
        txtParam1 = crearCampoTexto();
        
        lblParam2 = new JLabel();
        txtParam2 = crearCampoTexto();
        
        lblParam3 = new JLabel();
        txtParam3 = crearCampoTexto();
        
        panelParametros.add(lblParam1);
        panelParametros.add(txtParam1);
        panelParametros.add(lblParam2);
        panelParametros.add(txtParam2);
        panelParametros.add(lblParam3);
        panelParametros.add(txtParam3);
        
        // Panel de información
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(new Color(40, 40, 45));
        panelInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        lblInfoDistribucion = new JLabel("<html><div style='text-align: center;'>" +
            "Seleccione una distribución para ver información detallada</div></html>");
        lblInfoDistribucion.setForeground(COLOR_TEXTO_SECUNDARIO);
        lblInfoDistribucion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panelInfo.add(lblInfoDistribucion, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 10, 10));
        panelBotones.setBackground(COLOR_CARD);
        
        JButton btnGenerar = crearBotonModerno("GENERAR TRANSFORMADAS", COLOR_PRIMARIO);
        btnGenerar.addActionListener(e -> generarTransformadas());
        
        JButton btnUsarNumeros = crearBotonModerno("USAR NÚMEROS GENERADOS", COLOR_EXITO);
        btnUsarNumeros.addActionListener(e -> usarNumerosGenerados());
        
        panelBotones.add(btnGenerar);
        panelBotones.add(btnUsarNumeros);
        
        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(panelSeleccion, BorderLayout.NORTH);
        tarjeta.add(panelParametros, BorderLayout.CENTER);
        tarjeta.add(panelInfo, BorderLayout.SOUTH);
        tarjeta.add(panelBotones, BorderLayout.SOUTH);
        
        panel.add(tarjeta, BorderLayout.CENTER);
        
        // Inicializar campos
        actualizarCamposParametros();
        
        return panel;
    }
    
    private JPanel crearPanelResultados() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 25));
        
        // Tarjeta de resultados
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(COLOR_CARD);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Título
        JLabel lblTitulo = new JLabel("RESULTADOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_PRIMARIO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));
        lblTitulo.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Tabla de resultados
        modeloTabla = new DefaultTableModel(new Object[]{"#", "U", "X = F⁻¹(U)", "Distribución"}, 0);
        tablaResultados = new JTable(modeloTabla);
        estilizarTabla(tablaResultados);
        
        JScrollPane scrollTabla = new JScrollPane(tablaResultados);
        scrollTabla.getViewport().setBackground(COLOR_CARD);
        scrollTabla.setBorder(null);
        
        // Panel de estadísticas
        JPanel panelEstadisticas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEstadisticas.setBackground(new Color(40, 40, 45));
        panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblStats = new JLabel("Total de transformaciones: 0");
        lblStats.setForeground(COLOR_TEXTO_SECUNDARIO);
        lblStats.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panelEstadisticas.add(lblStats);
        
        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(scrollTabla, BorderLayout.CENTER);
        tarjeta.add(panelEstadisticas, BorderLayout.SOUTH);
        
        panel.add(tarjeta, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        campo.setBackground(new Color(45, 45, 48));
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return campo;
    }
    
    private void actualizarCamposParametros() {
        String distribucion = (String) comboDistribucion.getSelectedItem();
        
        // Ocultar todos los campos primero
        lblParam1.setVisible(false);
        txtParam1.setVisible(false);
        lblParam2.setVisible(false);
        txtParam2.setVisible(false);
        lblParam3.setVisible(false);
        txtParam3.setVisible(false);
        
        switch(distribucion) {
            case "Uniforme Continua":
                lblParam1.setText("Mínimo (a):");
                txtParam1.setText("0.0");
                lblParam1.setVisible(true);
                txtParam1.setVisible(true);
                
                lblParam2.setText("Máximo (b):");
                txtParam2.setText("1.0");
                lblParam2.setVisible(true);
                txtParam2.setVisible(true);
                
                lblInfoDistribucion.setText("<html><div style='text-align: center;'>" +
                    "<b>Distribución Uniforme Continua U(a,b)</b><br>" +
                    "Fórmula: X = a + (b-a)*U<br>" +
                    "U ~ Uniforme(0,1)</div></html>");
                break;
                
            case "Exponencial":
                lblParam1.setText("Tasa (λ):");
                txtParam1.setText("1.0");
                lblParam1.setVisible(true);
                txtParam1.setVisible(true);
                
                lblInfoDistribucion.setText("<html><div style='text-align: center;'>" +
                    "<b>Distribución Exponencial</b><br>" +
                    "Fórmula: X = -ln(1-U)/λ<br>" +
                    "λ > 0 (parámetro de tasa)</div></html>");
                break;
                
            case "Normal":
                lblParam1.setText("Media (μ):");
                txtParam1.setText("0.0");
                lblParam1.setVisible(true);
                txtParam1.setVisible(true);
                
                lblParam2.setText("Desviación (σ):");
                txtParam2.setText("1.0");
                lblParam2.setVisible(true);
                txtParam2.setVisible(true);
                
                lblInfoDistribucion.setText("<html><div style='text-align: center;'>" +
                    "<b>Distribución Normal N(μ,σ²)</b><br>" +
                    "Método: Box-Muller<br>" +
                    "X = μ + σ * √(-2·ln(U₁))·cos(2π·U₂)</div></html>");
                break;
                
            case "Triangular":
                lblParam1.setText("Mínimo (a):");
                txtParam1.setText("0.0");
                lblParam1.setVisible(true);
                txtParam1.setVisible(true);
                
                lblParam2.setText("Máximo (b):");
                txtParam2.setText("1.0");
                lblParam2.setVisible(true);
                txtParam2.setVisible(true);
                
                lblParam3.setText("Moda (c):");
                txtParam3.setText("0.5");
                lblParam3.setVisible(true);
                txtParam3.setVisible(true);
                
                lblInfoDistribucion.setText("<html><div style='text-align: center;'>" +
                    "<b>Distribución Triangular T(a,b,c)</b><br>" +
                    "Moda c se calcula automáticamente</div></html>");
                break;
                
            case "Bernoulli":
                lblParam1.setText("Probabilidad (p):");
                txtParam1.setText("0.5");
                lblParam1.setVisible(true);
                txtParam1.setVisible(true);
                
                lblInfoDistribucion.setText("<html><div style='text-align: center;'>" +
                    "<b>Distribución Bernoulli(p)</b><br>" +
                    "X = 1 si U ≤ p, 0 si U > p<br>" +
                    "Probabilidad de éxito: p</div></html>");
                break;
                
            case "Binomial":
                lblParam1.setText("Ensayo (n):");
                txtParam1.setText("10");
                lblParam1.setVisible(true);
                txtParam1.setVisible(true);
                
                lblParam2.setText("Probabilidad (p):");
                txtParam2.setText("0.5");
                lblParam2.setVisible(true);
                txtParam2.setVisible(true);
                
                lblInfoDistribucion.setText("<html><div style='text-align: center;'>" +
                    "<b>Distribución Binomial(n,p)</b><br>" +
                    "Suma de n ensayos Bernoulli<br>" +
                    "Número de éxitos en n intentos</div></html>");
                break;
        }
    }
    
    private void generarTransformadas() {
        modeloTabla.setRowCount(0);
        List<Double> numeros = obtenerNumerosParaTransformar();
        
        if (numeros.isEmpty()) {
            mostrarError("No hay números disponibles. Genere números primero o use números generados.");
            return;
        }
        
        String distribucion = (String) comboDistribucion.getSelectedItem();
        
        for (int i = 0; i < numeros.size(); i++) {
            double u = numeros.get(i);
            double x = transformarInversa(u, distribucion);
            
            modeloTabla.addRow(new Object[]{
                i + 1,
                String.format("%.6f", u),
                String.format("%.6f", x),
                distribucion
            });
        }
        
        mostrarExito(String.format("Se generaron %d transformadas con distribución %s", 
            numeros.size(), distribucion));
    }
    
    private double transformarInversa(double u, String distribucion) {
        try {
            switch(distribucion) {
                case "Uniforme Continua":
                    double a = Double.parseDouble(txtParam1.getText());
                    double b = Double.parseDouble(txtParam2.getText());
                    return a + (b - a) * u;
                    
                case "Exponencial":
                    double lambda = Double.parseDouble(txtParam1.getText());
                    return -Math.log(1 - u) / lambda;
                    
                case "Normal":
                    double mu = Double.parseDouble(txtParam1.getText());
                    double sigma = Double.parseDouble(txtParam2.getText());
                    double u1 = u;
                    double u2 = (u + 0.5) % 1.0;
                    double z = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
                    return mu + sigma * z;
                    
                case "Triangular":
                    double triA = Double.parseDouble(txtParam1.getText());
                    double triB = Double.parseDouble(txtParam2.getText());
                    double c = Double.parseDouble(txtParam3.getText());
                    if (u <= (c - triA) / (triB - triA)) {
                        return triA + Math.sqrt(u * (triB - triA) * (c - triA));
                    } else {
                        return triB - Math.sqrt((1 - u) * (triB - triA) * (triB - c));
                    }
                    
                case "Bernoulli":
                    double p = Double.parseDouble(txtParam1.getText());
                    return (u <= p) ? 1.0 : 0.0;
                    
                case "Binomial":
                    int n = Integer.parseInt(txtParam1.getText());
                    double prob = Double.parseDouble(txtParam2.getText());
                    int exitos = 0;
                    for (int i = 0; i < n; i++) {
                        double uTemp = (u + i * 0.1) % 1.0;
                        if (uTemp <= prob) exitos++;
                    }
                    return exitos;
                    
                default:
                    return u;
            }
        } catch (Exception e) {
            mostrarError("Error en parámetros: " + e.getMessage());
            return 0.0;
        }
    }
    
    private List<Double> obtenerNumerosParaTransformar() {
        GeneradorNumeros generador = GeneradorNumeros.getInstance();
        if (generador.hayNumeros()) {
            return new ArrayList<>(generador.getNumeros());
        }
        
        List<Double> numeros = new ArrayList<>();
        java.util.Random rand = new java.util.Random();
        
        for (int i = 0; i < 50; i++) {
            numeros.add(rand.nextDouble());
        }
        
        return numeros;
    }
    
    private void usarNumerosGenerados() {
        GeneradorNumeros generador = GeneradorNumeros.getInstance();
        if (generador.hayNumeros()) {
            int cantidad = generador.getNumeros().size();
            mostrarExito("Usando " + cantidad + " números generados anteriormente.");
        } else {
            mostrarError("No hay números generados. Genere números en el módulo de Generación primero.");
        }
    }
    
    // Métodos de estilo (similares a Practica3.java)
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
    
    private JButton crearBotonIcono(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        boton.setForeground(color);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setForeground(COLOR_TEXTO);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setForeground(color);
            }
        });
        
        return boton;
    }
    
    private void estilizarComboBox(JComboBox<?> combo) {
        combo.setBackground(new Color(45, 45, 48));
        combo.setForeground(COLOR_TEXTO);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? COLOR_PRIMARIO : new Color(45, 45, 48));
                setForeground(isSelected ? Color.WHITE : COLOR_TEXTO);
                return this;
            }
        });
    }
    
    private void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(40);
        tabla.setBackground(COLOR_CARD);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setGridColor(new Color(60, 60, 60));
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        tabla.getTableHeader().setBackground(new Color(45, 45, 48));
        tabla.getTableHeader().setForeground(COLOR_TEXTO);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
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
                    c.setBackground(row % 2 == 0 ? COLOR_CARD : new Color(40, 40, 42));
                    c.setForeground(COLOR_TEXTO);
                }
                
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
    }
    
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, 
            "<html><div style='color: #57a64a; font-family: Segoe UI;'>✓ " + mensaje + "</div></html>",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, 
            "<html><div style='color: #dc6e6e; font-family: Segoe UI;'>✗ " + mensaje + "</div></html>",
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}