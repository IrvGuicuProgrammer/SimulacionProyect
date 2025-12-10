import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;

public class Pseudoaleatorios extends JFrame {
    
    // Paleta de colores oscura
    private final Color COLOR_FONDO = new Color(15, 15, 20);//Negro azulado
    private final Color COLOR_CARD = new Color(30, 30, 40);//azul carbon
    private final Color COLOR_PRIMARIO = new Color(99, 102, 241); // Indigo
    private final Color COLOR_TEXTO = new Color(240, 240, 245);//Blanco suave
    private final Color COLOR_TEXTO_SECUNDARIO = new Color(160, 165, 180);//Gris frio
    private final Color COLOR_BORDE = new Color(50, 55, 65);//Slate gris
    private final Color COLOR_EXITO = new Color(16, 185, 129); // Green
    private final Color COLOR_ERROR = new Color(239, 68, 68); // Red
    private final Color COLOR_HOVER = new Color(40, 45, 55);//Slate oscuro
    private final Color COLOR_NEGRO = new Color(0,0,0);//Pos negro xd
    private final Color COLOR_ACEPTACION = new Color(16, 185, 129, 100); // Verde transparente
    private final Color COLOR_RECHAZO = new Color(239, 68, 68, 100); // Rojo transparente
    private final Color COLOR_CURVA = new Color(99, 102, 241); // Indigo para la curva
    private final Color COLOR_LINEA = new Color(245, 158, 11); // Amarillo para la línea del estadístico
    private final Color COLOR_CONJUNTO1 = new Color(99, 102, 241); // Azul para conjunto 1
    private final Color COLOR_CONJUNTO2 = new Color(239, 68, 68);  // Rojo para conjunto 2
    
    private JComboBox<String> cbMetodo;
    private JComboBox<String> cbConfianza;
    private JPanel panelParametros;
    private JTextField txtN;
    private JTextField txtX0;
    private JTextField txtA;
    private JTextField txtC;
    private JTextField txtM;
    private JTextField txtSemilla;
    private JTabbedPane tabbedPaneNumeros;
    private JTable tablaNumeros1;
    private JTable tablaNumeros2;
    private JScrollPane scrollPruebas;
    private JPanel panelPruebas;
    private List<Double>[] numerosGenerados; // Array de listas para 2 conjuntos
    private double nivelConfianza = 0.95; // Valor por defecto
    private EstadisticasCalculador.ResultadoPrueba[] resultadosPruebasConjunto1;
    private EstadisticasCalculador.ResultadoPrueba[] resultadosPruebasConjunto2;
    private int conjuntoPruebasActual = 0; // 0 para conjunto 1, 1 para conjunto 2
    
    public Pseudoaleatorios() {
        setTitle("Generador de Números Pseudoaleatorios - 2 Conjuntos");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1600, 900); // Ventana más ancha
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(COLOR_FONDO);
        
        // Inicializar todos los campos de texto
        txtN = new JTextField();
        txtX0 = new JTextField();
        txtA = new JTextField();
        txtC = new JTextField();
        txtM = new JTextField();
        txtSemilla = new JTextField();
        
        // Inicializar arrays de resultados para 2 conjuntos
        resultadosPruebasConjunto1 = new EstadisticasCalculador.ResultadoPrueba[6];
        resultadosPruebasConjunto2 = new EstadisticasCalculador.ResultadoPrueba[6];
        
        // Inicializar array de números generados
        numerosGenerados = new ArrayList[2];
        numerosGenerados[0] = new ArrayList<>();
        numerosGenerados[1] = new ArrayList<>();
        
        // Panel principal con padding
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(COLOR_FONDO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        mainPanel.add(crearPanelSuperior(), BorderLayout.NORTH);
        mainPanel.add(crearPanelCentral(), BorderLayout.CENTER);
        mainPanel.add(crearPanelInferior(), BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO);
        
        // Título principal
        JLabel titulo = new JLabel("Generador de Números Pseudoaleatorios");
        titulo.setFont(new Font("Inter", Font.BOLD, 26));
        titulo.setForeground(COLOR_TEXTO);
        
        // Subtítulo
        JLabel subtitulo = new JLabel("Genera 2 conjuntos para simulación de entrada/salida - !Cada conjunto ejecuta pruebas por separado!");
        subtitulo.setFont(new Font("Inter", Font.PLAIN, 14));
        subtitulo.setForeground(COLOR_TEXTO_SECUNDARIO);
        
        // Panel de selectores
        JPanel panelSelectores = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelSelectores.setBackground(COLOR_FONDO);
        
        // Selector de método
        JLabel lblMetodo = new JLabel("Método:");
        lblMetodo.setFont(new Font("Inter", Font.BOLD, 14));
        lblMetodo.setForeground(COLOR_TEXTO);
        
        cbMetodo = new JComboBox<>(new String[]{"Congruencial Mixto", "Cuadrados Medios"});
        cbMetodo.setFont(new Font("Inter", Font.PLAIN, 14));
        cbMetodo.setBackground(COLOR_CARD);
        cbMetodo.setForeground(COLOR_NEGRO);
        cbMetodo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        cbMetodo.addActionListener(e -> actualizarPanelParametros());
        
        // Selector de nivel de confianza
        JLabel lblConfianza = new JLabel("Nivel de Confianza:");
        lblConfianza.setFont(new Font("Inter", Font.BOLD, 14));
        lblConfianza.setForeground(COLOR_TEXTO);
        
        cbConfianza = new JComboBox<>(new String[]{
            "99% (α=0.01)", 
            "95% (α=0.05)", 
            "90% (α=0.10)", 
            "85% (α=0.15)", 
            "80% (α=0.20)"
        });
        cbConfianza.setSelectedIndex(1); // 95% por defecto
        cbConfianza.setFont(new Font("Inter", Font.PLAIN, 14));
        cbConfianza.setBackground(COLOR_CARD);
        cbConfianza.setForeground(COLOR_NEGRO);
        cbConfianza.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        panelSelectores.add(lblMetodo);
        panelSelectores.add(cbMetodo);
        panelSelectores.add(Box.createRigidArea(new Dimension(30, 0)));
        panelSelectores.add(lblConfianza);
        panelSelectores.add(cbConfianza);
        
        JPanel panelTitulos = new JPanel();
        panelTitulos.setLayout(new BoxLayout(panelTitulos, BoxLayout.Y_AXIS));
        panelTitulos.setBackground(COLOR_FONDO);
        panelTitulos.add(titulo);
        panelTitulos.add(Box.createRigidArea(new Dimension(0, 5)));
        panelTitulos.add(subtitulo);
        
        panel.add(panelTitulos, BorderLayout.WEST);
        panel.add(panelSelectores, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_FONDO);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 20); // Espacio entre paneles
        
