// import java.util.*;
// import java.util.concurrent.*;

public class Pagina {
    int numero;
    boolean enRAM;
    boolean modificado;
    boolean referencia;

    Pagina(int numero) {
        this.numero = numero;
        this.enRAM = false;
        this.modificado = false;
        this.referencia = false;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public boolean estaEnRAM() {
        return enRAM;
    }

    public void setEnRAM(boolean enRAM) {
        this.enRAM = enRAM;
    }

    public boolean isModificado() {
        return modificado;
    }

    public void setModificado(boolean modificado) {
        this.modificado = modificado;
    }

    public boolean isReferencia() {
        return referencia;
    }

    public void setReferencia(boolean referencia) {
        this.referencia = referencia;
    }

    
}
