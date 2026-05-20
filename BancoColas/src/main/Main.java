package main;

import controller.ControladorBanco;
import view.VentanaPrincipal;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada del simulador de cajeros bancarios.
 * La UI se crea en el Event Dispatch Thread (EDT) de Swing.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal vista = new VentanaPrincipal();
            new ControladorBanco(vista);
        });
    }
}
