import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class AdministradorMemoria {

    static final long TIEMPO_ACCESO_RAM_NS = 30;
    static final long TIEMPO_ACCESO_SWAP_MS = 10; 
    static Object lockTablaPaginas = new Object();

    static void accesoRAM() {
        try {
            Thread.sleep(TIEMPO_ACCESO_RAM_NS); // Simular el tiempo de acceso a RAM
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void accesoSWAP() {
        try {
            Thread.sleep(TIEMPO_ACCESO_SWAP_MS); // Simular el tiempo de acceso a SWAP
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
       Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("Menú:");
            System.out.println("1. Generación de referencias");
            System.out.println("2. Calcular datos");
            System.out.println("3. Salir");
            System.out.print("Ingrese su opción: ");
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    generarReferencias();
                    break;
                case 2:
                    System.out.print("Ingrese el número de marcos de página: ");
                    int numeroMarcosPagina = scanner.nextInt();
                    System.out.print("Ingrese el nombre del archivo de referencias: ");
                    String nombreArchivoReferencias = scanner.next();
                    calcularDatosConThreads(numeroMarcosPagina, nombreArchivoReferencias);
                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción no válida");
                    break;
            }
        }
        
    }

    static void generarReferencias() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el tamaño de la página: ");
        int tamanoPagina = scanner.nextInt();
        System.out.println(tamanoPagina);
        System.out.print("Ingrese el número de filas de la matriz de datos: ");
        int nf = scanner.nextInt();
        System.out.println(nf);
        int nc = nf;

        int paginasFiltro = calcularPaginas(nf, nc, tamanoPagina);
        int paginasDatos = calcularPaginas(nf, nc, tamanoPagina);
        int paginasResultado = calcularPaginas(nf, nc, tamanoPagina);

        int totalReferencias = nf * nc * 3; // Tres matrices: filtro, datos y resultado
        int totalPaginas = paginasFiltro + paginasDatos + paginasResultado;
        try {
            PrintWriter writer = new PrintWriter(new File("referencias.txt"));
            writer.println("TP: " + tamanoPagina);
            writer.println("NF: " + nf);
            writer.println("NC: " + nc);
            writer.println("NR: " + totalReferencias);
            writer.println("NP: " + totalPaginas);

            // Generar referencias para el filtro
            generarReferenciasMatriz(3, 3, tamanoPagina, 0, writer);

            // Generar referencias para los datos
            generarReferenciasMatriz(nf, nc, tamanoPagina, paginasFiltro, writer);

            // Generar referencias para el resultado
            generarReferenciasMatriz(nf, nc, tamanoPagina, paginasFiltro + paginasDatos, writer);

            writer.close();
            System.out.println("Referencias generadas y guardadas en referencias.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        scanner.close();
    }

    static void calcularDatosConThreads(int numeroMarcosPagina, String nombreArchivoReferencias) {
        Thread hiloTablaPaginas = new Thread(new ThreadTablaPaginas(numeroMarcosPagina, nombreArchivoReferencias));

        hiloTablaPaginas.start();

        try {
            hiloTablaPaginas.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Esto aun no funciona ->
    static class ThreadTablaPaginas implements Runnable {
        private TablaPaginas tablaPaginas;
        private String nombreArchivoReferencias;

        public ThreadTablaPaginas(int numeroMarcosPagina, String nombreArchivoReferencias) {
            this.tablaPaginas = new TablaPaginas(numeroMarcosPagina);
            this.nombreArchivoReferencias = nombreArchivoReferencias;
        }

        @Override
        public void run() {
            try {
                File file = new File(nombreArchivoReferencias);
                Scanner scanner = new Scanner(file);
        
                while (scanner.hasNextLine()) {
                    String linea = scanner.nextLine();
                    // Procesar la línea para obtener la referencia a la página
                    int paginaReferenciada = Integer.parseInt(linea); // Suponiendo que el archivo contiene solo números que representan páginas
        
                    // Verificar si la página está presente en la memoria RAM (hit) o si es necesario cargarla (miss)
                    synchronized (lockTablaPaginas) {
                        if (paginaReferenciada < tablaPaginas.getNumeroPaginas()) {
                            // La página está dentro del rango de páginas válidas
                            if (tablaPaginas.estaEnRAM(paginaReferenciada)) {
                                // La página está en la memoria RAM (hit)
                                System.out.println("Hit - Página " + paginaReferenciada);
                            } else {
                                // La página no está en la memoria RAM (miss)
                                System.out.println("Miss - Página " + paginaReferenciada);
                                // Simular el reemplazo de la página y actualizar la tabla de páginas
                                int paginaReemplazada = tablaPaginas.reemplazarPagina(paginaReferenciada);
                                System.out.println("Página " + paginaReemplazada + " reemplazada por página " + paginaReferenciada);
                            }
                        } else {
                            // La página está fuera del rango de páginas válidas
                            System.out.println("Referencia a página fuera del rango válido: " + paginaReferenciada);
                        }
                    }
        
                    // Actualizar el bit R cada cuatro milisegundos
                    TimeUnit.MILLISECONDS.sleep(4);
                }
        
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void calcularDatos(int numeroMarcosPagina, String nombreArchivoReferencias) {
        // Crear una instancia de TablaPaginas para simular la tabla de páginas
        TablaPaginas tablaPaginas = new TablaPaginas(numeroMarcosPagina);

        int totalReferencias = 0;
        int hits = 0;
        int fallasPagina = 0;

        // Leer el archivo de referencias
        try {
            File archivo = new File(nombreArchivoReferencias);
            Scanner scanner = new Scanner(archivo);
            // Leer los metadatos del archivo de referencias
            scanner.next(); // Saltar "TP:"
            int tamañoPagina = scanner.nextInt();
            scanner.next(); // Saltar "NF:"
            int nf = scanner.nextInt();
            scanner.next(); // Saltar "NC:"
            int nc = scanner.nextInt();
            scanner.next(); // Saltar "NR:"
            totalReferencias = scanner.nextInt();
            scanner.next(); // Saltar "NP:"
            int totalPaginas = scanner.nextInt();
            
            while (scanner.hasNext()) {
                String referencia = scanner.next();
                int paginaReferenciada = Integer.parseInt(referencia); // Convertir la referencia a un número de página
            
                // Procesar la referencia y simular el comportamiento del sistema de paginación
                synchronized (lockTablaPaginas) {
                    if (tablaPaginas.estaEnRAM(paginaReferenciada)) {
                        // La página está en la memoria RAM (hit)
                        hits++;
                    } else {
                        // La página no está en la memoria RAM (miss)
                        // Incrementar el contador de fallas de página
                        fallasPagina++;
            
                        // Simular el reemplazo de la página y actualizar la tabla de páginas
                        int paginaReemplazada = tablaPaginas.reemplazarPagina(paginaReferenciada);
                    }
                }

                // Simular el acceso a la página correspondiente
                // Si hay un hit, incrementar la variable hits; si hay una falla de página, incrementar la variable fallasPagina
                // Aquí se debería implementar la lógica para el algoritmo de envejecimiento y el reemplazo de páginas

                // Solo para propósitos de ejemplo, asumimos que todas las referencias son fallas de página
                fallasPagina++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Calcular el porcentaje de hits
        double porcentajeHits = ((double) hits / totalReferencias) * 100;

        // Imprimir resultados
        System.out.println("Número de referencias: " + totalReferencias);
        System.out.println("Número de hits: " + hits);
        System.out.println("Número de fallas de página: " + fallasPagina);
        System.out.println("Porcentaje de hits: " + porcentajeHits + "%");

        // Calcular y mostrar tiempos aquí si es necesario
    }
    // -> Hasta aqui

    public static int calcularPaginas(int nf, int nc, int tamañoPagina) {
        int tamañoMatriz = nf * nc * 4; // Cada elemento es un entero de 4 bytes
        int paginas = tamañoMatriz / tamañoPagina;
        if (tamañoMatriz % tamañoPagina != 0) {
            paginas++; // Añadir una página adicional si no se ajusta exactamente al tamaño de página
        }
        return paginas;
    }

    public static void generarReferenciasMatriz(int nf, int nc, int tamañoPagina, int paginaInicial, PrintWriter writer) {
        int elementosPorPagina = tamañoPagina / 4; // Cada entero ocupa 4 bytes
        int paginaActual = paginaInicial;
        int referencia = 0;

        for (int i = 0; i < nf; i++) {
            for (int j = 0; j < nc; j++) {
                int pagina = paginaActual + (i * nc + j) / elementosPorPagina;
                int offset = (i * nc + j) % elementosPorPagina;
                String action = "R"; // toca cambiar esto porque pues no siempre se lee
                writer.println("Matriz[" + i + "][" + j + "]," + pagina + "," + offset + "," + action);
            }
        }
    }
}
