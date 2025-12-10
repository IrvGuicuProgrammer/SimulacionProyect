import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimulacionTortilleria extends JFrame {

    // Colores
    private final Color COLOR_FONDO = new Color(15, 15, 20);
    private final Color COLOR_CARD = new Color(30, 30, 40);
    private final Color COLOR_TEXTO = new Color(240, 240, 245);
    private final Color COLOR_BORDE = new Color(50, 55, 65);
    private final Color COLOR_VERDE = new Color(16, 185, 129);

    // Componentes UI
    private JComboBox<String> cbDistLlegadas, cbDistServicio;
    private JPanel panelParamsLlegadas, panelParamsServicio;
    private JTextField txtLlegP1, txtLlegP2, txtSerP1, txtSerP2;
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

        if (!SimulacionDatos.getInstancia().hayDatos()) {
            JOptionPane.showMessageDialog(this, "¡Alerta! Faltan datos pseudoaleatorios.", "Error", JOptionPane.WARNING_MESSAGE);
        }

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);

        // Inicializar
        JLabel t1=new JLabel(), t2=new JLabel(), t3=new JLabel(), t4=new JLabel();
        actualizarInputsParams(cbDistLlegadas, panelParamsLlegadas, t1, txtLlegP1, t2, txtLlegP2);
        actualizarInputsParams(cbDistServicio, panelParamsServicio, t3, txtSerP1, t4, txtSerP2);
    }

    private JPanel crearPanelSuperior() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel t = new JLabel("Simulación del Flujo de Clientes");
        t.setFont(new Font("SansSerif", Font.BOLD, 22)); t.setForeground(COLOR_TEXTO);
        p.add(t, BorderLayout.WEST);

        JPanel ctrls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ctrls.setBackground(COLOR_FONDO);
        
        JButton btnCsv = new JButton("Cargar CSV Reales");
        btnCsv.addActionListener(e -> cargarDatosCSV());
        
        JLabel lH = new JLabel("Hora Inicio: "); lH.setForeground(Color.GRAY);
        txtHoraInicio = new JTextField("11:00", 5);
        
        ctrls.add(btnCsv); ctrls.add(lH); ctrls.add(txtHoraInicio);
        p.add(ctrls, BorderLayout.EAST);
        return p;
    }

    private JPanel crearPanelCentral() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COLOR_FONDO);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH; g.insets = new Insets(5, 5, 5, 5);

        // Configuración
        g.gridx=0; g.gridy=0; g.weightx=0.5;
        p.add(crearPanelConfigDistribucion("Tiempos de Llegada", true), g);
        g.gridx=1;
        p.add(crearPanelConfigDistribucion("Tiempos de Servicio", false), g);

        // Botón
        g.gridx=0; g.gridy=1; g.gridwidth=2;
        JButton btn = new JButton("Ejecutar Simulación (Con Factor Hora Pico)");
        btn.setBackground(COLOR_VERDE); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.addActionListener(e -> ejecutarSimulacion());
        p.add(btn, g);

        // Resultados
        g.gridy=2; g.weighty=1.0;
        tabbedPaneResultados = new JTabbedPane();
        tabbedPaneResultados.addTab("Tabla Simular", crearPanelTabla());
        tabbedPaneResultados.addTab("Gráfica Hora Pico", crearPanelGrafica());
        p.add(tabbedPaneResultados, g);
        
        return p;
    }

    private JPanel crearPanelInferior() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT)); p.setBackground(COLOR_FONDO);
        JButton btn = new JButton("Volver");
        btn.addActionListener(e -> dispose());
        p.add(btn); return p;
    }

    private JPanel crearPanelConfigDistribucion(String tit, boolean esLleg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_BORDE), tit));
        p.setBackground(COLOR_CARD); ((TitledBorder)p.getBorder()).setTitleColor(COLOR_TEXTO);

        JComboBox<String> cb = new JComboBox<>(new String[]{"Empírica (Datos Excel)", "Exponencial", "Uniforme", "Normal"});
        JPanel pIn = new JPanel(); pIn.setLayout(new BoxLayout(pIn, BoxLayout.Y_AXIS)); pIn.setBackground(COLOR_CARD);
        
        JLabel l1=new JLabel(), l2=new JLabel(); JTextField t1=new JTextField(), t2=new JTextField();
        if(esLleg) { cbDistLlegadas=cb; panelParamsLlegadas=pIn; txtLlegP1=t1; txtLlegP2=t2; }
        else { cbDistServicio=cb; panelParamsServicio=pIn; txtSerP1=t1; txtSerP2=t2; }

        cb.addItemListener(e -> { if(e.getStateChange()==ItemEvent.SELECTED) actualizarInputsParams(cb, pIn, l1, t1, l2, t2); });
        p.add(cb, BorderLayout.NORTH); p.add(pIn, BorderLayout.CENTER);
        return p;
    }

    private void actualizarInputsParams(JComboBox cb, JPanel p, JLabel l1, JTextField t1, JLabel l2, JTextField t2) {
        p.removeAll();
        String sel = (String)cb.getSelectedItem();
        if(sel.contains("Empírica")) {
            JLabel l = new JLabel("<html>Datos fijos ajustados<br>para Hora Pico</html>"); 
            l.setForeground(COLOR_VERDE); p.add(l);
            t1.setText("0"); t2.setText("0");
        } else {
            l1.setText("Param 1:"); l2.setText("Param 2:");
            p.add(l1); p.add(t1); p.add(l2); p.add(t2);
        }
        p.revalidate(); p.repaint();
    }

    private JPanel crearPanelTabla() {
        JPanel p = new JPanel(new BorderLayout());
        String[] h = {"#", "Ri(L)", "T.Lleg", "Hora Lleg", "Ri(S)", "T.Serv", "Inicio", "Fin", "Cola", "Ocio"};
        modeloTabla = new DefaultTableModel(h, 0);
        tablaSimulacion = new JTable(modeloTabla);
        p.add(new JScrollPane(tablaSimulacion));
        return p;
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
        if (!datos.hayDatos()) return;

        modeloTabla.setRowCount(0);
        List<Double> rL = datos.getConjunto1RiEn();
        List<Double> rS = datos.getConjunto2RiSn();
        int N = datos.getNGenerados();
        List<EventoSistema> eventosSimul = new ArrayList<>();

        try {
            Parametros pLleg = getParams(cbDistLlegadas, txtLlegP1, txtLlegP2);
            Parametros pServ = getParams(cbDistServicio, txtSerP1, txtSerP2);
            double horaBase = parsearHoraSimple(txtHoraInicio.getText());
            panelGraficaComparativa.setHoraInicio((int)horaBase);

            // Definir ventana de HORA PICO (simulada)
            // Empieza 60 minutos después del inicio y dura 90 minutos
            double inicioPico = horaBase + 60;
            double finPico = horaBase + 150;

            double finAnt = horaBase;
            double acumLleg = horaBase;

            for (int i = 0; i < N; i++) {
                // 1. Obtener Ri
                double riL = rL.get(i);
                
                // 2. Calcular Tiempo de Llegada Base
                double tLlegBase = pLleg.empirica ? getEmpiricaLlegadas(riL) : calcVar(riL, pLleg);
                
                // --- LÓGICA DE HORA PICO ---
                // Si la hora actual está en la franja pico, reducimos el tiempo de llegada (más gente)
                double factorPico = 1.0;
                if (acumLleg >= inicioPico && acumLleg <= finPico) {
                    factorPico = 0.5; // La gente llega al DOBLE de velocidad (tiempo / 2)
                } else if (acumLleg > finPico) {
                    factorPico = 1.2; // Después del pico, se calma un poco más de lo normal
                }
                
                double tLlegReal = tLlegBase * factorPico;
                acumLleg += tLlegReal;

                // 3. Calcular Tiempo de Servicio
                double riS = rS.get(i);
                double tServ = pServ.empirica ? getEmpiricaServicio(riS) : calcVar(riS, pServ);

                // 4. Lógica Tabular
                double iniServ = Math.max(acumLleg, finAnt);
                double finServ = iniServ + tServ;
                
                // 5. Generar Eventos para la Gráfica
                eventosSimul.add(new EventoSistema(acumLleg - horaBase, 1));
                eventosSimul.add(new EventoSistema(finServ - horaBase, -1));

                modeloTabla.addRow(new Object[]{
                    i+1, dfRi.format(riL), dfTime.format(tLlegReal), min2Hora(acumLleg),
                    dfRi.format(riS), dfTime.format(tServ), min2Hora(iniServ), min2Hora(finServ),
                    dfTime.format(iniServ-acumLleg), dfTime.format(iniServ-finAnt)
                });
                finAnt = finServ;
            }
            
            // 6. Calcular curva y actualizar gráfica
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
        // Ajustado: Tiempos más cortos para facilitar la aglomeración
        // Originalmente tenías 3, 4, 5. Los bajé a 2, 3, 4.
        if(r < 0.151) return 2.0;
        if(r < 0.930) return 3.0;
        return 4.0;
    }
    private double getEmpiricaServicio(double r) {
        // Ajustado: El servicio se mantiene o es ligeramente más lento que la llegada
        // Tiempos: 2, 3, 4 (Igual que llegadas, pero con el factor pico, las llegadas serán de 1.0 a 2.0)
        if(r < 0.195) return 2.0;
        if(r < 0.954) return 3.0;
        return 4.0;
    }
    
    private double calcVar(double r, Parametros p) {
        SimulacionDatos m = SimulacionDatos.getInstancia();
        switch(p.tipo){
            case "Exponencial": return m.calcularExponencial(r, p.p1);
            case "Uniforme": return m.calcularUniforme(r, p.p1, p.p2);
            case "Normal": return m.calcularNormal(r, p.p1, p.p2);
            default: return 0;
        }
    }

    // Helpers
    private Parametros getParams(JComboBox c, JTextField t1, JTextField t2) {
        double v1=0, v2=0; try{v1=Double.parseDouble(t1.getText()); v2=Double.parseDouble(t2.getText());}catch(Exception e){}
        return new Parametros((String)c.getSelectedItem(), v1, v2);
    }
    private double parsearHora(String s) { String[] p=s.split(":"); return Double.parseDouble(p[0])*60+Double.parseDouble(p[1]); }
    private double parsearHoraSimple(String s) { return parsearHora(s+":00"); }
    private String min2Hora(double m) { int t=(int)Math.round(m); return String.format("%02d:%02d", (t/60)%24, t%60); }
    private static class Parametros { String tipo; double p1, p2; boolean empirica; Parametros(String t, double a, double b){tipo=t; p1=a; p2=b; empirica=t.contains("Empírica");}}
}