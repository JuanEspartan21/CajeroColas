package controller;

import model.Cajero;
import view.VentanaPrincipal;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 *
 * @author migue
 */

public class ControladorBanco {

    private VentanaPrincipal vista;

    private Cajero c1;
    private Cajero c2;
    private Cajero c3;
    private Cajero c4;

    private Timer timerActualizacion;
    private Timer timerTiempo;

    private int tiempoRestante = 300; // 5 minutos

    public ControladorBanco(VentanaPrincipal vista) {

        this.vista = vista;

        iniciarEventos();
    }

    // EVENTOS
    private void iniciarEventos() {

        vista.btnIniciar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                iniciarSimulacion();
            }
        });
    }

    // INICIAR SIMULACIÓN
    private void iniciarSimulacion() {

        // EVITAR INICIAR DOS VECES
        vista.btnIniciar.setEnabled(false);

        // CREAR CAJEROS
        c1 = new Cajero(1);
        c2 = new Cajero(2);
        c3 = new Cajero(3);
        c4 = new Cajero(4);

        // INICIAR HILOS
        c1.start();
        c2.start();
        c3.start();
        c4.start();

        // ACTUALIZAR INTERFAZ
        iniciarActualizacionVisual();

        // TEMPORIZADOR
        iniciarTemporizador();
    }

    // ACTUALIZAR COLAS EN PANTALLA
    private void iniciarActualizacionVisual() {

        timerActualizacion = new Timer(500, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                vista.area1.setText(c1.verCola());

                vista.area2.setText(c2.verCola());

                vista.area3.setText(c3.verCola());

                vista.area4.setText(c4.verCola());
            }
        });

        timerActualizacion.start();
    }

    // TEMPORIZADOR GENERAL
    private void iniciarTemporizador() {

        timerTiempo = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                tiempoRestante--;

                int minutos = tiempoRestante / 60;

                int segundos = tiempoRestante % 60;

                vista.lblTiempo.setText(
                        String.format(
                                "Tiempo restante: %02d:%02d",
                                minutos,
                                segundos
                        )
                );

                // TERMINAR SIMULACIÓN
                if (tiempoRestante <= 0) {

                    finalizarSimulacion();
                }
            }
        });

        timerTiempo.start();
    }

    // FINALIZAR
    private void finalizarSimulacion() {

        timerActualizacion.stop();
        timerTiempo.stop();

        c1.detener();
        c2.detener();
        c3.detener();
        c4.detener();

        mostrarResultadosFinales();
    }

    // RESULTADOS
    private void mostrarResultadosFinales() {

        String resultados = "";

        resultados += generarTextoResultados(c1);
        resultados += generarTextoResultados(c2);
        resultados += generarTextoResultados(c3);
        resultados += generarTextoResultados(c4);

        JOptionPane.showMessageDialog(
                vista,
                resultados,
                "RESULTADOS FINALES",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String generarTextoResultados(Cajero c) {

        return
                "CAJERO " + c.getIdCajero() + "\n" +
                "Usuarios atendidos: " +
                c.getUsuariosAtendidos() + "\n" +

                "Transacciones realizadas: " +
                c.getTransaccionesRealizadas() +

                "\n\n";
    }
}
