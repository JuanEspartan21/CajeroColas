package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Ventana principal del simulador de cajeros bancarios.
 *
 * Layout:
 *  ┌── ENCABEZADO (título, timer, total clientes) ──────────────────────┐
 *  │  CAJERO 1            │  CAJERO 2                                   │
 *  │  [estado]            │  [estado]                                   │
 *  │  [cola JTextArea]    │  [cola JTextArea]                           │
 *  │  [stats]             │  [stats]                                    │
 *  │──────────────────────┼─────────────────────────────────────────────│
 *  │  CAJERO 3            │  CAJERO 4                                   │
 *  │  ...                 │  ...                                        │
 *  ├── REGISTRO DE EVENTOS (log) ────────────────────────────────────────┤
 *  └── BOTÓN INICIAR ────────────────────────────────────────────────────┘
 */
public class VentanaPrincipal extends JFrame {

    // ── Componentes accesibles por el controlador ─────────────────────────────
    public JTextArea area1, area2, area3, area4;  // colas de cada cajero
    public JTextArea areaLog;                      // registro de eventos
    public JButton   btnIniciar;
    public JLabel    lblTiempo;                    // cuenta regresiva
    public JLabel    lblTotal;                     // total clientes generados
    public JLabel[]  lblEstado = new JLabel[4];    // estado de cada cajero
    public JLabel[]  lblStats  = new JLabel[4];    // stats de cada cajero

    // ── Paleta de colores ─────────────────────────────────────────────────────
    private static final Color BG_DARK    = new Color(15,  17,  26);
    private static final Color BG_PANEL   = new Color(24,  28,  44);
    private static final Color BG_CAJERO  = new Color(20,  24,  38);
    private static final Color BG_HEADER  = new Color(18,  52,  99);
    private static final Color ACCENT     = new Color(55, 130, 215);
    private static final Color TEXT_MAIN  = new Color(210, 225, 255);
    private static final Color TEXT_DIM   = new Color(120, 145, 185);
    private static final Color TEXT_GREEN = new Color(90,  210, 130);
    private static final Color TEXT_GOLD  = new Color(255, 205,  70);
    private static final Color TEXT_CYAN  = new Color( 80, 200, 220);
    private static final Color LOG_GREEN  = new Color(140, 210, 140);
    private static final Color BORDER_COL = new Color(45,  75, 130);

