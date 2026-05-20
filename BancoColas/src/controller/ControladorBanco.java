package controller;

import model.Cajero;
import model.Cliente;
import view.VentanaPrincipal;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Controlador principal del simulador.
 *
 * Flujo:
 *  - Un clic en "INICIAR" arranca todo.
 *  - Cada 10–15 segundos (aleatorio) se agrega automáticamente un nuevo lote
 *    de 1–20 clientes distribuidos equitativamente entre los 4 cajeros.
 *  - La simulación termina únicamente cuando el reloj llega a 00:00.
 *  - Si las colas se vacían entre lotes, los cajeros quedan en espera.
 */
public class ControladorBanco {

    private final VentanaPrincipal vista;
    private final Cajero[]         cajeros = new Cajero[4];

    private javax.swing.Timer timerUI;      // refresco de interfaz (500 ms)
    private javax.swing.Timer timerClock;   // cuenta regresiva (1 s)
    private javax.swing.Timer timerLote;    // agrega clientes automáticamente

    private int     tiempoRestante   = 300;   // segundos (5 minutos)
    private boolean simulacionFin    = false;
    private int     totalAcumulado   = 0;

    private final Random rng = new Random();

    /** Cola thread-safe compartida con los cajeros para el log de eventos. */
    private final ConcurrentLinkedQueue<String> eventLog = new ConcurrentLinkedQueue<>();

    public ControladorBanco(VentanaPrincipal vista) {
        this.vista = vista;
        DefaultCaret caret = (DefaultCaret) vista.areaLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        vista.btnIniciar.addActionListener(e -> iniciarSimulacion());
    }

    // ── Arranque ──────────────────────────────────────────────────────────────
    private void iniciarSimulacion() {
        vista.btnIniciar.setEnabled(false);

        // Crear y lanzar los 4 cajeros simultáneamente
        for (int i = 0; i < 4; i++) cajeros[i] = new Cajero(i + 1, eventLog);
        for (Cajero c : cajeros)    c.start();

        iniciarTimerUI();
        iniciarReloj();
        iniciarTimerLotes(); // primer lote inmediato + lotes periódicos
    }

    // ── Timer de lotes automáticos ────────────────────────────────────────────
    private void iniciarTimerLotes() {
        // Primer lote inmediato al arrancar
        generarYDistribuir();

        // Programar el primer intervalo aleatorio (15 000–20 000 ms)
        programarSiguienteLote();
    }

    /**
     * Programa un disparo único con intervalo aleatorio 15-20 s.
     * Al disparar, agrega un lote y se reprograma a sí mismo.
     */
    private void programarSiguienteLote() {
        int intervalo = 15_000 + rng.nextInt(5_001); // 15 000–20 000 ms

        timerLote = new javax.swing.Timer(intervalo, e -> {
            timerLote.stop(); // era un disparo único
            if (!simulacionFin) {
                generarYDistribuir();
                programarSiguienteLote(); // reprogramar el siguiente
            }
        });
        timerLote.setRepeats(false);
        timerLote.start();
    }

    // ── Generación y distribución de clientes (1–20) ──────────────────────────
    private void generarYDistribuir() {
        Set<Integer> usados = new HashSet<>();

        int lote = rng.nextInt(10) + 1; // 1–20
        totalAcumulado += lote;

        vista.lblTotal.setText("Clientes acumulados: " + totalAcumulado);

        eventLog.add("╔══════════════════════════════════════════════════════╗");
        eventLog.add(String.format(
            "  Nuevo lote: %2d cliente(s)  |  Total acumulado: %d",
            lote, totalAcumulado));
        eventLog.add("╚══════════════════════════════════════════════════════╝");

        int base  = lote / 4;
        int extra = lote % 4;

        for (int i = 0; i < 4; i++) {
            int cantidad = base + (i < extra ? 1 : 0);

            for (int j = 0; j < cantidad; j++) {
                int id;
                do { id = 100 + rng.nextInt(900); }
                while (usados.contains(id));
                usados.add(id);

                int    trans = rng.nextInt(49) + 1;
                String tipo  = Cliente.TIPOS[rng.nextInt(Cliente.TIPOS.length)];
                cajeros[i].agregarCliente(new Cliente(id, trans, tipo));
            }

            eventLog.add(String.format("  Cajero %d: +%d cliente(s)", i + 1, cantidad));
        }
        eventLog.add("──────────────────────────────────────────────────────");
    }

    // ── Timer de refresco visual (500 ms) ─────────────────────────────────────
    private void iniciarTimerUI() {
        JTextArea[] areas = { vista.area1, vista.area2, vista.area3, vista.area4 };

        timerUI = new javax.swing.Timer(500, e -> {
            for (int i = 0; i < 4; i++) {
                areas[i].setText(cajeros[i].verCola());
                vista.lblEstado[i].setText(cajeros[i].getEstado());
                vista.lblStats[i].setText(String.format(
                    "Atendidos: %d  |  Transacciones: %d",
                    cajeros[i].getUsuariosAtendidos(),
                    cajeros[i].getTransaccionesRealizadas()
                ));
            }
            String msg;
            while ((msg = eventLog.poll()) != null) {
                vista.areaLog.append(msg + "\n");
            }
        });
        timerUI.start();
    }

    // ── Reloj de cuenta regresiva ─────────────────────────────────────────────
    private void iniciarReloj() {
        timerClock = new javax.swing.Timer(1000, e -> {
            tiempoRestante--;
            int min = tiempoRestante / 60;
            int seg = tiempoRestante % 60;
            vista.lblTiempo.setText(String.format("⏱  %02d:%02d", min, seg));
            if (tiempoRestante <= 0) finalizarSimulacion();
        });
        timerClock.start();
    }

    // ── Finalización por tiempo ───────────────────────────────────────────────
    private synchronized void finalizarSimulacion() {
        if (simulacionFin) return;
        simulacionFin = true;

        if (timerUI    != null) timerUI.stop();
        if (timerClock != null) timerClock.stop();
        if (timerLote  != null) timerLote.stop();

        for (Cajero c : cajeros) c.detener();

        // Actualización final de la UI
        JTextArea[] areas = { vista.area1, vista.area2, vista.area3, vista.area4 };
        for (int i = 0; i < 4; i++) {
            areas[i].setText(cajeros[i].verCola());
            vista.lblEstado[i].setText(cajeros[i].getEstado());
            vista.lblStats[i].setText(String.format(
                "Atendidos: %d  |  Transacciones: %d",
                cajeros[i].getUsuariosAtendidos(),
                cajeros[i].getTransaccionesRealizadas()
            ));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════ SIMULACIÓN COMPLETADA ══════════════╗\n\n");
        sb.append(String.format("  Clientes totales ingresados: %d\n\n", totalAcumulado));

        int totalAt = 0, totalTr = 0;
        for (Cajero c : cajeros) {
            int at = c.getUsuariosAtendidos();
            int tr = c.getTransaccionesRealizadas();
            sb.append(String.format(
                "  CAJERO %d:  %3d usuario(s)  |  %4d transacciones\n",
                c.getIdCajero(), at, tr
            ));
            totalAt += at;
            totalTr += tr;
        }
        sb.append(String.format(
            "\n  TOTAL:     %3d usuario(s)  |  %4d transacciones\n", totalAt, totalTr));
        sb.append("╚═══════════════════════════════════════════════════╝\n");

        vista.areaLog.append(sb.toString());
        JOptionPane.showMessageDialog(vista, sb.toString(),
            "Simulación Completada", JOptionPane.INFORMATION_MESSAGE);
    }
}
