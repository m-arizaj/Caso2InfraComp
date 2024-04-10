// import java.util.*;
// import java.util.concurrent.*;

public class Proceso {
    int[][] referencias;
    int nf;
    int nc;

    Proceso(int nf, int nc) {
        this.nf = nf;
        this.nc = nc;
        this.referencias = new int[nf][nc];
    }

    int[][] obtenerReferencias() {
        return this.referencias;
    }

    
}
