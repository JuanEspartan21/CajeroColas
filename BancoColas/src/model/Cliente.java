package model;

/**
 * Nodo de la lista enlazada circular.
 * Representa un cliente en cola para un cajero bancario.
 *
 * Campos del nodo:
 *   - id           : 3 dígitos aleatorios (100-999)
 *   - transacciones: número de transacciones pendientes (1-99)
 *   - tipo         : tipo de transacción (variable tipo nodo)
 *   - sig          : puntero al siguiente nodo (estructura circular)
 */
public class Cliente {

    // Tipos de transacción posibles (variable tipo nodo)
    public static final String[] TIPOS = {
        "RETIRO", "DEPOSITO", "CONSULTA", "TRANSFERENCIA"
    };

    private final int    id;            // 3 dígitos aleatorios
    private       int    transacciones; // 1-99
    private final String tipo;          // tipo de nodo
    private       Cliente sig;          // siguiente en la lista circular

    public Cliente(int id, int transacciones, String tipo) {
        this.id            = id;
        this.transacciones = transacciones;
        this.tipo          = tipo;
        this.sig           = null;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public int     getId()            { return id; }
    public int     getTransacciones() { return transacciones; }
    public String  getTipo()          { return tipo; }
    public Cliente getSig()           { return sig; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setTransacciones(int t) { this.transacciones = t; }
    public void setSig(Cliente sig)     { this.sig = sig; }

    @Override
    public String toString() {
        return String.format("[%03d] %-14s | Trans: %2d", id, tipo, transacciones);
    }
}
