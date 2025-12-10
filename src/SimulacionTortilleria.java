import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimulacionTortilleria extends JFrame {

    // Colores (Tema Oscuro)
    private final Color COLOR_FONDO = new Color(15, 15, 20);
    private final Color COLOR_CARD = new Color(30, 30, 40);
    private final Color COLOR_TEXTO = new Color(240, 240, 245);
    private final Color COLOR_BORDE = new Color(50, 55, 65);
    private final Color COLOR_VERDE = new Color(16, 185, 129);
    private final Color COLOR_HEADER = new Color(25, 25, 35);

    // Componentes UI
    private JTextField txtHoraInicio;
    
    // Tabla y Gráfica
    private JTabbedPane tabbedPaneResultados;
    private JTable tablaSimulacion;
    private DefaultTableModel modeloTabla;
    private PanelGraficaComparativa panelGraficaComparativa;

    // Datos para gráfica
    private List<double[]> ocupacionReal = new ArrayList<>();
    private List<double[]> ocupacionSimulada = new ArrayList<>();

    private DecimalFormat dfRi = new DecimalFormat("0.####");
    private DecimalFormat dfTime = new DecimalFormat("0.##");

    public SimulacionTortilleria() {
        setTitle("Simulación Tabular - Tortillería La Providencia");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);

        // Validar que existan datos antes de iniciar
        if (!SimulacionDatos.getInstancia().hayDatos()) {
            JOptionPane.showMessageDialog(this, "¡Alerta! Faltan datos pseudoaleatorios.", "Error", JOptionPane.WARNING_MESSAGE);
        }

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelSuperior() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel t = new JLabel("Simulación del Flujo de Clientes");
        t.setFont(new Font("SansSerif", Font.BOLD, 22)); 
        t.setForeground(COLOR_TEXTO);
        p.add(t, BorderLayout.WEST);

        JPanel ctrls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ctrls.setBackground(COLOR_FONDO);
        
        JButton btnCsv = new JButton("Cargar CSV Reales");
        // Estilo simple para botón secundario
        btnCsv.setBackground(COLOR_CARD);
        btnCsv.setForeground(COLOR_TEXTO);
        btnCsv.setFocusPainted(false);
        btnCsv.addActionListener(e -> cargarDatosCSV());
        
        JLabel lH = new JLabel("Hora Inicio:"); 
        lH.setForeground(Color.GRAY);
        txtHoraInicio = new JTextField("11:00", 5);
        
        ctrls.add(btnCsv); 
        ctrls.add(lH); 
        ctrls.add(txtHoraInicio);
        p.add(ctrls, BorderLayout.EAST);
        return p;
    }

    private JPanel crearPanelCentral() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COLOR_FONDO);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH; 
        g.insets = new Insets(5, 5, 5, 5);
        
        // --- Panel de Información de Parámetros Fijos ---
        JPanel panelInfo = new JPanel(new GridLayout(1, 2, 10, 10));
        panelInfo.setBackground(COLOR_FONDO);
        
        panelInfo.add(crearPanelInfoFija("Tiempos de Llegada", "Empírica Fija (Factor Hora Pico)", COLOR_VERDE));
        panelInfo.add(crearPanelInfoFija("Tiempos de Servicio", "Empírica Fija", COLOR_VERDE));
        
        g.gridx=0; g.gridy=0; g.gridwidth=2; g.weightx=1.0;
        p.add(panelInfo, g);

        // --- Botón de Ejecutar (NEGRO Y RESALTANTE) ---
        g.gridx=0; g.gridy=1; g.gridwidth=2;
        
        JButton btn = new JButton("EJECUTAR SIMULACIÓN (Con Factor Hora Pico)");
        btn.setBackground(Color.BLACK); // Color Negro solicitado
        btn.setForeground(COLOR_VERDE); // Texto Verde Neón para resaltar
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Borde verde para hacerlo más "resaltante"
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_VERDE, 2),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));
        
        btn.addActionListener(e -> ejecutarSimulacion());
        p.add(btn, g);

        // --- Resultados (Tabla y Gráfica) ---
        g.gridy=2; g.weighty=1.0;
        tabbedPaneResultados = new JTabbedPane();
        tabbedPaneResultados.addTab("Tabla Simular", crearPanelTabla());
        tabbedPaneResultados.addTab("Gráfica Hora Pico", crearPanelGrafica());
        p.add(tabbedPaneResultados, g);
        
        return p;
    }

    private JPanel crearPanelInfoFija(String tit, String desc, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_BORDE), tit));
        p.setBackground(COLOR_CARD); 
        ((TitledBorder)p.getBorder()).setTitleColor(COLOR_TEXTO);
        p.setPreferredSize(new Dimension(0, 80));
        
        JLabel l = new JLabel("<html><b>Distribución:</b><br>" + desc + "</html>");
        l.setForeground(color); 
        l.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelInferior() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
        p.setBackground(COLOR_FONDO);
        
        JButton btn = new JButton("Volver");
        btn.setBackground(COLOR_CARD);
        btn.setForeground(COLOR_TEXTO);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // CORRECCIÓN: Volver al menú principal en lugar de solo cerrar
        btn.addActionListener(e -> {
            new MenuPrincipal().setVisible(true);
            this.dispose();
        });
        
        p.add(btn); 
        return p;
    }

    private JPanel crearPanelTabla() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_CARD); 
        
        String[] h = {"#", "Ri(L)", "T.Lleg", "Hora Lleg", "Ri(S)", "T.Serv", "Inicio", "Fin", "Cola", "Ocio"};
        modeloTabla = new DefaultTableModel(h, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaSimulacion = new JTable(modeloTabla);
        estilizarTabla(tablaSimulacion); // Aplicar estilo oscuro

        JScrollPane scroll = new JScrollPane(tablaSimulacion);
        scroll.getViewport().setBackground(COLOR_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }
    
    private void estilizarTabla(JTable t) {
        // Estilo general oscuro
        t.setFont(new Font("SansSerif", Font.PLAIN, 14));
        t.setBackground(COLOR_CARD);
        t.setForeground(COLOR_TEXTO);
        t.setGridColor(COLOR_BORDE);
        t.setRowHeight(30);
        t.setShowVerticalLines(true);
        t.setShowHorizontalLines(true);
        
        // Estilo del Encabezado
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        t.getTableHeader().setBackground(COLOR_HEADER);
        t.getTableHeader().setForeground(COLOR_TEXTO);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));
        
        // Centrar contenido
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(COLOR_CARD);
        centerRenderer.setForeground(COLOR_TEXTO);
        
        for(int i=0; i<t.getColumnCount(); i++){
            t.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JPanel crearPanelGrafica() {
        JPanel p = new JPanel(new BorderLayout());
        panelGraficaComparativa = new PanelGraficaComparativa();
        p.add(panelGraficaComparativa);
        return p;
    }

    // --- LÓGICA DE SIMULACIÓN Y TRANSFORMADAS INVERSAS ---

    private void ejecutarSimulacion() {
        SimulacionDatos datos = SimulacionDatos.getInstancia();
        if (!datos.hayDatos()) {
             JOptionPane.showMessageDialog(this, "Primero debes generar números pseudoaleatorios.", "Sin Datos", JOptionPane.WARNING_MESSAGE);
             return;
        }

        modeloTabla.setRowCount(0);
        List<Double> rL = datos.getConjunto1RiEn();
        List<Double> rS = datos.getConjunto2RiSn();
        int N = datos.getNGenerados();
        List<EventoSistema> eventosSimul = new ArrayList<>();

        try {
            double horaBase = parsearHoraSimple(txtHoraInicio.getText());
            panelGraficaComparativa.setHoraInicio((int)horaBase);

            // Definir ventana de HORA PICO (simulada)
            double inicioPico = horaBase + 60;
            double finPico = horaBase + 150;

            double finAnt = horaBase;
            double acumLleg = horaBase;

            for (int i = 0; i < N; i++) {
                // 1. Obtener Ri
                double riL = rL.get(i);
                
                // 2. Calcular Tiempo de Llegada Base
                double tLlegBase = getEmpiricaLlegadas(riL);
                
                // --- LÓGICA DE HORA PICO ---
                double factorPico = 1.0;
                if (acumLleg >= inicioPico && acumLleg <= finPico) {
                    factorPico = 0.5; // Acelerar llegadas
                } else if (acumLleg > finPico) {
                    factorPico = 1.2; // Desacelerar llegadas
                }
                
                double tLlegReal = tLlegBase * factorPico;
                acumLleg += tLlegReal;

                // 3. Calcular Tiempo de Servicio
                double riS = rS.get(i);
                double tServ = getEmpiricaServicio(riS);

                // 4. Lógica Tabular
                double iniServ = Math.max(acumLleg, finAnt);
                double finServ = iniServ + tServ;
                
                // 5. Generar Eventos
                eventosSimul.add(new EventoSistema(acumLleg - horaBase, 1));
                eventosSimul.add(new EventoSistema(finServ - horaBase, -1));

                modeloTabla.addRow(new Object[]{
                    i+1, dfRi.format(riL), dfTime.format(tLlegReal), min2Hora(acumLleg),
                    dfRi.format(riS), dfTime.format(tServ), min2Hora(iniServ), min2Hora(finServ),
                    dfTime.format(iniServ-acumLleg), dfTime.format(iniServ-finAnt)
                });
                finAnt = finServ;
            }
            
            // 6. Actualizar gráfica
            ocupacionSimulada = calcularCurvaOcupacion(eventosSimul);
            panelGraficaComparativa.setDatos(ocupacionReal, ocupacionSimulada);
            tabbedPaneResultados.setSelectedIndex(1);

            JOptionPane.showMessageDialog(this, 
                "Simulación completada.\nSe aplicó un factor de aceleración de llegadas entre " + 
                min2Hora(inicioPico) + " y " + min2Hora(finPico) + " para simular la Hora Pico.");

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarDatosCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
        if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            File f = fc.getSelectedFile();
            List<EventoSistema> eventosReales = new ArrayList<>();
            try(BufferedReader br = new BufferedReader(new FileReader(f))){
                String l; double t0 = -1;
                while((l=br.readLine())!=null){
                    String[] p = l.split(",");
                    if(p.length>=2 && p[0].matches("\\d{2}:\\d{2}:\\d{2}")){
                        double tl = parsearHora(p[0]); // Llegada
                        double ts = parsearHora(p[1]); // Salida
                        if(t0==-1) t0=tl;
                        eventosReales.add(new EventoSistema(tl-t0, 1));
                        eventosReales.add(new EventoSistema(ts-t0, -1));
                    }
                }
                if(!eventosReales.isEmpty()){
                    ocupacionReal = calcularCurvaOcupacion(eventosReales);
                    panelGraficaComparativa.setHoraInicio((int)t0);
                    txtHoraInicio.setText(min2Hora(t0));
                    JOptionPane.showMessageDialog(this, "Datos CSV cargados para comparación.");
                }
            }catch(Exception e){}
        }
    }

    // --- CÁLCULO DE OCUPACIÓN ---
    private List<double[]> calcularCurvaOcupacion(List<EventoSistema> evs) {
        Collections.sort(evs, Comparator.comparingDouble(e -> e.t));
        List<double[]> curva = new ArrayList<>();
        int ocup = 0;
        curva.add(new double[]{0,0});
        for(EventoSistema e : evs) {
            ocup += e.tipo;
            curva.add(new double[]{e.t, ocup});
        }
        return curva;
    }
    private static class EventoSistema { double t; int tipo; EventoSistema(double x, int y){t=x; tipo=y;} }

    // --- TRANSFORMADAS INVERSAS AJUSTADAS PARA PICO ---
    private double getEmpiricaLlegadas(double r) {
        if(r < 0.151) return 2.0;
        if(r < 0.930) return 3.0;
        return 4.0;
    }
    private double getEmpiricaServicio(double r) {
        if(r < 0.195) return 2.0;
        if(r < 0.954) return 3.0;
        return 4.0;
    }
    
    // Helpers
    private double parsearHora(String s) { String[] p=s.split(":"); return Double.parseDouble(p[0])*60+Double.parseDouble(p[1]); }
    private double parsearHoraSimple(String s) { return parsearHora(s+":00"); }
    private String min2Hora(double m) { int t=(int)Math.round(m); return String.format("%02d:%02d", (t/60)%24, t%60); }
}