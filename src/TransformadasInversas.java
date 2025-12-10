import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private JLabel lblCantidadDatos; 
    private JLabel lblInfoMetodo;
    private DefaultTableModel modeloTabla;
    private PanelGraficaHistograma panelGrafica;
    
    // Datos
    private List<Double> datosSimulados;

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

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
        
        actualizarContadorDatos(); 
        actualizarInfoMetodo(); // Mostrar info inicial
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
        cbConjuntoRi.addItemListener(e -> {
            actualizarContadorDatos();
            actualizarInfoMetodo();
        }); 
        contenido.add(cbConjuntoRi);
        contenido.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Indicador de datos
        lblCantidadDatos = new JLabel("Cargando datos...");
        lblCantidadDatos.setFont(new Font("Inter", Font.ITALIC, 12));
        lblCantidadDatos.setForeground(COLOR_GRAFICA_BARRA); 
        contenido.add(lblCantidadDatos);
        contenido.add(Box.createRigidArea(new Dimension(0, 25)));

        // Información de la lógica aplicada (Sin inputs manuales)
        JLabel lblLogica = crearLabel("");
        lblLogica.setForeground(COLOR_PRIMARIO);
        contenido.add(lblLogica);
        contenido.add(Box.createRigidArea(new Dimension(0, 10)));

        lblInfoMetodo = new JLabel();
        lblInfoMetodo.setFont(new Font("Inter", Font.PLAIN, 13));
        lblInfoMetodo.setForeground(COLOR_TEXTO);
        // Permitir múltiples líneas en el label
        lblInfoMetodo.setText("<html>Se aplicará la distribución empírica<br>correspondiente a este conjunto.</html>");
        contenido.add(lblInfoMetodo);
        
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
        lblCantidadDatos.setText("✓ Datos disponibles: " + n);
    }

    private void actualizarInfoMetodo() {
        if (cbConjuntoRi.getSelectedIndex() == 0) {
            // Conjunto 1: Entradas -> Llegadas
            lblInfoMetodo.setText("");
        } else {
            // Conjunto 2: Salidas -> Servicio
            lblInfoMetodo.setText("");
        }
    }

    private JPanel crearPanelTabla() {
        JPanel card = new JPanel(new BorderLayout());
        estilizarCard(card);
        
        JLabel lblTitulo = new JLabel("Resultados Transformados");
        lblTitulo.setFont(new Font("Inter", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        String[] columnas = {"#", "Ri (Origen)", "Xi (Calculado)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaResultados = new JTable(modeloTabla);
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

    // --- LÓGICA PRINCIPAL AUTOMATIZADA ---

    private void generarVariables() {
        if (!SimulacionDatos.getInstancia().hayDatos()) {
            JOptionPane.showMessageDialog(this, "No hay datos Ri cargados.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // 1. Obtener lista seleccionada
            List<Double> listaRi;
            boolean esConjunto1 = (cbConjuntoRi.getSelectedIndex() == 0);
            
            if (esConjunto1) 
                listaRi = SimulacionDatos.getInstancia().getConjunto1RiEn();
            else 
                listaRi = SimulacionDatos.getInstancia().getConjunto2RiSn();
                
            int n = listaRi.size(); 
            limpiarDatos();
            
            // 2. Transformar TODOS los datos automáticamente
            for (int i = 0; i < n; i++) {
                double ri = listaRi.get(i);
                double xi;
                
                // LÓGICA EMPÍRICA AUTOMÁTICA
                if (esConjunto1) {
                    // Lógica para LLEGADAS (Conjunto 1)
                    if (ri < 0.151) xi = 2.0;
                    else if (ri < 0.930) xi = 3.0;
                    else xi = 4.0;
                } else {
                    // Lógica para SERVICIO (Conjunto 2)
                    if (ri < 0.195) xi = 2.0;
                    else if (ri < 0.954) xi = 3.0;
                    else xi = 4.0;
                }
                
                datosSimulados.add(xi);
                
                // Mostrar en tabla
                if(i < 5000) modeloTabla.addRow(new Object[]{i+1, String.format("%.5f", ri), String.format("%.4f", xi)});
            }
            
            String nombreDist = esConjunto1 ? "Empírica (Llegadas)" : "Empírica (Servicio)";
            panelGrafica.setDatos(datosSimulados, nombreDist);
            
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al transformar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarDatos() {
        datosSimulados.clear();
        modeloTabla.setRowCount(0); 
        panelGrafica.limpiar();
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
    
    // NOTA: Se eliminó el método crearInput porque ya no hay inputs manuales
    
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