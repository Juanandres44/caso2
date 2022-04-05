

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

public class App {
	static String pathToProperties = "";
	static boolean carga = false;
	static int tamPag;
	static int tamEnt;
	static int fil;
	static int col;
	static int rec;
	static int numMarcosDePaginaRAM;
	static int numPaginasProceso;
	static int numReferencias;
	static int numPaginasCargadas;
	static int numFallos;
	static ConcurrentHashMap<Integer, Pagina> tabla;
	static ArrayList<Integer> RAM = new ArrayList<>();
	static ArrayList<String> secuenciaReferencias = new ArrayList<>();

	public static void cargarDatos() {
		Scanner scanner = new Scanner (System.in);
		System.out.print("Numero de marcos de pagina para la simulacion: ");  
		numMarcosDePaginaRAM = scanner.nextInt(); 
		Scanner sc = new Scanner(System.in);
		while (!carga) {
			// Carga de datos del archivo Properties
			try {
				if (pathToProperties == "") {
					System.out.print(
							"Change input file path or just press enter for using the default path 'referencia/referencias8_128_75.txt' \n");
					pathToProperties = sc.nextLine();
					if (pathToProperties == "") {
						pathToProperties = "referencia/referencias8_128_75.txt";
					}
				}
				File f = new File(pathToProperties);
				Scanner lector = new Scanner(f);
				tamPag = Integer.parseInt(lector.nextLine());
				tamEnt = Integer.parseInt(lector.nextLine());
				fil = Integer.parseInt(lector.nextLine());
				col = Integer.parseInt(lector.nextLine());
				rec = Integer.parseInt(lector.nextLine());
				numPaginasProceso = Integer.parseInt(lector.nextLine());
				numReferencias = Integer.parseInt(lector.nextLine());
				while (lector.hasNextLine()) {
					secuenciaReferencias.add(lector.nextLine());
				}
				System.out.println("Configuración cargada");
				carga = true;
				lector.close();
				sc.close();
				scanner.close();
				numPaginasCargadas = 0;
			} catch (FileNotFoundException ex) {
				System.out.println("No se encontró el archivo.");
				System.out.println("Por favor ingresa la ruta del archivo de propiedades:");
				pathToProperties = sc.nextLine();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Scanner info = new Scanner (System.in);
		System.out.print("¿Que opcion va a realizar? p.e opcion1 o opcion2 ");  
		String opcion = info.next();
		if (opcion.equals("opcion1")){
			//opcion1
		}
		else if(opcion.equals("opcion2")){
			cargarDatos();
			tabla = new ConcurrentHashMap<>(numPaginasProceso);
			for (int i = 0; i < numPaginasProceso; i++) 
			{
			Pagina pag = new Pagina(i);
			tabla.put(i, pag);
			}
			
			CyclicBarrier barrera = new CyclicBarrier(2);
			
			new ActualizadorTabla(secuenciaReferencias, tabla, numReferencias, barrera).start();
			
			try {
				barrera.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
			
			System.out.println("El número de fallas de página generadas es: " + numFallos);
		}
		else{
			System.out.println("Favor ingresar una opcion valida");
		}
		info.close();
	}

	public static void cargarPagina() {
		numPaginasCargadas += 1;
	}

	public static void falloGenerado() {
		numFallos += 1;
	}

	public static int darNumPagCargadas() {
		return numPaginasCargadas;
	}

	public static int darNumPagRAM() {
		return numMarcosDePaginaRAM;
	}
}
