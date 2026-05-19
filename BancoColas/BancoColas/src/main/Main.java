package main;

import controller.ControladorBanco;
import view.VentanaPrincipal;

/**
 *
 * @author migue
 */
public class Main {
    
    public static void main(String[] args) {
        
        VentanaPrincipal vista = new VentanaPrincipal();
        
        new ControladorBanco(vista);
    }
}
