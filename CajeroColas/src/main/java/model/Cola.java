package model;

/**
 * Cola implementada como lista enlazada CIRCULAR.
 *
 * Estructura:
 *   frente → nodo1 → nodo2 → ... → fin
 *                                    ↓
 *                                 frente  (circularidad: fin.sig = frente)
 *
 * Todos los métodos son synchronized para uso seguro entre hilos.
 */
public class Cola {

    private Cliente frente; // Primer elemento (próximo a ser atendido)
    private Cliente fin;    // Último elemento (fin.sig siempre apunta a frente)
    private int     tamaño;

    public Cola() {
        frente = null;
        fin    = null;
        tamaño = 0;
    }

    // ── ENCOLAR (agregar al final) ────────────────────────────────────────────
    public synchronized void encolar(Cliente nuevo) {
        if (frente == null) {
            // Lista vacía: el nodo apunta a sí mismo
            frente = nuevo;
            fin    = nuevo;
            nuevo.setSig(frente);       // circularidad: único nodo → sí mismo
        } else {
            nuevo.setSig(frente);       // nuevo nodo → frente (circularidad)
            fin.setSig(nuevo);          // antiguo fin → nuevo nodo
            fin = nuevo;               // el nuevo nodo pasa a ser el fin
        }
        tamaño++;
    }

    // ── DESENCOLAR (quitar del frente) ────────────────────────────────────────
    public synchronized Cliente desencolar() {
        if (frente == null) return null;

        Cliente aux = frente;

        if (frente == fin) {
            // Solo quedaba un elemento → lista queda vacía
            frente = null;
            fin    = null;
        } else {
            frente = frente.getSig();   // avanzar frente
            fin.setSig(frente);        // mantener circularidad: fin → nuevo frente
        }

        aux.setSig(null); // limpiar referencia del nodo extraído
        tamaño--;
        return aux;
    }

    // ── MOSTRAR (recorrido circular con do-while) ─────────────────────────────
    public synchronized String mostrar() {
        if (frente == null) return "  (vacía)";

        StringBuilder sb  = new StringBuilder();
        Cliente       aux = frente;
        int           pos = 1;

        do {
            // El primero es el que está siendo (o será) atendido
            String prefijo = (pos == 1) ? "► " : "  ";
            sb.append(prefijo).append(aux.toString()).append("\n");
            aux = aux.getSig();
            pos++;
        } while (aux != frente); // condición de parada: dimos la vuelta completa

        return sb.toString();
    }

    // ── Consultas ─────────────────────────────────────────────────────────────
    public synchronized boolean estaVacia()      { return frente == null; }
    public synchronized int     getTamaño()      { return tamaño; }
    public synchronized Cliente getFrente()      { return frente; }
}