    public VentanaPrincipal() {
        setTitle("Simulador de Cajeros Bancarios");
        setSize(1100, 800);
        setMinimumSize(new Dimension(900, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        iniciarComponentes();
        setVisible(true);
    }

    // ── Construcción de la UI ─────────────────────────────────────────────────
    private void iniciarComponentes() {
        setLayout(new BorderLayout(8, 8));
        ((JPanel) getContentPane()).setBorder(
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );
        add(crearEncabezado(),     BorderLayout.NORTH);
        add(crearGridCajeros(),    BorderLayout.CENTER);
        add(crearPanelInferior(),  BorderLayout.SOUTH);
    }

    // ── ENCABEZADO ────────────────────────────────────────────────────────────
    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(BG_HEADER);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 1),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        JLabel titulo = new JLabel("🏦  SIMULADOR DE CAJEROS BANCARIOS", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(TEXT_MAIN);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
        barra.setOpaque(false);

        lblTiempo = mkLabel("⏱  05:00", TEXT_GOLD, 15, Font.BOLD);
        lblTotal  = mkLabel("Clientes generados: —", TEXT_GREEN, 13, Font.PLAIN);

        barra.add(lblTiempo);
        barra.add(mkSep());
        barra.add(lblTotal);

        panel.add(titulo, BorderLayout.CENTER);
        panel.add(barra,  BorderLayout.SOUTH);
        return panel;
    }

    // ── GRID 2×2 DE CAJEROS ───────────────────────────────────────────────────
    private JPanel crearGridCajeros() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 8, 8));
        grid.setBackground(BG_DARK);

        area1 = new JTextArea(); lblEstado[0] = new JLabel(); lblStats[0] = new JLabel();
        area2 = new JTextArea(); lblEstado[1] = new JLabel(); lblStats[1] = new JLabel();
        area3 = new JTextArea(); lblEstado[2] = new JLabel(); lblStats[2] = new JLabel();
        area4 = new JTextArea(); lblEstado[3] = new JLabel(); lblStats[3] = new JLabel();

        grid.add(crearPanelCajero("CAJERO 1", area1, lblEstado[0], lblStats[0]));
        grid.add(crearPanelCajero("CAJERO 2", area2, lblEstado[1], lblStats[1]));
        grid.add(crearPanelCajero("CAJERO 3", area3, lblEstado[2], lblStats[2]));
        grid.add(crearPanelCajero("CAJERO 4", area4, lblEstado[3], lblStats[3]));

        return grid;
    }

    /**
     * Panel individual de un cajero:
     *   [header azul] → [lblEstado] → [scroll con cola] → [lblStats]
     */
    private JPanel crearPanelCajero(String titulo, JTextArea area,
                                    JLabel lblEstado, JLabel lblStats) {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BG_CAJERO);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COL, 1));

        // Encabezado del cajero
        JLabel header = new JLabel("  " + titulo, SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(ACCENT);
        header.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        // Label de estado (cliente en atención)
        lblEstado.setText("⏳ En espera...");
        lblEstado.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblEstado.setForeground(TEXT_CYAN);
        lblEstado.setOpaque(true);
        lblEstado.setBackground(new Color(18, 28, 48));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        // Parte superior: header + estado
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(header,    BorderLayout.NORTH);
        top.add(lblEstado, BorderLayout.SOUTH);

        // Área de la cola (lista de espera)
        area.setBackground(BG_CAJERO);
        area.setForeground(TEXT_MAIN);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        area.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(null);
        scroll.setBackground(BG_CAJERO);
        scroll.getViewport().setBackground(BG_CAJERO);

        // Label de estadísticas (pie del panel)
        lblStats.setText("Atendidos: 0  |  Transacciones: 0");
        lblStats.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblStats.setForeground(TEXT_DIM);
        lblStats.setOpaque(true);
        lblStats.setBackground(new Color(12, 15, 25));
        lblStats.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        panel.add(top,    BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(lblStats, BorderLayout.SOUTH);

        return panel;
    }

    // ── PANEL INFERIOR (Log + Botón) ──────────────────────────────────────────
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(BG_DARK);

        // Log de eventos
        JPanel logWrapper = new JPanel(new BorderLayout());
        logWrapper.setBackground(BG_PANEL);
        logWrapper.setBorder(BorderFactory.createLineBorder(BORDER_COL, 1));

        JLabel logTitulo = new JLabel("  📋  REGISTRO DE EVENTOS", SwingConstants.LEFT);
        logTitulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        logTitulo.setForeground(TEXT_MAIN);
        logTitulo.setOpaque(true);
        logTitulo.setBackground(new Color(30, 38, 62));
        logTitulo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        areaLog = new JTextArea(6, 0);
        areaLog.setBackground(new Color(10, 12, 20));
        areaLog.setForeground(LOG_GREEN);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaLog.setEditable(false);

        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(null);
        scrollLog.getViewport().setBackground(areaLog.getBackground());

        logWrapper.add(logTitulo, BorderLayout.NORTH);
        logWrapper.add(scrollLog, BorderLayout.CENTER);

        // Botón iniciar
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        btnPanel.setBackground(BG_DARK);

        btnIniciar = new JButton("▶   INICIAR SIMULACIÓN");
        btnIniciar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnIniciar.setBackground(new Color(35, 110, 70));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFocusPainted(false);
        btnIniciar.setBorderPainted(false);
        btnIniciar.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        btnIniciar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Efecto hover
        btnIniciar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnIniciar.setBackground(new Color(45, 145, 90));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnIniciar.setBackground(new Color(35, 110, 70));
            }
        });

        btnPanel.add(btnIniciar);

        panel.add(logWrapper, BorderLayout.CENTER);
        panel.add(btnPanel,   BorderLayout.SOUTH);

        return panel;
    }

    // ── Utilidades ────────────────────────────────────────────────────────────
    private JLabel mkLabel(String txt, Color color, int size, int style) {
        JLabel lbl = new JLabel(txt);
        lbl.setFont(new Font("SansSerif", style, size));
        lbl.setForeground(color);
        return lbl;
    }

    private JSeparator mkSep() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 20));
        sep.setForeground(BORDER_COL);
        return sep;
    }
}
