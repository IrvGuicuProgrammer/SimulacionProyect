import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.*;
import java.util.List;

public class SimulacionTortilleria extends JFrame {
    
    // Componentes para simulación
    private JTextField txtLambdaAtencion, txtLambdaLlegada;
    private JTextField txtHorasSimulacion, txtClientesPorHora;
    private JButton btnSimular, btnCargarDatosReales, btnAnalizarSimilitud;
    private JComboBox<String> comboDistAtencion, comboDistLlegada;
    
    // Gráfica
    private GraficaPanel panelGrafica;
    
    // Tablas
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    
    // Datos
    private List<PuntoDato> datosReales = new ArrayList<>();
    private List<PuntoDato> datosSimulados = new ArrayList<>();
    
    // Etiquetas de estadísticas
    private JLabel lblEstadisticasReales, lblEstadisticasSimulados, lblComparacion;
    
    // Constantes
    private static final double[] DATOS_REALES_EJEMPLO = {
        0, 18, 42, 65, 88, 105, 120, 132, 140  // Valores acumulados cada 0.5 horas
    };
    
    public SimulacionTortilleria() {
        setTitle("SIMULACIÓN DE TORTILLERÍA LA PROVIDENCIA");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        aplicarTemaOscuro();
        
        // Panel superior con botón de regreso
        JPanel panelSuperiorCompleto = new JPanel(new BorderLayout());
        panelSuperiorCompleto.setBackground(new Color(45, 45, 48));
        
        // Botón de regreso
        JButton btnRegresar = new JButton("← REGRESAR AL MENÚ");
        estilizarBoton(btnRegresar, new Color(0, 122, 204));
        btnRegresar.addActionListener(e -> {
            new MenuPrincipal().setVisible(true);
            this.dispose();
        });
        
        JPanel panelBotonesSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonesSuperior.setBackground(new Color(45, 45, 48));
        panelBotonesSuperior.add(btnRegresar);
        
        panelSuperiorCompleto.add(panelBotonesSuperior, BorderLayout.NORTH);
        panelSuperiorCompleto.add(crearPanelControl(), BorderLayout.CENTER);
        
        add(panelSuperiorCompleto, BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
        
        // Cargar datos reales automáticamente
        cargarDatosReales();
    }
    
    private JPanel crearPanelControl() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBackground(new Color(45, 45, 48));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(62, 62, 66)),
            "CONTROLES DE SIMULACIÓN"
        ));
        
        // Fila 1: Parámetros de simulación
        JPanel panelParametros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelParametros.setBackground(new Color(45, 45, 48));
        
        panelParametros.add(new JLabel("Dist. Atención:"));
        comboDistAtencion = new JComboBox<>(new String[]{"Exponencial", "Normal", "Uniforme"});
        comboDistAtencion.setBackground(new Color(37, 37, 38));
        comboDistAtencion.setForeground(Color.WHITE);
        panelParametros.add(comboDistAtencion);
        
        panelParametros.add(new JLabel("λ/Media Atención:"));
        txtLambdaAtencion = crearCampoTexto("4.5");
        txtLambdaAtencion.setToolTipText("Minutos promedio de atención por cliente");
        panelParametros.add(txtLambdaAtencion);
        
        panelParametros.add(new JLabel("Dist. Llegada:"));
        comboDistLlegada = new JComboBox<>(new String[]{"Exponencial", "Poisson", "Uniforme"});
        comboDistLlegada.setBackground(new Color(37, 37, 38));
        comboDistLlegada.setForeground(Color.WHITE);
        panelParametros.add(comboDistLlegada);
        
        panelParametros.add(new JLabel("λ Llegada (cl/hr):"));
        txtLambdaLlegada = crearCampoTexto("30");
        txtLambdaLlegada.setToolTipText("Clientes por hora promedio");
        panelParametros.add(txtLambdaLlegada);
        
        // Fila 2: Configuración y botones
        JPanel panelConfig = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelConfig.setBackground(new Color(45, 45, 48));
        
        panelConfig.add(new JLabel("Horas Simulación:"));
        txtHorasSimulacion = crearCampoTexto("4");
        txtHorasSimulacion.setToolTipText("Duración de la simulación en horas");
        panelConfig.add(txtHorasSimulacion);
        
        panelConfig.add(new JLabel("Capacidad Máxima:"));
        txtClientesPorHora = crearCampoTexto("35");
        txtClientesPorHora.setToolTipText("Máximo de clientes que pueden ser atendidos por hora");
        panelConfig.add(txtClientesPorHora);
        
        btnCargarDatosReales = new JButton("📊 CARGAR DATOS REALES");
        estilizarBoton(btnCargarDatosReales, new Color(0, 122, 204));
        btnCargarDatosReales.addActionListener(e -> cargarDatosReales());
        panelConfig.add(btnCargarDatosReales);
        
        btnSimular = new JButton("▶ EJECUTAR SIMULACIÓN");
        estilizarBoton(btnSimular, new Color(87, 166, 74));
        btnSimular.addActionListener(e -> ejecutarSimulacion());
        panelConfig.add(btnSimular);
        
        btnAnalizarSimilitud = new JButton("📈 ANALIZAR SIMILITUD");
        estilizarBoton(btnAnalizarSimilitud, new Color(180, 100, 255));
        btnAnalizarSimilitud.addActionListener(e -> analizarSimilitudGraficas());
        panelConfig.add(btnAnalizarSimilitud);
        
        panel.add(panelParametros);
        panel.add(panelConfig);
        
        return panel;
    }
    
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBackground(new Color(45, 45, 48));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel izquierdo: Gráfica
        JPanel panelGraficaContenedor = new JPanel(new BorderLayout());
        panelGraficaContenedor.setBackground(new Color(45, 45, 48));
        panelGraficaContenedor.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(62, 62, 66)),
            "GRÁFICA COMPARATIVA: DATOS REALES vs SIMULADOS"
        ));
        
        panelGrafica = new GraficaPanel();
        panelGraficaContenedor.add(panelGrafica, BorderLayout.CENTER);
        
        // Panel derecho: Resultados y estadísticas
        JPanel panelDerecho = new JPanel(new BorderLayout(5, 5));
        panelDerecho.setBackground(new Color(45, 45, 48));
        
        // Tabla de resultados
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(new Color(45, 45, 48));
        panelTabla.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(62, 62, 66)),
            "RESULTADOS DETALLADOS"
        ));
        
        modeloTabla = new DefaultTableModel(new Object[]{"Hora", "Clientes Reales", "Clientes Simulados", "Diferencia", "% Error"}, 0);
        tablaResultados = new JTable(modeloTabla);
        tablaResultados.setRowHeight(25);
        tablaResultados.getTableHeader().setForeground(Color.WHITE);
        tablaResultados.getTableHeader().setBackground(new Color(37, 37, 38));
        tablaResultados.setGridColor(new Color(62, 62, 66));
        
        JScrollPane scrollTabla = new JScrollPane(tablaResultados);
        scrollTabla.getViewport().setBackground(new Color(37, 37, 38));
        panelTabla.add(scrollTabla, BorderLayout.CENTER);
        
        // Panel de estadísticas
        JPanel panelEstadisticas = new JPanel(new GridLayout(3, 1, 5, 5));
        panelEstadisticas.setBackground(new Color(45, 45, 48));
        panelEstadisticas.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(62, 62, 66)),
            "ESTADÍSTICAS COMPARATIVAS"
        ));
        
        lblEstadisticasReales = crearEtiquetaEstadistica("📋 Datos Reales: Cargando...");
        lblEstadisticasSimulados = crearEtiquetaEstadistica("⚙️ Datos Simulados: No generados");
        lblComparacion = crearEtiquetaEstadistica("📊 Comparación: Esperando datos...");
        
        panelEstadisticas.add(lblEstadisticasReales);
        panelEstadisticas.add(lblEstadisticasSimulados);
        panelEstadisticas.add(lblComparacion);
        
        panelDerecho.add(panelTabla, BorderLayout.CENTER);
        panelDerecho.add(panelEstadisticas, BorderLayout.SOUTH);
        
        panel.add(panelGraficaContenedor);
        panel.add(panelDerecho);
        
        return panel;
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 45, 48));
        
        JPanel panelLeyenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panelLeyenda.setBackground(new Color(45, 45, 48));
        
        // Leyenda para la gráfica
        JLabel leyendaReal = new JLabel("🔵 Datos Reales (Tortillería La Providencia)");
        leyendaReal.setForeground(new Color(0, 122, 204));
        leyendaReal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JLabel leyendaSimulado = new JLabel("🟢 Datos Simulados");
        leyendaSimulado.setForeground(new Color(87, 166, 74));
        leyendaSimulado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        panelLeyenda.add(leyendaReal);
        panelLeyenda.add(leyendaSimulado);
        
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.setBackground(new Color(45, 45, 48));
        JLabel info = new JLabel("Simulación de flujo de clientes - Tortillería La Providencia | Datos reales tomados durante 4 horas (12:00 - 16:00)");
        info.setForeground(new Color(200, 200, 200));
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panelInfo.add(info);
        
        panel.add(panelLeyenda, BorderLayout.NORTH);
        panel.add(panelInfo, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JTextField crearCampoTexto(String texto) {
        JTextField campo = new JTextField(texto, 8);
        campo.setBackground(new Color(37, 37, 38));
        campo.setForeground(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(62, 62, 66)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return campo;
    }
    
    private void estilizarBoton(JButton b, Color color) {
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        b.setBackground(new Color(37, 37, 38));
        b.setForeground(color);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(color);
                b.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(new Color(37, 37, 38));
                b.setForeground(color);
            }
        });
    }
    
    private JLabel crearEtiquetaEstadistica(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }
    
    private void cargarDatosReales() {
        datosReales.clear();
        
        // Intentar cargar datos reales del Excel
        boolean datosCargados = false;
        
        try {
            // Aquí deberías implementar la lectura real del Excel
            // Por ahora usamos datos de ejemplo basados en tu descripción
            
            // Simular datos reales (ajusta estos valores según tus datos)
            // Estos son datos acumulados cada 30 minutos
            for (int i = 0; i <= 8; i++) { // 0, 0.5, 1, ..., 4 horas
                double hora = i * 0.5;
                int clientes = (int)DATOS_REALES_EJEMPLO[i];
                datosReales.add(new PuntoDato(hora, clientes));
            }
            
            datosCargados = true;
            
        } catch (Exception e) {
            System.err.println("Error al cargar datos reales: " + e.getMessage());
            // Usar datos de ejemplo como respaldo
            cargarDatosEjemplo();
        }
        
        if (datosCargados) {
            actualizarEstadisticasReales();
            panelGrafica.repaint();
            
            JOptionPane.showMessageDialog(this, 
                String.format("<html><div style='text-align: center;'>" +
                    "<b>✅ DATOS REALES CARGADOS</b><br>" +
                    "Puntos temporales: %d<br>" +
                    "Total clientes: %d<br>" +
                    "Período: 12:00 - 16:00 (4 horas)" +
                    "</div></html>",
                    datosReales.size(), 
                    datosReales.isEmpty() ? 0 : datosReales.get(datosReales.size()-1).clientes),
                "Datos Cargados", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void cargarDatosEjemplo() {
        datosReales.clear();
        
        // Datos de ejemplo más detallados
        double[] horas = {0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0};
        int[] clientes = {0, 22, 45, 67, 89, 108, 124, 137, 148};
        
        for (int i = 0; i < horas.length; i++) {
            datosReales.add(new PuntoDato(horas[i], clientes[i]));
        }
        
        actualizarEstadisticasReales();
        panelGrafica.repaint();
    }
    
    private void ejecutarSimulacion() {
        try {
            double lambdaAtencion = Double.parseDouble(txtLambdaAtencion.getText());
            double lambdaLlegada = Double.parseDouble(txtLambdaLlegada.getText());
            int horas = Integer.parseInt(txtHorasSimulacion.getText());
            int capacidadMaxima = Integer.parseInt(txtClientesPorHora.getText());
            
            if (lambdaAtencion <= 0 || lambdaLlegada <= 0 || horas <= 0 || capacidadMaxima <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Todos los parámetros deben ser valores positivos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (datosReales.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Primero carga los datos reales para comparar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            datosSimulados.clear();
            modeloTabla.setRowCount(0);
            
            Random rand = new Random();
            String distAtencion = (String) comboDistAtencion.getSelectedItem();
            String distLlegada = (String) comboDistLlegada.getSelectedItem();
            
            // Variables de simulación
            double tiempoActual = 0.0;
            int clientesAtendidos = 0;
            int clientesEnCola = 0;
            int maxClientesEnSistema = 0;
            List<Double> tiemposAtencion = new ArrayList<>();
            List<Double> tiemposEntreLlegadas = new ArrayList<>();
            
            // Simulación por intervalos de tiempo (método de intervalos fijos)
            double intervalo = 0.5; // 30 minutos
            int indiceIntervalo = 0;
            
            while (tiempoActual < horas) {
                // Calcular llegadas en este intervalo
                double llegadasEsperadas = lambdaLlegada * intervalo;
                int llegadas = rand.nextInt((int)Math.ceil(llegadasEsperadas * 1.5)) + 
                               (int)Math.floor(llegadasEsperadas * 0.5);
                
                clientesEnCola += llegadas;
                maxClientesEnSistema = Math.max(maxClientesEnSistema, clientesEnCola);
                
                // Calcular capacidad de atención en este intervalo
                double atencionesPosibles = (capacidadMaxima * intervalo) / 
                                          (lambdaAtencion / 60.0); // Convertir a horas
                int atenciones = Math.min(clientesEnCola, (int)Math.ceil(atencionesPosibles));
                
                clientesEnCola -= atenciones;
                clientesAtendidos += atenciones;
                
                // Registrar para este intervalo
                datosSimulados.add(new PuntoDato(tiempoActual, clientesAtendidos));
                
                // Generar tiempos aleatorios para estadísticas
                for (int i = 0; i < atenciones; i++) {
                    tiemposAtencion.add(generarTiempo(distAtencion, lambdaAtencion/60.0, rand)); // Convertir a horas
                }
                for (int i = 0; i < llegadas; i++) {
                    tiemposEntreLlegadas.add(generarTiempo(distLlegada, lambdaLlegada, rand));
                }
                
                tiempoActual += intervalo;
                indiceIntervalo++;
            }
            
            // Asegurar que tenemos el mismo número de puntos que los datos reales
            completarDatosSimulados(horas);
            
            // Actualizar interfaz
            actualizarTablaResultados();
            actualizarEstadisticasSimulados(tiemposAtencion, tiemposEntreLlegadas, maxClientesEnSistema);
            panelGrafica.repaint();
            
            // Mostrar resumen
            int totalFinal = datosSimulados.isEmpty() ? 0 : 
                           datosSimulados.get(datosSimulados.size()-1).clientes;
            int realFinal = datosReales.isEmpty() ? 0 : 
                          datosReales.get(datosReales.size()-1).clientes;
            double diferenciaPorcentual = realFinal > 0 ? 
                Math.abs((totalFinal - realFinal) / (double)realFinal * 100) : 0;
            
            String mensaje = String.format(
                "<html><div style='text-align: center;'>" +
                "<b>✅ SIMULACIÓN COMPLETADA</b><br><br>" +
                "Clientes simulados atendidos: <b>%d</b><br>" +
                "Clientes reales atendidos: <b>%d</b><br>" +
                "Diferencia: <b>%+.d clientes (%.1f%%)</b><br>" +
                "Máximo en sistema: <b>%d clientes</b><br><br>" +
                "<span style='color: %s;'>%s</span>" +
                "</div></html>",
                totalFinal, realFinal, totalFinal - realFinal, diferenciaPorcentual,
                maxClientesEnSistema,
                diferenciaPorcentual < 15 ? "#57A64A" : 
                diferenciaPorcentual < 30 ? "#FF9900" : "#D16969",
                diferenciaPorcentual < 15 ? "✓ Buena coincidencia con datos reales" :
                diferenciaPorcentual < 30 ? "⚠ Coincidencia moderada" :
                "✗ Diferencias significativas - ajustar parámetros"
            );
            
            JOptionPane.showMessageDialog(this, mensaje, 
                "Resultado de Simulación", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Error: Verifica que todos los parámetros sean números válidos\n" + e.getMessage(),
                "Error en Parámetros", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error inesperado en la simulación: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private double generarTiempo(String distribucion, double parametro, Random rand) {
        switch (distribucion) {
            case "Exponencial":
                return -Math.log(1 - rand.nextDouble()) / parametro;
            case "Normal":
                double valor = rand.nextGaussian() * (parametro/3) + parametro;
                return Math.max(0.01, valor); // Evitar valores negativos
            case "Uniforme":
                return rand.nextDouble() * parametro * 2;
            case "Poisson":
                // Para simulación de Poisson, usamos tiempo exponencial
                return -Math.log(1 - rand.nextDouble()) / parametro;
            default:
                return -Math.log(1 - rand.nextDouble()) / parametro;
        }
    }
    
    private void completarDatosSimulados(int horas) {
        if (datosSimulados.isEmpty() || datosReales.isEmpty()) return;
        
        // Asegurar que tenemos puntos en los mismos intervalos que los datos reales
        List<PuntoDato> completos = new ArrayList<>();
        
        for (PuntoDato puntoReal : datosReales) {
            double hora = puntoReal.hora;
            
            // Buscar el dato simulado más cercano
            PuntoDato masCercano = null;
            double menorDistancia = Double.MAX_VALUE;
            
            for (PuntoDato puntoSim : datosSimulados) {
                double distancia = Math.abs(puntoSim.hora - hora);
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    masCercano = puntoSim;
                }
            }
            
            if (masCercano != null && menorDistancia <= 0.25) { // Dentro de 15 minutos
                completos.add(new PuntoDato(hora, masCercano.clientes));
            } else {
                // Interpolar o usar el último valor
                int valor = completos.isEmpty() ? 0 : completos.get(completos.size()-1).clientes;
                completos.add(new PuntoDato(hora, valor));
            }
        }
        
        datosSimulados = completos;
    }
    
    private void actualizarTablaResultados() {
        modeloTabla.setRowCount(0);
        
        if (datosReales.isEmpty() || datosSimulados.isEmpty()) {
            return;
        }
        
        double errorTotalAbsoluto = 0;
        double errorTotalPorcentual = 0;
        int puntosValidos = 0;
        
        for (int i = 0; i < datosReales.size(); i++) {
            PuntoDato real = datosReales.get(i);
            PuntoDato simulado = i < datosSimulados.size() ? datosSimulados.get(i) : 
                               new PuntoDato(real.hora, 0);
            
            int diferencia = simulado.clientes - real.clientes;
            double errorPorcentual = real.clientes > 0 ? 
                Math.abs((double)diferencia / real.clientes * 100) : 0;
            
            modeloTabla.addRow(new Object[]{
                String.format("%.1f hrs", real.hora),
                real.clientes,
                simulado.clientes,
                String.format("%+d", diferencia),
                String.format("%.1f%%", errorPorcentual)
            });
            
            if (real.clientes > 0) {
                errorTotalAbsoluto += Math.abs(diferencia);
                errorTotalPorcentual += errorPorcentual;
                puntosValidos++;
            }
        }
        
        // Agregar promedio al final
        if (puntosValidos > 0) {
            modeloTabla.addRow(new Object[]{
                "PROMEDIO",
                "-",
                "-",
                String.format("%.1f", errorTotalAbsoluto / puntosValidos),
                String.format("%.1f%%", errorTotalPorcentual / puntosValidos)
            });
        }
    }
    
    private void actualizarEstadisticasReales() {
        if (datosReales.isEmpty()) {
            lblEstadisticasReales.setText("📋 Datos Reales: No cargados");
            return;
        }
        
        int totalClientes = datosReales.get(datosReales.size()-1).clientes;
        double tasaPromedio = totalClientes / 4.0; // por hora
        
        // Calcular horas pico
        double horaPico = 0;
        int maxClientesHora = 0;
        for (int i = 1; i < datosReales.size(); i++) {
            int clientesEnHora = datosReales.get(i).clientes - datosReales.get(i-1).clientes;
            if (clientesEnHora > maxClientesHora) {
                maxClientesHora = clientesEnHora;
                horaPico = datosReales.get(i).hora;
            }
        }
        
        lblEstadisticasReales.setText(String.format(
            "📋 Datos Reales: %d clientes totales, %.1f clientes/hora | Hora pico: %.1f hrs (%d clientes)",
            totalClientes, tasaPromedio, horaPico, maxClientesHora
        ));
    }
    
    private void actualizarEstadisticasSimulados(List<Double> tiemposAtencion, 
                                                 List<Double> tiemposLlegada, 
                                                 int maxClientes) {
        if (datosSimulados.isEmpty()) {
            lblEstadisticasSimulados.setText("⚙️ Datos Simulados: No generados");
            return;
        }
        
        double tiempoPromedioAtencion = tiemposAtencion.isEmpty() ? 0 :
            tiemposAtencion.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        
        double tiempoPromedioEntreLlegadas = tiemposLlegada.isEmpty() ? 0 :
            tiemposLlegada.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        
        int clientesTotales = datosSimulados.get(datosSimulados.size()-1).clientes;
        
        lblEstadisticasSimulados.setText(String.format(
            "⚙️ Datos Simulados: %d clientes | Atención: %.1f min | Entre llegadas: %.1f min | Máx en sistema: %d",
            clientesTotales, 
            tiempoPromedioAtencion * 60,
            tiempoPromedioEntreLlegadas * 60,
            maxClientes
        ));
        
        // Actualizar comparación
        if (!datosReales.isEmpty()) {
            int clientesReales = datosReales.get(datosReales.size()-1).clientes;
            double diferencia = clientesTotales - clientesReales;
            double diferenciaPorcentual = clientesReales > 0 ? 
                (diferencia / clientesReales) * 100 : 0;
            
            String evaluacion;
            Color color;
            
            if (Math.abs(diferenciaPorcentual) < 10) {
                evaluacion = "EXCELENTE coincidencia";
                color = new Color(87, 166, 74);
            } else if (Math.abs(diferenciaPorcentual) < 20) {
                evaluacion = "BUENA coincidencia";
                color = new Color(255, 153, 0);
            } else if (Math.abs(diferenciaPorcentual) < 30) {
                evaluacion = "COINCIDENCIA MODERADA";
                color = new Color(255, 100, 100);
            } else {
                evaluacion = "BAJA coincidencia";
                color = new Color(200, 50, 50);
            }
            
            lblComparacion.setText(String.format(
                "<html>📊 Comparación: Diferencia: <b style='color: %s'>%+.1f%%</b> (%s)</html>",
                String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()),
                diferenciaPorcentual, evaluacion
            ));
        }
    }
    
    private void analizarSimilitudGraficas() {
        if (datosReales.isEmpty() || datosSimulados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center;'>" +
                "❌ <b>DATOS INSUFICIENTES</b><br><br>" +
                "Para analizar la similitud necesitas:<br>" +
                "1. Cargar datos reales (botón azul)<br>" +
                "2. Ejecutar una simulación (botón verde)</div></html>",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Métricas de similitud
        double mae = calcularMAE();
        double mape = calcularMAPE();
        double correlacion = calcularCorrelacion();
        double similitudForma = calcularSimilitudForma();
        
        // Análisis visual
        String analisisVisual = realizarAnalisisVisual();
        
        // Crear reporte
        String reporte = crearReporteSimilitud(mae, mape, correlacion, similitudForma, analisisVisual);
        
        // Mostrar diálogo
        mostrarDialogoAnalisis(reporte);
    }
    
    private double calcularMAE() {
        double errorTotal = 0;
        int puntos = 0;
        
        for (int i = 0; i < Math.min(datosReales.size(), datosSimulados.size()); i++) {
            int real = datosReales.get(i).clientes;
            int simulado = datosSimulados.get(i).clientes;
            errorTotal += Math.abs(simulado - real);
            puntos++;
        }
        
        return puntos > 0 ? errorTotal / puntos : 0;
    }
    
    private double calcularMAPE() {
        double errorTotal = 0;
        int puntos = 0;
        
        for (int i = 0; i < Math.min(datosReales.size(), datosSimulados.size()); i++) {
            int real = datosReales.get(i).clientes;
            int simulado = datosSimulados.get(i).clientes;
            
            if (real > 0) {
                errorTotal += Math.abs((double)(simulado - real) / real);
                puntos++;
            }
        }
        
        return puntos > 0 ? (errorTotal / puntos) * 100 : 0;
    }
    
    private double calcularCorrelacion() {
        List<Double> reales = new ArrayList<>();
        List<Double> simulados = new ArrayList<>();
        
        for (int i = 0; i < Math.min(datosReales.size(), datosSimulados.size()); i++) {
            reales.add((double) datosReales.get(i).clientes);
            simulados.add((double) datosSimulados.get(i).clientes);
        }
        
        return calcularCoeficienteCorrelacion(reales, simulados);
    }
    
    private double calcularSimilitudForma() {
        if (datosReales.size() < 2 || datosSimulados.size() < 2) return 0;
        
        // Calcular pendientes
        List<Double> pendientesReales = new ArrayList<>();
        List<Double> pendientesSimulados = new ArrayList<>();
        
        for (int i = 1; i < datosReales.size(); i++) {
            double pendiente = (datosReales.get(i).clientes - datosReales.get(i-1).clientes) / 0.5;
            pendientesReales.add(pendiente);
        }
        
        for (int i = 1; i < datosSimulados.size(); i++) {
            double pendiente = (datosSimulados.get(i).clientes - datosSimulados.get(i-1).clientes) / 0.5;
            pendientesSimulados.add(pendiente);
        }
        
        // Comparar pendientes
        double similitud = 0;
        int comparaciones = Math.min(pendientesReales.size(), pendientesSimulados.size());
        
        for (int i = 0; i < comparaciones; i++) {
            double diff = Math.abs(pendientesReales.get(i) - pendientesSimulados.get(i));
            double max = Math.max(Math.abs(pendientesReales.get(i)), Math.abs(pendientesSimulados.get(i)));
            if (max > 0) {
                similitud += 1 - (diff / max);
            }
        }
        
        return comparaciones > 0 ? similitud / comparaciones : 0;
    }
    
    private String realizarAnalisisVisual() {
        StringBuilder analisis = new StringBuilder();
        
        // 1. Comparar forma general
        boolean mismaTendencia = tieneMismaTendencia();
        analisis.append("• <b>Tendencia general:</b> ");
        analisis.append(mismaTendencia ? 
            "✅ Ambas curvas tienen tendencia ascendente similar<br>" :
            "❌ Las curvas muestran tendencias diferentes<br>");
        
        // 2. Comparar puntos críticos
        List<Double> picosReales = encontrarPicos(datosReales);
        List<Double> picosSimulados = encontrarPicos(datosSimulados);
        
        analisis.append("• <b>Puntos de crecimiento acelerado:</b><br>");
        analisis.append("  - Reales: ").append(formatearHoras(picosReales)).append("<br>");
        analisis.append("  - Simulados: ").append(formatearHoras(picosSimulados)).append("<br>");
        
        // 3. Comparar valores finales
        int finalReal = datosReales.get(datosReales.size()-1).clientes;
        int finalSimulado = datosSimulados.get(datosSimulados.size()-1).clientes;
        double diffFinal = Math.abs(finalSimulado - finalReal) / (double)finalReal * 100;
        
        analisis.append("• <b>Valor final:</b> ");
        analisis.append(String.format("Real: %d, Simulado: %d (%.1f%% diferencia)<br>", 
            finalReal, finalSimulado, diffFinal));
        
        // 4. Evaluación visual general
        if (diffFinal < 15 && mismaTendencia && !picosReales.isEmpty() && !picosSimulados.isEmpty()) {
            analisis.append("<br><span style='color: #57A64A;'><b>✅ EVALUACIÓN VISUAL: EXCELENTE</b><br>");
            analisis.append("Las gráficas muestran alta similitud en forma y valores</span>");
        } else if (diffFinal < 25 && mismaTendencia) {
            analisis.append("<br><span style='color: #FF9900;'><b>⚠️ EVALUACIÓN VISUAL: MODERADA</b><br>");
            analisis.append("Misma tendencia pero diferencias en valores específicos</span>");
        } else {
            analisis.append("<br><span style='color: #D16969;'><b>❌ EVALUACIÓN VISUAL: BAJA</b><br>");
            analisis.append("Diferencias significativas en forma y/o valores</span>");
        }
        
        return analisis.toString();
    }
    
    private String crearReporteSimilitud(double mae, double mape, double correlacion, 
                                        double similitudForma, String analisisVisual) {
        // Calcular puntuación general
        double puntuacion = (100 - Math.min(mape, 100)) * 0.3 + // MAPE contribuye 30%
                          correlacion * 40 +                    // Correlación contribuye 40%
                          similitudForma * 30;                  // Forma contribuye 30%
        
        String conclusion;
        String color;
        
        if (puntuacion >= 85) {
            conclusion = "EXCELENTE COINCIDENCIA - La simulación representa fielmente la realidad";
            color = "#57A64A";
        } else if (puntuacion >= 70) {
            conclusion = "BUENA COINCIDENCIA - Captura adecuadamente el comportamiento general";
            color = "#FF9900";
        } else if (puntuacion >= 50) {
            conclusion = "COINCIDENCIA MODERADA - Necesita ajustes menores en parámetros";
            color = "#D16969";
        } else {
            conclusion = "BAJA COINCIDENCIA - Revisar modelo y parámetros de simulación";
            color = "#A33030";
        }
        
        return String.format(
            "<html><div style='font-family: Segoe UI; width: 500px;'>" +
            "<h2 style='color: #007ACC; text-align: center;'>📊 ANÁLISIS DE SIMILITUD</h2>" +
            "<h3 style='color: #FF9900; text-align: center;'>Tortillería La Providencia</h3>" +
            "<hr style='border: 1px solid #3E3E42;'>" +
            
            "<h4 style='color: #57A64A;'>📈 MÉTRICAS CUANTITATIVAS:</h4>" +
            "<table border='0' cellpadding='5' style='width: 100%%;'>" +
            "<tr><td><b>Error Absoluto Medio (MAE):</b></td>" +
            "<td>%.1f clientes</td><td>%s</td></tr>" +
            
            "<tr><td><b>Error Porcentual (MAPE):</b></td>" +
            "<td>%.1f%%</td><td>%s</td></tr>" +
            
            "<tr><td><b>Coeficiente de Correlación:</b></td>" +
            "<td>%.3f</td><td>%s</td></tr>" +
            
            "<tr><td><b>Similitud de Forma:</b></td>" +
            "<td>%.1f%%</td><td>%s</td></tr>" +
            
            "<tr><td colspan='3'><hr style='border: 1px dashed #3E3E42;'></td></tr>" +
            
            "<tr><td><b>PUNTUACIÓN GENERAL:</b></td>" +
            "<td><b>%.0f/100</b></td>" +
            "<td><b style='color: %s'>%s</b></td></tr>" +
            "</table>" +
            
            "<hr style='border: 1px solid #3E3E42;'>" +
            
            "<h4 style='color: #FF9900;'>👁️ ANÁLISIS VISUAL:</h4>" +
            "<div style='background: #2D2D30; padding: 10px; border-radius: 5px;'>" +
            "%s" +
            "</div>" +
            
            "<hr style='border: 1px solid #3E3E42;'>" +
            
            "<h4 style='color: #B464A6;'>🎯 CONCLUSIÓN:</h4>" +
            "<div style='background: %s20; padding: 10px; border-radius: 5px; border-left: 4px solid %s;'>" +
            "<b style='color: %s'>%s</b>" +
            "</div>" +
            
            "<hr style='border: 1px solid #3E3E42;'>" +
            
            "<div style='font-size: 10px; color: #999999; text-align: center;'>" +
            "Interpretación: MAE ≤ 5 excelente, ≤ 10 bueno | MAPE ≤ 10%% excelente, ≤ 20%% bueno | " +
            "Correlación ≥ 0.9 excelente, ≥ 0.7 buena" +
            "</div>" +
            "</div></html>",
            mae, evaluarMAE(mae),
            mape, evaluarMAPE(mape),
            correlacion, evaluarCorrelacion(correlacion),
            similitudForma * 100, evaluarPorcentaje(similitudForma * 100),
            puntuacion, color, evaluarPuntuacion(puntuacion),
            analisisVisual,
            color, color, color, conclusion
        );
    }
    
    private String evaluarMAE(double mae) {
        if (mae <= 5) return "✅ Excelente";
        if (mae <= 10) return "🟡 Bueno";
        if (mae <= 15) return "🟠 Moderado";
        return "🔴 Alto";
    }
    
    private String evaluarMAPE(double mape) {
        if (mape <= 10) return "✅ Excelente";
        if (mape <= 20) return "🟡 Bueno";
        if (mape <= 30) return "🟠 Moderado";
        return "🔴 Alto";
    }
    
    private String evaluarCorrelacion(double corr) {
        if (corr >= 0.9) return "✅ Muy fuerte";
        if (corr >= 0.7) return "🟡 Fuerte";
        if (corr >= 0.5) return "🟠 Moderada";
        if (corr >= 0.3) return "🟡 Débil";
        return "🔴 Muy débil";
    }
    
    private String evaluarPorcentaje(double porcentaje) {
        if (porcentaje >= 90) return "✅ Excelente";
        if (porcentaje >= 80) return "🟡 Bueno";
        if (porcentaje >= 70) return "🟠 Moderado";
        return "🔴 Bajo";
    }
    
    private String evaluarPuntuacion(double puntuacion) {
        if (puntuacion >= 85) return "EXCELENTE";
        if (puntuacion >= 70) return "BUENA";
        if (puntuacion >= 50) return "MODERADA";
        return "BAJA";
    }
    
    private void mostrarDialogoAnalisis(String contenido) {
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(contenido);
        textPane.setEditable(false);
        textPane.setBackground(new Color(37, 37, 38));
        textPane.setForeground(Color.WHITE);
        textPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(550, 600));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Resultado del Análisis de Similitud", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Métodos auxiliares
    private double calcularCoeficienteCorrelacion(List<Double> x, List<Double> y) {
        int n = x.size();
        if (n != y.size() || n < 2) return 0;
        
        double sumX = 0, sumY = 0, sumXY = 0;
        double sumX2 = 0, sumY2 = 0;
        
        for (int i = 0; i < n; i++) {
            sumX += x.get(i);
            sumY += y.get(i);
            sumXY += x.get(i) * y.get(i);
            sumX2 += x.get(i) * x.get(i);
            sumY2 += y.get(i) * y.get(i);
        }
        
        double numerador = n * sumXY - sumX * sumY;
        double denominador = Math.sqrt((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY));
        
        return denominador != 0 ? numerador / denominador : 0;
    }
    
    private boolean tieneMismaTendencia() {
        if (datosReales.size() < 2 || datosSimulados.size() < 2) return false;
        
        double pendienteReal = (datosReales.get(datosReales.size()-1).clientes - 
                               datosReales.get(0).clientes) / 4.0;
        double pendienteSimulada = (datosSimulados.get(datosSimulados.size()-1).clientes - 
                                   datosSimulados.get(0).clientes) / 4.0;
        
        return (pendienteReal > 0 && pendienteSimulada > 0) || 
               (pendienteReal < 0 && pendienteSimulada < 0);
    }
    
    private List<Double> encontrarPicos(List<PuntoDato> datos) {
        List<Double> picos = new ArrayList<>();
        if (datos.size() < 3) return picos;
        
        for (int i = 1; i < datos.size() - 1; i++) {
            double crecimientoAnterior = datos.get(i).clientes - datos.get(i-1).clientes;
            double crecimientoSiguiente = datos.get(i+1).clientes - datos.get(i).clientes;
            
            // Pico local: crecimiento alto seguido de crecimiento bajo
            if (crecimientoAnterior > 10 && crecimientoSiguiente < 5) {
                picos.add(datos.get(i).hora);
            }
        }
        
        return picos;
    }
    
    private String formatearHoras(List<Double> horas) {
    if (horas.isEmpty()) return "Ninguno identificado";

    StringBuilder sb = new StringBuilder();
    for (Double hora : horas) {
        int horasEnteras = hora.intValue(); // Forma correcta de convertir Double a int
        int minutos = (int)((hora - horasEnteras) * 60);
        sb.append(String.format("%d:%02d, ", 12 + horasEnteras, minutos));
    }

    // Eliminar la última coma y espacio
    if (sb.length() > 0) {
        sb.setLength(sb.length() - 2);
    }

    return sb.toString();
}
    
    private void aplicarTemaOscuro() {
        Color fondoPrincipal = new Color(45, 45, 48);
        Color fondoSecundario = new Color(37, 37, 38);
        Color texto = Color.WHITE;
        Color borde = new Color(62, 62, 66);
        
        UIManager.put("Panel.background", fondoPrincipal);
        UIManager.put("Label.foreground", texto);
        UIManager.put("Table.background", fondoSecundario);
        UIManager.put("Table.foreground", texto);
        UIManager.put("Table.gridColor", borde);
        UIManager.put("TableHeader.foreground", texto);
        UIManager.put("TableHeader.background", fondoSecundario);
        UIManager.put("TextField.background", fondoSecundario);
        UIManager.put("TextField.foreground", texto);
        UIManager.put("ComboBox.background", fondoSecundario);
        UIManager.put("ComboBox.foreground", texto);
        UIManager.put("Button.background", fondoSecundario);
        UIManager.put("TitledBorder.titleColor", texto);
        UIManager.put("TitledBorder.border", BorderFactory.createLineBorder(borde));
    }
    
    // Clase interna estática para datos de puntos
    private static class PuntoDato {
        double hora;
        int clientes;
        
        PuntoDato(double hora, int clientes) {
            this.hora = hora;
            this.clientes = clientes;
        }
    }
    
    // Panel personalizado para la gráfica
    private class GraficaPanel extends JPanel {
        private static final int PADDING = 70;
        private static final int POINT_SIZE = 8;
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fondo
            g2d.setColor(new Color(37, 37, 38));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            int width = getWidth() - 2 * PADDING;
            int height = getHeight() - 2 * PADDING;
            
            if (width <= 0 || height <= 0) return;
            
            // Dibujar ejes
            g2d.setColor(new Color(200, 200, 200));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(PADDING, PADDING + height, PADDING + width, PADDING + height); // Eje X
            g2d.drawLine(PADDING, PADDING, PADDING, PADDING + height); // Eje Y
            
            // Encontrar máximos para escalar
            int maxClientes = 150; // Máximo fijo para mejor comparación
            double maxHora = 4.0; // 4 horas de simulación
            
            // Dibujar grid
            g2d.setColor(new Color(80, 80, 80));
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
                0, new float[]{5}, 0);
            g2d.setStroke(dashed);
            
            // Grid vertical (horas)
            for (double hora = 0; hora <= maxHora; hora += 0.5) {
                int x = PADDING + (int)((hora / maxHora) * width);
                g2d.drawLine(x, PADDING, x, PADDING + height);
                
                // Etiqueta de hora
                g2d.setColor(new Color(200, 200, 200));
                int horaReal = 12 + (int)hora;
                int minutos = (int)((hora - (int)hora) * 60);
                g2d.drawString(String.format("%d:%02d", horaReal, minutos), x - 15, PADDING + height + 20);
                g2d.setColor(new Color(80, 80, 80));
            }
            
            // Grid horizontal (clientes)
            for (int i = 0; i <= 10; i++) {
                int clientes = i * (maxClientes / 10);
                int y = PADDING + height - (int)(((double)clientes / maxClientes) * height);
                g2d.drawLine(PADDING, y, PADDING + width, y);
                
                // Etiqueta de clientes
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawString(String.valueOf(clientes), PADDING - 35, y + 5);
                g2d.setColor(new Color(80, 80, 80));
            }
            
            g2d.setStroke(new BasicStroke(3));
            
            // Dibujar línea de datos reales (azul)
            if (!datosReales.isEmpty()) {
                g2d.setColor(new Color(0, 122, 204, 220));
                dibujarLinea(g2d, datosReales, width, height, maxHora, maxClientes);
                
                // Dibujar puntos reales
                g2d.setColor(new Color(0, 122, 204));
                for (PuntoDato dato : datosReales) {
                    int x = PADDING + (int)((dato.hora / maxHora) * width);
                    int y = PADDING + height - (int)(((double)dato.clientes / maxClientes) * height);
                    g2d.fill(new Ellipse2D.Double(x - POINT_SIZE/2, y - POINT_SIZE/2, POINT_SIZE, POINT_SIZE));
                }
            }
            
            // Dibujar línea de datos simulados (verde)
            if (!datosSimulados.isEmpty()) {
                g2d.setColor(new Color(87, 166, 74, 220));
                dibujarLinea(g2d, datosSimulados, width, height, maxHora, maxClientes);
                
                // Dibujar puntos simulados
                g2d.setColor(new Color(87, 166, 74));
                for (PuntoDato dato : datosSimulados) {
                    int x = PADDING + (int)((dato.hora / maxHora) * width);
                    int y = PADDING + height - (int)(((double)dato.clientes / maxClientes) * height);
                    g2d.fill(new Ellipse2D.Double(x - POINT_SIZE/2, y - POINT_SIZE/2, POINT_SIZE, POINT_SIZE));
                }
            }
            
            // Títulos de ejes
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2d.drawString("Clientes Atendidos (acumulados)", PADDING - 60, PADDING / 2);
            g2d.drawString("Tiempo (Horas del día)", PADDING + width / 2 - 50, PADDING + height + 40);
            
            // Título de la gráfica
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2d.drawString("Comparación: Datos Reales vs Simulados", 
                getWidth() / 2 - 150, PADDING - 20);
            
            // Leyenda en la gráfica
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            if (!datosReales.isEmpty()) {
                g2d.setColor(new Color(0, 122, 204));
                g2d.fillRect(PADDING + width - 150, PADDING + 20, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Datos Reales", PADDING + width - 130, PADDING + 32);
            }
            
            if (!datosSimulados.isEmpty()) {
                g2d.setColor(new Color(87, 166, 74));
                g2d.fillRect(PADDING + width - 150, PADDING + 45, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Datos Simulados", PADDING + width - 130, PADDING + 57);
            }
        }
        
        private void dibujarLinea(Graphics2D g2d, List<PuntoDato> datos, 
                                 int width, int height, double maxHora, int maxClientes) {
            if (datos.size() < 2) return;
            
            List<PuntoDato> sorted = new ArrayList<>(datos);
            sorted.sort(Comparator.comparingDouble(d -> d.hora));
            
            for (int i = 0; i < sorted.size() - 1; i++) {
                PuntoDato p1 = sorted.get(i);
                PuntoDato p2 = sorted.get(i + 1);
                
                int x1 = PADDING + (int)((p1.hora / maxHora) * width);
                int y1 = PADDING + height - (int)(((double)p1.clientes / maxClientes) * height);
                int x2 = PADDING + (int)((p2.hora / maxHora) * width);
                int y2 = PADDING + height - (int)(((double)p2.clientes / maxClientes) * height);
                
                g2d.drawLine(x1, y1, x2, y2);
            }
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(600, 400);
        }
    }
    
    // Método principal para probar
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulacionTortilleria ventana = new SimulacionTortilleria();
            ventana.setVisible(true);
        });
    }
}