package model;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Cajero bancario — hilo independiente (Thread).
 *
 * Cada cajero:
 *  - Mantiene su propia cola circular de clientes.
 *  - Atiende hasta 4 transacciones por turno.
 *  - Si al cliente le quedan transacciones, vuelve al final de la cola.
 *  - Registra eventos en un log compartido (thread-safe) en lugar de consola.
 *  - Los clientes son asignados externamente por el controlador.
 */
public class Cajero extends Thread {

    private final int  id;
    private final Cola cola;

    private volatile int    usuariosAtendidos;
    private volatile int    transaccionesRealizadas;
    private volatile boolean activo;
    private volatile String  estado;

    /** Log compartido con el controlador (ConcurrentLinkedQueue es thread-safe). */
    private final ConcurrentLinkedQueue<String> log;

    public Cajero(int id, ConcurrentLinkedQueue<String> log) {
        this.id                      = id;
        this.cola                    = new Cola();
        this.usuariosAtendidos       = 0;
        this.transaccionesRealizadas = 0;
        this.activo                  = true;
        this.estado                  = "⏳ En espera...";
        this.log                     = log;
        setDaemon(true); // no bloquea el cierre de la aplicación
    }

    // ── API pública ───────────────────────────────────────────────────────────

    /** Agrega un cliente al final de la cola (llamado desde el controlador). */
    public void agregarCliente(Cliente c) {
        cola.encolar(c);
    }

    /** Devuelve el contenido de la cola para mostrar en la interfaz. */
    public String verCola() {
        return cola.mostrar();
    }

    // ── Hilo principal ────────────────────────────────────────────────────────
    @Override
    public void run() {
        while (activo) {
            try {
                if (cola.estaVacia()) {
                    estado = "⏳ En espera...";
                    Thread.sleep(400);
                    continue;
                }

                // Atender al cliente del frente
                Cliente actual = cola.desencolar();
                if (actual == null) continue;

                estado = String.format("⚡ Atendiendo [%03d] %s", actual.getId(), actual.getTipo());

                // Requisito: se atienden máximo 4 transacciones por turno
                int pendientes = actual.getTransacciones();
                int realizadas = Math.min(4, pendientes);
                pendientes -= realizadas;
                actual.setTransacciones(pendientes);
                transaccionesRealizadas += realizadas;

                // Registrar evento en el log (NO en consola)
                log.add(String.format(
                    "Cajero %d ▸ [%03d] %-14s | %d trans. realizadas%s",
                    id,
                    actual.getId(),
                    actual.getTipo(),
                    realizadas,
                    pendientes > 0
                        ? " | " + pendientes + " pendiente(s) → vuelve a la cola"
                        : " ✓ Completado"
                ));

                if (pendientes <= 0) {
                    // Cliente atendido por completo: sale del sistema
                    usuariosAtendidos++;
                } else {
                    // Aún tiene transacciones: vuelve al final de la cola
                    cola.encolar(actual);
                }

                Thread.sleep(1200); // simular tiempo de atención

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        estado = "✅ Finalizado";
    }

    /** Detiene el hilo de forma limpia. */
    public void detener() {
        activo = false;
        interrupt();
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public int     getIdCajero()               { return id; }
    public int     getUsuariosAtendidos()      { return usuariosAtendidos; }
    public int     getTransaccionesRealizadas(){ return transaccionesRealizadas; }
    public String  getEstado()                 { return estado; }

    /**
     * True si la cola está vacía Y el cajero no está actualmente atendiendo.
     * Usado por el controlador para detectar que terminó su trabajo.
     */
    public boolean estaIdle() {
        return cola.estaVacia() && !estado.startsWith("⚡");
    }
}
