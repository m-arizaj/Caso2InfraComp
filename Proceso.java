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

    void aplicarFiltro(int[][] matrizDatos, int[][] matrizFiltro) {
        int[][] matrizResultado = new int[nf][nc];

        for (int i = 1; i < nf - 1; i++) {
            for (int j = 0; j < nc - 1; j++) {
                int acum = 0;
                for (int a = -1; a <= 1; a++) {
                    for (int b = -1; b <= 1; b++) {
                        int i2 = i + a;
                        int j2 = j + b;
                        int i3 = 1 + a;
                        int j3 = 1 + b;
                        acum += (matrizFiltro[i3][j3] * matrizDatos[i2][j2]);
                    }
                }
                if (acum >= 0 && acum <= 255)
                    matrizResultado[i][j] = acum;
                else if (acum < 0)
                    matrizResultado[i][j] = 0;
                else
                    matrizResultado[i][j] = 255;
            }
        }

        // Asignar valores predefinidos a los bordes
        for (int i = 0; i < nc; i++) {
            matrizResultado[0][i] = 0;
            matrizResultado[nf - 1][i] = 255;
        }
        for (int i = 1; i < nf - 1; i++) {
            matrizResultado[i][0] = 0;
            matrizResultado[i][nc - 1] = 255;
        }

    }
}
