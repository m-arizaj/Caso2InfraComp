import java.util.*;
// import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TablaPaginas {
    List<Pagina> paginas;
    int marcos;
    private final Lock lock = new ReentrantLock();

    TablaPaginas(int marcos) {
        this.marcos = marcos;
        this.paginas = new ArrayList<>();
        for (int i = 0; i < marcos; i++) {
            this.paginas.add(new Pagina(i));
        }
    }

    synchronized void actualizarPagina(int numeroPagina, boolean modificado, boolean referencia) {
        Pagina pagina = paginas.get(numeroPagina);
        pagina.modificado = modificado;
        pagina.referencia = referencia;
    }

    synchronized int reemplazarPagina() {
        for (Pagina pagina : paginas) {
            if (!pagina.referencia) {
                int numeroPagina = pagina.numero;
                pagina.enRAM = true;
                pagina.modificado = false;
                return numeroPagina;
            }
        }
        return -1; // No se encontró ninguna página para reemplazar
    }

    synchronized void actualizarBitR() {
        for (Pagina pagina : paginas) {
            pagina.referencia = false;
        }
    }


    public boolean estaEnRAM(int numeroPagina) {
        try {
            lock.lock();
            for (Pagina pagina : paginas) {
                if (pagina.getNumero() == numeroPagina && pagina.estaEnRAM()) {
                    return true;
                }
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    public int reemplazarPagina(int nuevaPagina) {
        try {
            lock.lock();
            int paginaReemplazada = -1;
            for (Pagina pagina : paginas) {
                if (!pagina.estaEnRAM()) {
                    paginaReemplazada = pagina.getNumero();
                    pagina.setNumero(nuevaPagina);
                    pagina.setEnRAM(true);
                    break;
                }
            }
            return paginaReemplazada;
        } finally {
            lock.unlock();
        }
    }

    public List<Pagina> getPaginas() {
        return paginas;
    }

    public void setPaginas(List<Pagina> paginas) {
        this.paginas = paginas;
    }

    public int getNumeroPaginas() {
        return paginas.size();
    }

    
}
