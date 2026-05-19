package model;

import java.util.Random;

/**
 *
 * @author migue
 */

public class Cajero extends Thread {

    private int id;
    private Cola cola;

    private int usuariosAtendidos;
    private int transaccionesRealizadas;

    private boolean activo;
    private int siguienteCliente;

    public Cajero(int id) {

        this.id = id;

        cola = new Cola();

        usuariosAtendidos = 0;
        transaccionesRealizadas = 0;

        activo = true;
        
        siguienteCliente = 1;
        generarClientesIniciales();
    }

    // GENERAR 4 CLIENTES
    private void generarClientesIniciales() {

        Random r = new Random();

        for (int i = 0; i < 4; i++) {

            int transacciones = r.nextInt(100) + 1;

            cola.encolar(
                    new Cliente(
                            siguienteCliente,
                            transacciones
                    )
            );
            
            siguienteCliente++;
        }    
    }

    public String verCola() {
        return cola.mostrar();
    }

    @Override
    public void run() {

        Random r = new Random();

        while (activo) {

            try {

                Cliente actual = cola.desencolar();

                if (actual != null) {

                    int pendientes = actual.getTransacciones();

                    int realizadas = Math.min(4, pendientes);

                    pendientes -= realizadas;

                    actual.setTransacciones(pendientes);

                    transaccionesRealizadas += realizadas;

                    System.out.println(
                            "Cajero " + id
                            + " atendiendo Cliente "
                            + actual.getId()
                            + " -> " + realizadas
                            + " transacciones"
                    );

                    // SI TERMINA
                    if (pendientes <= 0) {

                        usuariosAtendidos++;

                        int nuevasTrans = r.nextInt(100) + 1;
                        
                        cola.encolar(
                                new Cliente(
                                         siguienteCliente,
                                         nuevasTrans
                                )
                        );
                        
                        siguienteCliente++;

                    } else {

                        // VUELVE AL FINAL
                        cola.encolar(actual);
                    }

                    Thread.sleep(1000);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void detener() {
        activo = false;
    }

    public Cola getCola() {
        return cola;
    }

    public int getUsuariosAtendidos() {
        return usuariosAtendidos;
    }

    public int getTransaccionesRealizadas() {
        return transaccionesRealizadas;
    }

    public int getIdCajero() {
        return id;
    }
}
