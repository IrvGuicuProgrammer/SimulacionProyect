import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collections;
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
    private final Color COLOR_GRAFICA_BARRA = new Color(16, 185, 129); // Verde

    // Componentes
    private JComboBox<String> cbConjuntoRi;
    private JComboBox<String> cbDistribucion;
    private JLabel lblCantidadDatos; 
    private JTextField txtParam1, txtParam2, txtParam3;
    private JLabel lblParam1, lblParam2, lblParam3;
    private JPanel panelInputsDinamicos;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private PanelGraficaHistograma panelGrafica;
    
    // Datos
    private List<Double> datosSimulados;
    private List<Double> datosRiUtilizados;

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

        datosSimulados = new ArrayList<>();
        datosRiUtilizados = new ArrayList<>();

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
        
        actualizarCamposInputs();
        actualizarContadorDatos(); 
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel titulo = new JLabel("Método de la Transformada Inversa");
        titulo.setFont(new Font("Inter", Font.BOLD, 24));
        titulo.setForeground(COLOR_TEXTO);

        JLabel subtitulo = new JLabel("Simulación automática basada en los números pseudoaleatorios generados previamente");
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

        // Configuración (Izquierda)
        gbc.weightx = 0.25; gbc.weighty = 1.0; gbc.gridx = 0;
        panel.add(crearPanelConfiguracion(), gbc);

        // Tabla (Centro)
        gbc.weightx = 0.30; gbc.gridx = 1;
        panel.add(crearPanelTabla(), gbc);

        // Gráfica (Derecha)
        gbc.weightx = 0.45; gbc.insets = new Insets(0, 0, 0, 0); gbc.gridx = 2;
        panel.add(crearPanelGrafica(), gbc);

        return panel;
    }

    private JPanel crearPanelConfiguracion() {
        JPanel card = new JPanel(new BorderLayout());
        estilizarCard(card);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(COLOR_CARD);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Selector de Conjunto
        contenido.add(crearLabel("Fuente de Datos (Ri):"));
        cbConjuntoRi = new JComboBox<>(new String[]{"Conjunto 1 (Entradas)", "Conjunto 2 (Salidas)"});
        estilizarCombo(cbConjuntoRi);
        cbConjuntoRi.addItemListener(e -> actualizarContadorDatos()); 
        contenido.add(cbConjuntoRi);
        contenido.add(Box.createRigidArea(new Dimension(0, 10)));
        
        lblCantidadDatos = new JLabel("Cargando datos...");
        lblCantidadDatos.setFont(new Font("Inter", Font.ITALIC, 12));
        lblCantidadDatos.setForeground(COLOR_GRAFICA_BARRA); 
        contenido.add(lblCantidadDatos);
        contenido.add(Box.createRigidArea(new Dimension(0, 20)));

        // Selector Distribución con opciones EMPÍRICAS agregadas
        contenido.add(crearLabel("Transformar a Distribución:"));
        cbDistribucion = new JComboBox<>(new String[]{
            "Empírica Llegadas (Excel)",
            "Empírica Servicio (Excel)",
            "Uniforme (A, B)", 
            "Exponencial (Media)", 
            "Normal (Media, Desv.Est)", 
            "Triangular (Min, Max, Moda)"
        });
        estilizarCombo(cbDistribucion);
        cbDistribucion.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) actualizarCamposInputs();
        });
        contenido.add(cbDistribucion);
        contenido.add(Box.createRigidArea(new Dimension(0, 20)));

        // Parámetros Dinámicos
        panelInputsDinamicos = new JPanel();
        panelInputsDinamicos.setLayout(new BoxLayout(panelInputsDinamicos, BoxLayout.Y_AXIS));
        panelInputsDinamicos.setBackground(COLOR_CARD);
        
        lblParam1 = crearLabel("Parámetro 1:"); txtParam1 = crearInput("");
        lblParam2 = crearLabel("Parámetro 2:"); txtParam2 = crearInput("");
        lblParam3 = crearLabel("Parámetro 3:"); txtParam3 = crearInput("");
        
        contenido.add(panelInputsDinamicos);
        contenido.add(Box.createVerticalGlue());

        // Botón
        JButton btnGenerar = crearBoton("Transformar Datos", COLOR_PRIMARIO, true);
        btnGenerar.addActionListener(e -> generarVariables());
        
        card.add(contenido, BorderLayout.CENTER);
        card.add(btnGenerar, BorderLayout.SOUTH);

        return card;
    }
    
    private void actualizarContadorDatos() {
        if (!SimulacionDatos.getInstancia().hayDatos()) {
            lblCantidadDatos.setText("Sin datos disponibles (N=0)");
            return;
        }
        
        int n;
        if (cbConjuntoRi.getSelectedIndex() == 0) {
            n = SimulacionDatos.getInstancia().getConjunto1RiEn().size();
        } else {
            n = SimulacionDatos.getInstancia().getConjunto2RiSn().size();
        }
        lblCantidadDatos.setText("✓ Se procesarán " + n + " registros automáticamente");
    }

    private JPanel crearPanelTabla() {
        JPanel card = new JPanel(new BorderLayout());
        estilizarCard(card);
        
        JLabel lblTitulo = new JLabel("Resultados Transformados");
        lblTitulo.setFont(new Font("Inter", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        String[] columnas = {"#", "Ri (Origen)", "Xi (Transformado)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaResultados = new JTable(modeloTabla);
        estilizarTabla(tablaResultados);
        
        JScrollPane scroll = new JScrollPane(tablaResultados);
        scroll.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
        scroll.getViewport().setBackground(COLOR_CARD);
        
        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearPanelGrafica() {
        JPanel card = new JPanel(new BorderLayout());
        estilizarCard(card);
        JLabel lblTitulo = new JLabel("Histograma Resultante");
        lblTitulo.setFont(new Font("Inter", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        panelGrafica = new PanelGraficaHistograma();
        panelGrafica.setBackground(COLOR_CARD);
        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(panelGrafica, BorderLayout.CENTER);
        return card;
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

    // --- LÓGICA ---
    private void actualizarCamposInputs() {
        panelInputsDinamicos.removeAll();
        String sel = (String) cbDistribucion.getSelectedItem();
        
        if (sel.contains("Empírica")) {
             JLabel lblInfo = crearLabel("<html>Probabilidades fijas cargadas<br>desde el archivo Excel.</html>");
             lblInfo.setForeground(COLOR_GRAFICA_BARRA);
             panelInputsDinamicos.add(lblInfo);
        } else if (sel.contains("Uniforme")) {
            agregarCampo(lblParam1, txtParam1, "Límite A (Mín):", "0");
            agregarCampo(lblParam2, txtParam2, "Límite B (Máx):", "10");
        } else if (sel.contains("Exponencial")) {
            agregarCampo(lblParam1, txtParam1, "Media (μ):", "5");
        } else if (sel.contains("Normal")) {
            agregarCampo(lblParam1, txtParam1, "Media (μ):", "100");
            agregarCampo(lblParam2, txtParam2, "Desv. Est (σ):", "15");
        } else if (sel.contains("Triangular")) {
            agregarCampo(lblParam1, txtParam1, "Mín (a):", "10");
            agregarCampo(lblParam2, txtParam2, "Máx (b):", "30");
            agregarCampo(lblParam3, txtParam3, "Moda (c):", "25");
        }
        panelInputsDinamicos.revalidate(); panelInputsDinamicos.repaint();
    }
    
    private void agregarCampo(JLabel lbl, JTextField txt, String t, String v) {
        lbl.setText(t); txt.setText(v);
        panelInputsDinamicos.add(lbl); panelInputsDinamicos.add(txt);
        panelInputsDinamicos.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private void generarVariables() {
        if (!SimulacionDatos.getInstancia().hayDatos()) {
            JOptionPane.showMessageDialog(this, "No hay datos Ri cargados.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            List<Double> listaRi;
            if (cbConjuntoRi.getSelectedIndex() == 0) 
                listaRi = SimulacionDatos.getInstancia().getConjunto1RiEn();
            else 
                listaRi = SimulacionDatos.getInstancia().getConjunto2RiSn();
                
            int n = listaRi.size(); 
            
            limpiarDatos();
            String sel = (String) cbDistribucion.getSelectedItem();
            SimulacionDatos math = SimulacionDatos.getInstancia();
            
            double p1=0, p2=0, p3=0;
            // Solo parseamos si NO es empírica
            if (!sel.contains("Empírica")) {
                if(!txtParam1.getText().isEmpty()) p1 = Double.parseDouble(txtParam1.getText());
                if(txtParam2.isShowing()) p2 = Double.parseDouble(txtParam2.getText());
                if(txtParam3.isShowing()) p3 = Double.parseDouble(txtParam3.getText());
            }

            // 3. Transformar TODOS los datos
            for (int i = 0; i < n; i++) {
                double ri = listaRi.get(i);
                double xi = 0;
                
                if (sel.contains("Empírica Llegadas")) {
                    // X=3 (<0.151), X=4 (<0.930), X=5 (Resto)
                    if (ri < 0.151) xi = 3.0;
                    else if (ri < 0.930) xi = 4.0;
                    else xi = 5.0;
                } else if (sel.contains("Empírica Servicio")) {
                    // X=2 (<0.195), X=3 (<0.954), X=4 (Resto)
                    if (ri < 0.195) xi = 2.0;
                    else if (ri < 0.954) xi = 3.0;
                    else xi = 4.0;
                } else if (sel.contains("Uniforme")) {
                    xi = math.calcularUniforme(ri, p1, p2); 
                } else if (sel.contains("Exponencial")) {
                    xi = math.calcularExponencial(ri, p1);
                } else if (sel.contains("Normal")) {
                    xi = math.calcularNormal(ri, p1, p2);
                } else if (sel.contains("Triangular")) {
                    double a=p1, b=p2, c=p3;
                    double corte = (c-a)/(b-a);
                    if (ri < corte) xi = a + Math.sqrt(ri*(b-a)*(c-a));
                    else xi = b - Math.sqrt((1-ri)*(b-a)*(b-c));
                }
                
                datosSimulados.add(xi);
                datosRiUtilizados.add(ri);
                
                if(i < 5000) modeloTabla.addRow(new Object[]{i+1, String.format("%.5f", ri), String.format("%.4f", xi)});
            }
            
            panelGrafica.setDatos(datosSimulados, sel);
            
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Verifique los parámetros numéricos de la distribución.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarDatos() {
        datosSimulados.clear(); datosRiUtilizados.clear();
        modeloTabla.setRowCount(0); panelGrafica.limpiar();
    }

    // --- Helpers UI (Estilos) ---
    private void estilizarCard(JPanel p) {
        p.setBackground(COLOR_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE,1),BorderFactory.createEmptyBorder(0,0,0,0)));
    }
    private JLabel crearLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Inter",Font.BOLD,12));
        l.setForeground(COLOR_TEXTO_SECUNDARIO); l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }
    private JTextField crearInput(String t) {
        JTextField x = new JTextField(t); x.setFont(new Font("Inter",Font.PLAIN,14));
        x.setBackground(COLOR_HOVER); x.setForeground(COLOR_TEXTO); x.setCaretColor(COLOR_TEXTO);
        x.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE),BorderFactory.createEmptyBorder(8,10,8,10)));
        x.setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); x.setAlignmentX(Component.LEFT_ALIGNMENT); return x;
    }
    private void estilizarCombo(JComboBox<String> c) {
        c.setFont(new Font("Inter",Font.PLAIN,14)); c.setBackground(COLOR_HOVER); c.setForeground(Color.WHITE);
        c.setBorder(BorderFactory.createLineBorder(COLOR_BORDE)); c.setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); c.setAlignmentX(Component.LEFT_ALIGNMENT);
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

    // --- Panel Histograma ---
    private class PanelGraficaHistograma extends JPanel {
        private List<Double> d; private String tit; private int nInt;
        public void setDatos(List<Double> datos, String t) {
            this.d = datos; this.tit = t;
            this.nInt = (int)Math.sqrt(datos.size()); if(nInt<5)nInt=5; if(nInt>50)nInt=50;
            repaint();
        }
        public void limpiar() { this.d=null; repaint(); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(d==null||d.isEmpty()) { g.setColor(COLOR_TEXTO_SECUNDARIO); g.drawString("Sin datos transformados", 20, 30); return; }
            Graphics2D g2 = (Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight(), pad=40;
            double min=Collections.min(d), max=Collections.max(d); if(min==max)max+=1;
            int[] f = new int[nInt]; double anc=(max-min)/nInt; int maxF=0;
            for(double v:d) { int i=(int)((v-min)/anc); if(i>=nInt)i=nInt-1; f[i]++; if(f[i]>maxF)maxF=f[i]; }
            g2.setColor(COLOR_TEXTO_SECUNDARIO); g2.drawLine(pad,h-pad,w-pad,h-pad); g2.drawLine(pad,pad,pad,h-pad);
            double bw=(double)(w-2*pad)/nInt;
            for(int i=0;i<nInt;i++) {
                double bh=((double)f[i]/maxF)*(h-2*pad);
                int x=pad+(int)(i*bw), y=h-pad-(int)bh;
                g2.setColor(COLOR_GRAFICA_BARRA); g2.fillRect(x,y,(int)bw-1,(int)bh);
                g2.setColor(COLOR_GRAFICA_BARRA.darker()); g2.drawRect(x,y,(int)bw-1,(int)bh);
            }
            g2.setColor(COLOR_TEXTO); g2.setFont(new Font("Inter",Font.PLAIN,10));
            g2.drawString(String.format("%.2f",min),pad,h-pad+15); g2.drawString(String.format("%.2f",max),w-pad-40,h-pad+15);
            g2.setFont(new Font("Inter",Font.BOLD,12)); g2.drawString(tit!=null?tit:"Distribución",pad+10,pad-10);
        }
    }
}