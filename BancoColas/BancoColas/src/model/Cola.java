package model;

/**
 *
 * @author migue
 */
public class Cola {

    private Cliente frente;
    private Cliente fin;
    private int tamaño;

    public Cola() {
        frente = null;
        fin = null;
        tamaño = 0;
    }

    // ENCOLAR
    public void encolar(Cliente nuevo) {

        if (frente == null) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.setSig(nuevo);
            fin = nuevo;
        }

        tamaño++;
    }

    // DESENCOLAR
    public Cliente desencolar() {

        if (frente == null) {
            return null;
        }

        Cliente aux = frente;
        frente = frente.getSig();

        if (frente == null) {
            fin = null;
        }

        aux.setSig(null);

        tamaño--;

        return aux;
    }

    // MOSTRAR
    public String mostrar() {

        if (frente == null) {
            return "Vacía";
        }

        String texto = "";

        Cliente aux = frente;

        while (aux != null) {

            texto += aux.toString() + "\n";

            aux = aux.getSig();
        }

        return texto;
    }

    public int getTamaño() {
        return tamaño;
    }

    public Cliente getFrente() {
        return frente;
    }
}