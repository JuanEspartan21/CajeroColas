package view;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author migue
 */

public class VentanaPrincipal extends JFrame {

    public JTextArea area1;
    public JTextArea area2;
    public JTextArea area3;
    public JTextArea area4;

    public JButton btnIniciar;

    public JLabel lblTiempo;

    public VentanaPrincipal() {

        setTitle("SIMULADOR BANCO");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        iniciarComponentes();

        setVisible(true);
    }

    private void iniciarComponentes() {

        setLayout(new BorderLayout());

        // PANEL SUPERIOR
        JPanel superior = new JPanel();

        lblTiempo = new JLabel("Tiempo restante: 05:00");

        superior.add(lblTiempo);

        add(superior, BorderLayout.NORTH);

        // PANEL CENTRAL
        JPanel centro = new JPanel();

        centro.setLayout(new GridLayout(2, 2, 10, 10));

        area1 = crearArea("CAJERO 1");
        area2 = crearArea("CAJERO 2");
        area3 = crearArea("CAJERO 3");
        area4 = crearArea("CAJERO 4");

        centro.add(new JScrollPane(area1));
        centro.add(new JScrollPane(area2));
        centro.add(new JScrollPane(area3));
        centro.add(new JScrollPane(area4));

        add(centro, BorderLayout.CENTER);

        // PANEL INFERIOR
        JPanel inferior = new JPanel();

        btnIniciar = new JButton("INICIAR SIMULACIÓN");

        inferior.add(btnIniciar);

        add(inferior, BorderLayout.SOUTH);
    }

    private JTextArea crearArea(String titulo) {

        JTextArea area = new JTextArea();

        area.setBorder(
                BorderFactory.createTitledBorder(titulo)
        );

        area.setEditable(false);

        area.setFont(new Font("Monospaced", Font.PLAIN, 14));

        return area;
    }
}