        // Panel izquierdo: Parámetros (20% del ancho)
        gbc.weightx = 0.15;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        panel.add(crearCardParametros(), gbc);
        
        // Panel central: Números (35% del ancho)
        gbc.weightx = 0.30;
        gbc.gridx = 1;
        panel.add(crearCardNumeros(), gbc);
        
        // Panel derecho: Resultados/Pruebas (55% del ancho - el más ancho)
        gbc.weightx = 0.55;
        gbc.insets = new Insets(0, 0, 0, 0); // Sin espacio al final
        gbc.gridx = 2;
        panel.add(crearCardResultados(), gbc);
        
        return panel;
    }
    
    private JPanel crearCardParametros() {
        JPanel card = crearCard("Parámetros de Generación");
        card.setLayout(new BorderLayout(0, 15));
        
        panelParametros = new JPanel();
        panelParametros.setLayout(new BoxLayout(panelParametros, BoxLayout.Y_AXIS));
        panelParametros.setBackground(COLOR_CARD);
        
        actualizarPanelParametros();
        
        JScrollPane scroll = new JScrollPane(panelParametros);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_CARD);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        card.add(scroll, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel crearCardNumeros() {
        JPanel card = crearCard("Números Generados - 2 Conjuntos");
        card.setLayout(new BorderLayout(0, 15));
        
        // Crear un tabbed pane para mostrar los 2 conjuntos
        tabbedPaneNumeros = new JTabbedPane();
        tabbedPaneNumeros.setFont(new Font("Inter", Font.BOLD, 13));
        tabbedPaneNumeros.setBackground(COLOR_CARD);
        tabbedPaneNumeros.setForeground(COLOR_NEGRO);
        
        // Panel para conjunto 1
        JPanel panelConjunto1 = new JPanel(new BorderLayout());
        panelConjunto1.setBackground(COLOR_NEGRO);
        
        JLabel lblConjunto1 = new JLabel("Conjunto 1 (Entradas)");
        lblConjunto1.setFont(new Font("Inter", Font.BOLD, 12));
        lblConjunto1.setForeground(COLOR_TEXTO);
        lblConjunto1.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        tablaNumeros1 = crearTablaNumeros();
        JScrollPane scroll1 = new JScrollPane(tablaNumeros1);
        scroll1.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scroll1.getViewport().setBackground(COLOR_CARD);
        scroll1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        panelConjunto1.add(lblConjunto1, BorderLayout.NORTH);
        panelConjunto1.add(scroll1, BorderLayout.CENTER);
        
        // Panel para conjunto 2
        JPanel panelConjunto2 = new JPanel(new BorderLayout());
        panelConjunto2.setBackground(COLOR_CARD);
        
        JLabel lblConjunto2 = new JLabel("Conjunto 2 (Salidas)");
        lblConjunto2.setFont(new Font("Inter", Font.BOLD, 12));
        lblConjunto2.setForeground(COLOR_TEXTO);
        lblConjunto2.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        tablaNumeros2 = crearTablaNumeros();
        JScrollPane scroll2 = new JScrollPane(tablaNumeros2);
        scroll2.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scroll2.getViewport().setBackground(COLOR_CARD);
        scroll2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        panelConjunto2.add(lblConjunto2, BorderLayout.NORTH);
        panelConjunto2.add(scroll2, BorderLayout.CENTER);
        
        tabbedPaneNumeros.addTab("Conjunto 1", panelConjunto1);
        tabbedPaneNumeros.addTab("Conjunto 2", panelConjunto2);
        
        card.add(tabbedPaneNumeros, BorderLayout.CENTER);
        
        return card;
    }
    
    private JTable crearTablaNumeros() {
        String[] columnas = {"#", "Valor"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        JTable tabla = new JTable(modelo);
        estilizarTabla(tabla);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(0).setMaxWidth(80);
        
        return tabla;
    }
    
    private JPanel crearCardResultados() {
        JPanel card = crearCard("Análisis Estadístico");
        card.setLayout(new BorderLayout(0, 15));
        
        // Indicador de nivel de confianza actual
        JPanel panelInfoConfianza = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfoConfianza.setBackground(COLOR_CARD);
        panelInfoConfianza.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel lblInfoConfianza = new JLabel("Nivel de Confianza Actual: ");
        lblInfoConfianza.setFont(new Font("Inter", Font.BOLD, 12));
        lblInfoConfianza.setForeground(COLOR_TEXTO_SECUNDARIO);
        
        JLabel lblValorConfianza = new JLabel("95%");
        lblValorConfianza.setFont(new Font("Inter", Font.BOLD, 12));
        lblValorConfianza.setForeground(COLOR_PRIMARIO);
        
        panelInfoConfianza.add(lblInfoConfianza);
        panelInfoConfianza.add(lblValorConfianza);
        
        // Panel de pruebas - COMO ESTABA ORIGINALMENTE
        panelPruebas = new JPanel();
        panelPruebas.setLayout(new BoxLayout(panelPruebas, BoxLayout.Y_AXIS));
        panelPruebas.setBackground(COLOR_CARD);
        
        scrollPruebas = new JScrollPane(panelPruebas);
        scrollPruebas.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scrollPruebas.getViewport().setBackground(COLOR_CARD);
        scrollPruebas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // SCROLL SIEMPRE VISIBLE
        scrollPruebas.getVerticalScrollBar().setUnitIncrement(16);
        
        JPanel panelContenedor = new JPanel(new BorderLayout());
        panelContenedor.setBackground(COLOR_CARD);
        panelContenedor.add(panelInfoConfianza, BorderLayout.NORTH);
        panelContenedor.add(scrollPruebas, BorderLayout.CENTER);
        
        card.add(panelContenedor, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel crearCard(String titulo) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Inter", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_TEXTO);
        
        card.add(lblTitulo, BorderLayout.NORTH);
        
        return card;
    }
    
    private void estilizarTabla(JTable tabla) {
        tabla.setFont(new Font("Inter", Font.PLAIN, 13));
        tabla.setBackground(COLOR_CARD);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setGridColor(COLOR_BORDE);
        tabla.setRowHeight(32);
        tabla.setShowVerticalLines(true);
        tabla.setShowHorizontalLines(true);
        tabla.setIntercellSpacing(new Dimension(1, 1));
        
        tabla.getTableHeader().setFont(new Font("Inter", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(COLOR_HOVER);
        tabla.getTableHeader().setForeground(COLOR_NEGRO);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BORDE));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    private void actualizarPanelParametros() {
        panelParametros.removeAll();
        panelParametros.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Siempre agregar el campo N primero
        txtN.setText("100");
        agregarCampoExistente(txtN, "Cantidad de números por conjunto (N)", 
            "Cantidad de valores pseudoaleatorios a generar en CADA conjunto");
        
        
        if (cbMetodo.getSelectedIndex() == 0) {
            // Congruencial Mixto (CON PARÁMETROS QUE FUNCIONAN CON TU MÉTODO)
            txtX0.setText("7");
            txtA.setText("5");
            txtC.setText("3");
            txtM.setText("16");
            
            agregarCampoExistente(txtX0, "Semilla Base (X₀)", "Valor inicial base (cada conjunto tendrá semilla diferente)");
            agregarCampoExistente(txtA, "Multiplicador (a)", "Constante multiplicativa (debe ser < m)");
            agregarCampoExistente(txtC, "Incremento (c)", "Constante aditiva (debe ser < m)");
            agregarCampoExistente(txtM, "Módulo (m)", "Valor máximo del módulo (ej: 16, 128, 256)");
            
            // Ocultar campo semilla del método de cuadrados medios
            txtSemilla.setVisible(false);
        } else {
            // Cuadrados Medios (CON PARÁMETROS QUE FUNCIONAN CON TU MÉTODO)
            txtSemilla.setText("12345");
            agregarCampoExistente(txtSemilla, "Semilla Base", 
                "Número de al menos 3 dígitos (cada conjunto tendrá semilla diferente)");
            
            // Ocultar campos del método mixto
            txtX0.setVisible(false);
            txtA.setVisible(false);
            txtC.setVisible(false);
            txtM.setVisible(false);
        }
        
        panelParametros.add(Box.createRigidArea(new Dimension(0, 10)));
        panelParametros.revalidate();
        panelParametros.repaint();
    }
    
    private void agregarCampoExistente(JTextField txt, String label, String tooltip) {
        JPanel panelCampo = new JPanel(new BorderLayout(0, 8));
        panelCampo.setBackground(COLOR_CARD);
        panelCampo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        panelCampo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Inter", Font.BOLD, 13));
        lbl.setForeground(COLOR_TEXTO);
        
        txt.setFont(new Font("Inter", Font.PLAIN, 14));
        txt.setBackground(COLOR_HOVER);
        txt.setForeground(COLOR_TEXTO);
        txt.setCaretColor(COLOR_TEXTO);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        txt.setToolTipText(tooltip);
        txt.setVisible(true);
        
        panelCampo.add(lbl, BorderLayout.NORTH);
        panelCampo.add(txt, BorderLayout.CENTER);
        
        panelParametros.add(panelCampo);
        panelParametros.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(COLOR_FONDO);
        
        JButton btnGenerar = crearBoton("Generar 2 Conjuntos", COLOR_PRIMARIO, true);
        btnGenerar.addActionListener(e -> generarNumeros());
        
        JButton btnPruebas1 = crearBoton("Pruebas Conjunto 1", COLOR_CONJUNTO1, false);
        btnPruebas1.addActionListener(e -> {
            conjuntoPruebasActual = 0;
            ejecutarPruebas(0);
        });
        
        JButton btnPruebas2 = crearBoton("Pruebas Conjunto 2", COLOR_CONJUNTO2, false);
        btnPruebas2.addActionListener(e -> {
            conjuntoPruebasActual = 1;
            ejecutarPruebas(1);
        });
        
    
        
        JButton btnLimpiar = crearBoton("Limpiar", COLOR_ERROR, false);
        btnLimpiar.addActionListener(e -> limpiar());
        
        JButton btnVolver = crearBoton("Volver al Menú", COLOR_TEXTO_SECUNDARIO, false);
        btnVolver.addActionListener(e -> {
            new MenuPrincipal().setVisible(true);
            dispose();
        });
        
        panel.add(btnGenerar);
        panel.add(btnPruebas1);
        panel.add(btnPruebas2);
        panel.add(btnLimpiar);
        panel.add(btnVolver);
        
        return panel;
    }
    
    private JButton crearBoton(String texto, Color color, boolean esPrimario) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = color;
                if (getModel().isPressed()) {
                    bgColor = color.darker();
                } else if (getModel().isRollover()) {
                    bgColor = esPrimario ? color.brighter() : COLOR_HOVER;
                }
                
                if (esPrimario) {
                    g2.setColor(bgColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else {
                    g2.setColor(COLOR_HOVER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        boton.setFont(new Font("Inter", Font.BOLD, 13));
        boton.setForeground(esPrimario ? Color.WHITE : color);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return boton;
    }
    
    private void generarNumeros() {
        try {
            int n = Integer.parseInt(txtN.getText());
            
            if (n <= 0 || n > 5000) {
                mostrarError("La cantidad debe estar entre 1 y 5,000 por conjunto");
                return;
            }
            
            if (cbMetodo.getSelectedIndex() == 0) {
                // Congruencial Mixto - TU MÉTODO
                int x0 = Integer.parseInt(txtX0.getText());
                int a = Integer.parseInt(txtA.getText());
                int c = Integer.parseInt(txtC.getText());
                int m = Integer.parseInt(txtM.getText());
                
                // Validación básica para tu método
                if (x0 < 0 || x0 >= m) {
                    mostrarError("X₀ debe estar en el rango [0, m-1]");
                    return;
                }
                if (a <= 0 || a >= m) {
                    mostrarError("a debe estar en el rango (0, m)");
                    return;
                }
                if (c < 0 || c >= m) {
                    mostrarError("c debe estar en el rango [0, m-1]");
                    return;
                }
                if (m <= 0) {
                    mostrarError("m debe ser > 0");
                    return;
                }
                
                // Usar la validación original o una simplificada
                String validacion = validarParametrosMixtoSencillo(m, a, c, x0);
                if (!validacion.equals("OK")) {
                    mostrarError("Parámetros inválidos:\n" + validacion);
                    return;
                }
                
                // Generar 2 conjuntos
                numerosGenerados = GeneradorPseudoaleatorios.generarCongruencialMixto(n, x0, a, c, m, 2);
                
            } else {
                // Cuadrados Medios - TU MÉTODO
                int semilla = Integer.parseInt(txtSemilla.getText());
                
                // Validación básica para tu método
                if (semilla <= 0) {
                    mostrarError("La semilla debe ser > 0");
                    return;
                }
                
                int numDigitos = String.valueOf(semilla).length();
                if (numDigitos < 3) {
                    mostrarError("La semilla debe tener al menos 3 dígitos");
                    return;
                }
                
                // Verificar desbordamiento
                long cuadrado = (long) semilla * (long) semilla;
                if (cuadrado < 0) {
                    mostrarError("Semilla demasiado grande, puede causar desbordamiento");
                    return;
                }
                
                // Generar 2 conjuntos
                numerosGenerados = GeneradorPseudoaleatorios.generarCuadradosMedios(n, semilla, 2);
            }
            
            // --- MODIFICACIÓN: GUARDAR EN SINGLETON ---
            if (numerosGenerados != null) {
                SimulacionDatos.getInstancia().setDatosGenerados(
                    numerosGenerados[0],
                    numerosGenerados[1],
                    n
                );
            }
            // ----------------------------------------
            
            // Actualizar ambas tablas
            actualizarTablaNumeros(0, tablaNumeros1);
            actualizarTablaNumeros(1, tablaNumeros2);
            
            mostrarExito("Se generaron 2 conjuntos de " + n + " números cada uno correctamente\n" +
                        "• Conjunto 1: Para datos de entrada\n" +
                        "• Conjunto 2: Para datos de salida\n" +
                        "• ¡Datos guardados en memoria para las siguientes etapas!");
            
        } catch (NumberFormatException ex) {
            mostrarError("Todos los campos deben contener números válidos");
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            mostrarError("Error en la generación: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            mostrarError("Error inesperado: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Validación simplificada para tu método congruencial
     */
    private String validarParametrosMixtoSencillo(int m, int a, int c, int x0) {
        // Validaciones básicas
        if (m <= 0) return "m debe ser > 0";
        if (a <= 0) return "a debe ser > 0";
        if (c < 0) return "c debe ser ≥ 0";
        if (x0 < 0) return "X₀ debe ser ≥ 0";
        if (x0 >= m) return "X₀ debe ser < m";
        if (c >= m) return "c debe ser < m";
        if (a >= m) return "a debe ser < m";
        
        // Para tu método, podemos usar validaciones más tolerantes
        // Solo verificar que sean números válidos
        return "OK";
    }
    
    private void actualizarTablaNumeros(int conjunto, JTable tabla) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        
        if (numerosGenerados != null && numerosGenerados[conjunto] != null) {
            for (int i = 0; i < numerosGenerados[conjunto].size(); i++) {
                modelo.addRow(new Object[]{i + 1, String.format("%.5f", numerosGenerados[conjunto].get(i))});
            }
        }
    }
    
    private void ejecutarPruebas(int conjunto) {
        if (numerosGenerados == null || numerosGenerados[conjunto] == null || numerosGenerados[conjunto].isEmpty()) {
            mostrarError("Primero debes generar números para el conjunto " + (conjunto + 1));
            return;
        }
        
        // Obtener nivel de confianza seleccionado
        nivelConfianza = obtenerNivelConfianzaSeleccionado();
        
        // Actualizar información de nivel de confianza
        actualizarInfoConfianza();
        
        // Ejecutar pruebas y almacenar resultados
        if (conjunto == 0) {
            resultadosPruebasConjunto1[0] = EstadisticasCalculador.pruebaMedia(numerosGenerados[conjunto], nivelConfianza);
            resultadosPruebasConjunto1[1] = EstadisticasCalculador.pruebaVarianza(numerosGenerados[conjunto], nivelConfianza);
            resultadosPruebasConjunto1[2] = EstadisticasCalculador.pruebaUniformidad(numerosGenerados[conjunto], 5, nivelConfianza);
            resultadosPruebasConjunto1[3] = EstadisticasCalculador.pruebaCorridas(numerosGenerados[conjunto], nivelConfianza);
            resultadosPruebasConjunto1[4] = EstadisticasCalculador.pruebaSeries(numerosGenerados[conjunto], nivelConfianza);
            resultadosPruebasConjunto1[5] = EstadisticasCalculador.pruebaPoker(numerosGenerados[conjunto], nivelConfianza);
        } else {
            resultadosPruebasConjunto2[0] = EstadisticasCalculador.pruebaMedia(numerosGenerados[conjunto], nivelConfianza);
            resultadosPruebasConjunto2[1] = EstadisticasCalculador.pruebaVarianza(numerosGenerados[conjunto], nivelConfianza);
            resultadosPruebasConjunto2[2] = EstadisticasCalculador.pruebaUniformidad(numerosGenerados[conjunto], 5, nivelConfianza);
            resultadosPruebasConjunto2[3] = EstadisticasCalculador.pruebaCorridas(numerosGenerados[conjunto], nivelConfianza);
            resultadosPruebasConjunto2[4] = EstadisticasCalculador.pruebaSeries(numerosGenerados[conjunto], nivelConfianza);
            resultadosPruebasConjunto2[5] = EstadisticasCalculador.pruebaPoker(numerosGenerados[conjunto], nivelConfianza);
        }
        
        // Mostrar pruebas del conjunto seleccionado
        mostrarPruebasConjunto(conjunto);
        
        mostrarExito("Pruebas ejecutadas correctamente para el Conjunto " + (conjunto + 1) + 
                     " con nivel de confianza " + String.format("%.1f%%", nivelConfianza * 100));
    }
    
    private void ejecutarPruebasAmbos() {
        if (numerosGenerados == null || numerosGenerados[0] == null || numerosGenerados[0].isEmpty() ||
            numerosGenerados[1] == null || numerosGenerados[1].isEmpty()) {
            mostrarError("Primero debes generar números para ambos conjuntos");
            return;
        }
        
        // Obtener nivel de confianza seleccionado
        nivelConfianza = obtenerNivelConfianzaSeleccionado();
        
        // Ejecutar pruebas para ambos conjuntos
        ejecutarPruebas(0);
        ejecutarPruebas(1);
        
        mostrarExito("Pruebas ejecutadas correctamente para AMBOS conjuntos con nivel de confianza " + 
                     String.format("%.1f%%", nivelConfianza * 100));
    }
    
    private void mostrarPruebasConjunto(int conjunto) {
        panelPruebas.removeAll();
        panelPruebas.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Obtener resultados del conjunto seleccionado
        EstadisticasCalculador.ResultadoPrueba[] resultados;
        Color colorConjunto;
        
        if (conjunto == 0) {
            resultados = resultadosPruebasConjunto1;
            colorConjunto = COLOR_CONJUNTO1;
        } else {
            resultados = resultadosPruebasConjunto2;
            colorConjunto = COLOR_CONJUNTO2;
        }
        
        // Verificar si hay resultados
        if (resultados == null || resultados[0] == null) {
            JLabel lblNoPruebas = new JLabel("No hay pruebas ejecutadas para este conjunto");
            lblNoPruebas.setFont(new Font("Inter", Font.BOLD, 14));
            lblNoPruebas.setForeground(colorConjunto);
            lblNoPruebas.setHorizontalAlignment(SwingConstants.CENTER);
            panelPruebas.add(lblNoPruebas);
        } else {
            // Agregar cada prueba - EXACTAMENTE COMO ESTABA ORIGINALMENTE
            String[] nombresPruebas = {"Media", "Varianza", "Uniformidad (Chi-Cuadrado)", 
                                      "Corridas (Independencia)", "Series", "Poker"};
            
            for (int i = 0; i < resultados.length; i++) {
                if (resultados[i] != null) {
                    agregarPruebaConGrafica(nombresPruebas[i], resultados[i], colorConjunto);
                }
            }
        }
        
        panelPruebas.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPruebas.revalidate();
        panelPruebas.repaint();
        
        // Asegurar que el scroll esté visible
        scrollPruebas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }
    
    private double obtenerNivelConfianzaSeleccionado() {
        String seleccionado = (String) cbConfianza.getSelectedItem();
        if (seleccionado.contains("99%")) return 0.99;
        if (seleccionado.contains("95%")) return 0.95;
        if (seleccionado.contains("90%")) return 0.90;
        if (seleccionado.contains("85%")) return 0.85;
        if (seleccionado.contains("80%")) return 0.80;
        return 0.95; // Valor por defecto
    }
    
    private void actualizarInfoConfianza() {
        // Buscar y actualizar el label en el panel de resultados
        Component[] componentes = panelPruebas.getParent().getParent().getParent().getComponents();
        for (Component comp : componentes) {
            if (comp instanceof JPanel) {
                Component[] hijos = ((JPanel)comp).getComponents();
                for (Component hijo : hijos) {
                    if (hijo instanceof JLabel) {
                        JLabel label = (JLabel) hijo;
                        if (label.getText() != null && label.getText().contains("Nivel de Confianza Actual")) {
                            // Encontrar y actualizar el label del valor
                            JPanel parentPanel = (JPanel) hijo.getParent();
                            for (Component hermano : parentPanel.getComponents()) {
                                if (hermano instanceof JLabel && hermano != hijo) {
                                    JLabel valorLabel = (JLabel) hermano;
                                    valorLabel.setText(String.format("%.1f%%", nivelConfianza * 100));
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void agregarPruebaConGrafica(String nombre, EstadisticasCalculador.ResultadoPrueba resultado, Color colorConjunto) {
        JPanel panelPrueba = new JPanel(new BorderLayout(15, 15));
        panelPrueba.setBackground(COLOR_HOVER);
        panelPrueba.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panelPrueba.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        // Panel izquierdo: Detalles de la prueba
        JPanel panelIzquierdo = new JPanel(new BorderLayout(12, 12));
        panelIzquierdo.setBackground(COLOR_HOVER);
        
        // Encabezado con resultado
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_HOVER);
        
        JLabel lblNombre = new JLabel("Prueba de " + nombre);
        lblNombre.setFont(new Font("Inter", Font.BOLD, 14));
        lblNombre.setForeground(COLOR_TEXTO);
        
        JLabel lblResultado = new JLabel(resultado.mensaje);
        lblResultado.setFont(new Font("Inter", Font.BOLD, 13));
        lblResultado.setForeground(resultado.pasa ? COLOR_EXITO : COLOR_ERROR);
        
        JLabel lblIcono = new JLabel(resultado.pasa ? "✓" : "✗");
        lblIcono.setFont(new Font("Inter", Font.BOLD, 20));
        lblIcono.setForeground(resultado.pasa ? COLOR_EXITO : COLOR_ERROR);
        
        JLabel lblConfianza = new JLabel(String.format("(Confianza: %.1f%%)", resultado.nivelConfianza * 100));
        lblConfianza.setFont(new Font("Inter", Font.PLAIN, 10));
        lblConfianza.setForeground(COLOR_TEXTO_SECUNDARIO);
        
        JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelDerecha.setBackground(COLOR_HOVER);
        panelDerecha.add(lblConfianza);
        panelDerecha.add(lblIcono);
        
        header.add(lblNombre, BorderLayout.WEST);
        header.add(lblResultado, BorderLayout.CENTER);
        header.add(panelDerecha, BorderLayout.EAST);
        
        // Detalles de la prueba
        JPanel detalles = new JPanel();
        detalles.setLayout(new BoxLayout(detalles, BoxLayout.Y_AXIS));
        detalles.setBackground(COLOR_HOVER);
        
        DecimalFormat df = new DecimalFormat("#.####");
        
        if (resultado.datosAdicionales != null && resultado.datosAdicionales.length > 0) {
            if (nombre.equals("Media") || nombre.equals("Varianza")) {
                detalles.add(crearDetalle("Valor calculado", df.format(resultado.datosAdicionales[0]), colorConjunto));
                detalles.add(crearDetalle("Límite inferior", df.format(resultado.datosAdicionales[1]), colorConjunto));
                detalles.add(crearDetalle("Límite superior", df.format(resultado.datosAdicionales[2]), colorConjunto));
                detalles.add(crearDetalle("Valor Z crítico", df.format(resultado.datosAdicionales[3]), colorConjunto));
                detalles.add(crearDetalle("Nivel de significancia (α)", df.format(1 - resultado.nivelConfianza), colorConjunto));
            } else if (nombre.contains("Corridas")) {
                detalles.add(crearDetalle("Corridas observadas", String.valueOf((int)resultado.datosAdicionales[0]), colorConjunto));
                detalles.add(crearDetalle("Corridas esperadas", df.format(resultado.datosAdicionales[1]), colorConjunto));
                detalles.add(crearDetalle("Estadístico Z", df.format(resultado.estadistico), colorConjunto));
                detalles.add(crearDetalle("Valor Z crítico", df.format(resultado.datosAdicionales[3]), colorConjunto));
                detalles.add(crearDetalle("Nivel de significancia (α)", df.format(1 - resultado.nivelConfianza), colorConjunto));
            }
        } else {
            detalles.add(crearDetalle("Estadístico χ²", df.format(resultado.estadistico), colorConjunto));
            if (resultado.valorCritico > 0) {
                detalles.add(crearDetalle("Valor crítico (α=" + (1-resultado.nivelConfianza) + ")", 
                                          df.format(resultado.valorCritico), colorConjunto));
                detalles.add(crearDetalle("Grados de libertad", String.valueOf(resultado.gradosLibertad), colorConjunto));
                detalles.add(crearDetalle("Nivel de significancia (α)", df.format(1 - resultado.nivelConfianza), colorConjunto));
            }
        }
        
        panelIzquierdo.add(header, BorderLayout.NORTH);
        panelIzquierdo.add(detalles, BorderLayout.CENTER);
        
        // Panel derecho: Gráfica de distribución normal
        JPanel panelGrafica = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGraficaDistribucion(g, resultado, colorConjunto);
            }
        };
        panelGrafica.setBackground(COLOR_HOVER);
        panelGrafica.setPreferredSize(new Dimension(300, 200));
        panelGrafica.setMinimumSize(new Dimension(250, 180));
        
        panelPrueba.add(panelIzquierdo, BorderLayout.CENTER);
        panelPrueba.add(panelGrafica, BorderLayout.EAST);
        
        panelPruebas.add(panelPrueba);
        panelPruebas.add(Box.createRigidArea(new Dimension(0, 15)));
    }
    
    private void dibujarGraficaDistribucion(Graphics g, EstadisticasCalculador.ResultadoPrueba resultado, Color colorConjunto) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = 280;
        int height = 180;
        int margin = 20;
        
        // Área de dibujo
        int graphWidth = width - 2 * margin;
        int graphHeight = height - 2 * margin;
        
        // Calcular valores críticos según el nivel de confianza
        double alpha = 1 - resultado.nivelConfianza;
        double alphaMitad = alpha / 2;
        
        // Para pruebas Z (Media, Varianza, Corridas)
        double zCritico = 0;
        double estadisticoZ = 0;
        
        if (resultado.datosAdicionales != null && resultado.datosAdicionales.length >= 4) {
            zCritico = resultado.datosAdicionales[3]; // Valor Z crítico
            estadisticoZ = Math.abs(resultado.estadistico); // Estadístico Z absoluto
        }
        
        // Dibujar eje X
        g2.setColor(COLOR_TEXTO_SECUNDARIO);
        g2.drawLine(margin, height - margin, width - margin, height - margin);
        
        // Marcar puntos en el eje X
        double[] puntosX = {-3, -2, -1, 0, 1, 2, 3};
        for (double punto : puntosX) {
            int x = margin + (int)((punto + 3) * graphWidth / 6);
            g2.drawLine(x, height - margin - 3, x, height - margin + 3);
            g2.setFont(new Font("Inter", Font.PLAIN, 9));
            String label = String.format("%.0f", punto);
            int labelWidth = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, x - labelWidth/2, height - margin + 15);
        }
        
        // Dibujar curva de distribución normal
        g2.setColor(colorConjunto);
        g2.setStroke(new BasicStroke(2));
        
        Path2D curva = new Path2D.Double();
        boolean primero = true;
        for (int i = 0; i <= graphWidth; i++) {
            double x = -3 + (6.0 * i / graphWidth);
            double y = funcionDensidadNormal(x);
            int px = margin + i;
            int py = height - margin - (int)(y * graphHeight * 1.5);
            
            if (primero) {
                curva.moveTo(px, py);
                primero = false;
            } else {
                curva.lineTo(px, py);
            }
        }
        g2.draw(curva);
        
        // Dibujar áreas de aceptación y rechazo
        if (zCritico > 0) {
            // Calcular posiciones de los límites críticos
            int xLimiteInferior = margin + (int)((-zCritico + 3) * graphWidth / 6);
            int xLimiteSuperior = margin + (int)((zCritico + 3) * graphWidth / 6);
            
            // Área de aceptación (central)
            Color colorAceptacion = new Color(colorConjunto.getRed(), colorConjunto.getGreen(), colorConjunto.getBlue(), 50);
            g2.setColor(colorAceptacion);
            g2.fillRect(xLimiteInferior, margin, xLimiteSuperior - xLimiteInferior, graphHeight);
            
            // Áreas de rechazo (colas)
            Color colorRechazo = new Color(COLOR_ERROR.getRed(), COLOR_ERROR.getGreen(), COLOR_ERROR.getBlue(), 50);
            g2.setColor(colorRechazo);
            g2.fillRect(margin, margin, xLimiteInferior - margin, graphHeight);
            g2.fillRect(xLimiteSuperior, margin, width - margin - xLimiteSuperior, graphHeight);
            
            // Líneas de límites críticos
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
            g2.drawLine(xLimiteInferior, margin, xLimiteInferior, height - margin);
            g2.drawLine(xLimiteSuperior, margin, xLimiteSuperior, height - margin);
            
            // Etiquetas de porcentajes
            g2.setFont(new Font("Inter", Font.BOLD, 10));
            g2.setColor(COLOR_TEXTO);
            
            // Área de aceptación (centro)
            String aceptacion = String.format("%.1f%%", resultado.nivelConfianza * 100);
            int aceptacionWidth = g2.getFontMetrics().stringWidth(aceptacion);
            int aceptacionX = xLimiteInferior + (xLimiteSuperior - xLimiteInferior) / 2 - aceptacionWidth/2;
            g2.drawString(aceptacion, aceptacionX, height - margin - 30);
            
            // Áreas de rechazo (colas)
            String rechazo = String.format("%.1f%%", alphaMitad * 100);
            int rechazoWidth = g2.getFontMetrics().stringWidth(rechazo);
            
            // Cola izquierda
            g2.drawString(rechazo, margin + 5, height - margin - 30);
            
            // Cola derecha
            g2.drawString(rechazo, width - margin - rechazoWidth - 5, height - margin - 30);
            
            // Dibujar línea del estadístico Z si existe
            if (estadisticoZ > 0) {
                int xEstadistico = margin + (int)((estadisticoZ + 3) * graphWidth / 6);
                g2.setColor(COLOR_LINEA);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(xEstadistico, margin, xEstadistico, height - margin);
                
                // Indicador del valor del estadístico
                g2.setFont(new Font("Inter", Font.BOLD, 9));
                String estadisticoStr = String.format("Z=%.2f", resultado.estadistico);
                int estadisticoWidth = g2.getFontMetrics().stringWidth(estadisticoStr);
                g2.drawString(estadisticoStr, xEstadistico - estadisticoWidth/2, margin + 15);
                
                // Punto en la curva
                double yCurva = funcionDensidadNormal(estadisticoZ);
                int pyCurva = height - margin - (int)(yCurva * graphHeight * 1.5);
                g2.fillOval(xEstadistico - 3, pyCurva - 3, 6, 6);
            }
        }
        
        // Título de la gráfica
        g2.setFont(new Font("Inter", Font.BOLD, 11));
        g2.setColor(colorConjunto);
        String titulo = "Distribución Normal - Nivel de Confianza";
        int tituloWidth = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, margin + (graphWidth - tituloWidth)/2, margin - 5);
    }
    
    private double funcionDensidadNormal(double x) {
        // Función de densidad de probabilidad normal estándar
        return (1.0 / Math.sqrt(2 * Math.PI)) * Math.exp(-0.5 * x * x);
    }
    
    private JPanel crearDetalle(String etiqueta, String valor, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_HOVER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JLabel lblEtiqueta = new JLabel(etiqueta + ":");
        lblEtiqueta.setFont(new Font("Inter", Font.PLAIN, 12));
        lblEtiqueta.setForeground(COLOR_TEXTO_SECUNDARIO);
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("JetBrains Mono", Font.BOLD, 12));
        lblValor.setForeground(color);
        
        panel.add(lblEtiqueta, BorderLayout.WEST);
        panel.add(lblValor, BorderLayout.EAST);
        
        return panel;
    }
    
    private void limpiar() {
        // Limpiar tablas
        DefaultTableModel modelo1 = (DefaultTableModel) tablaNumeros1.getModel();
        modelo1.setRowCount(0);
        
        DefaultTableModel modelo2 = (DefaultTableModel) tablaNumeros2.getModel();
        modelo2.setRowCount(0);
        
        // Limpiar panel de pruebas
        panelPruebas.removeAll();
        panelPruebas.revalidate();
        panelPruebas.repaint();
        
        // Limpiar resultados
        for (int i = 0; i < resultadosPruebasConjunto1.length; i++) {
            resultadosPruebasConjunto1[i] = null;
            resultadosPruebasConjunto2[i] = null;
        }
        
        // Limpiar números generados
        numerosGenerados[0] = new ArrayList<>();
        numerosGenerados[1] = new ArrayList<>();
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            Pseudoaleatorios ventana = new Pseudoaleatorios();
            ventana.setVisible(true);
        });
    }
}