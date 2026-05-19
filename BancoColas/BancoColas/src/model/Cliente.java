package model;

/**
 *
 * @author migue
 */
public class Cliente {

    private int id;
    private int transacciones;
    private Cliente sig;

    public Cliente(int id, int transacciones) {
        this.id = id;
        this.transacciones = transacciones;
        this.sig = null;
    }

    public int getId() {
        return id;
    }

    public int getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(int transacciones) {
        this.transacciones = transacciones;
    }

    public Cliente getSig() {
        return sig;
    }

    public void setSig(Cliente sig) {
        this.sig = sig;
    }

    @Override
    public String toString() {
        return 
                "Cliente " + id + 
                " | Transacciones: " +
                transacciones;
    }
}