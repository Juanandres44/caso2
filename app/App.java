import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

public class App {
	static File file;
	static FileWriter escribir;
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



	public static int  PositionResult(int pathtype ,int pagesize , int rowquantity , int columnquantity , int intsize, FileWriter newFile,String currentmat,int checker2) {
		int refnumber=3*(rowquantity*columnquantity);
		int pagenumber=rowquantity*columnquantity*3*intsize / pagesize;
		int conteo =0;
		int PA=checker2;
		if (PA==0) {
		try {
			newFile.write(Integer.toString(pagesize)+"\r\n");
			newFile.write(Integer.toString(intsize)+"\r\n");
			newFile.write(Integer.toString(rowquantity)+"\r\n");
			newFile.write(Integer.toString(columnquantity)+"\r\n");
			newFile.write(Integer.toString(pathtype)+"\r\n");
			newFile.write(Integer.toString(pagenumber)+"\r\n");
			newFile.write(Integer.toString(refnumber)+"\r\n");

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		}
	
		if(pathtype==1) {
	
			for(int i=0;i< rowquantity;i++) {
	
				for(int j=0;j<columnquantity;j++) {
					
	
					String currentAnotation= new String(currentmat+":"+Integer.toString(i)+":"+Integer.toString(j)+","+PA+","+conteo+"\r\n");
	
					if(conteo<pagesize-intsize) {
					 conteo+=intsize;
					 
					}
					else {
						conteo=0;
						PA+=1;		
					}
					
					try {
						newFile.write(currentAnotation);
						System.out.println(currentAnotation);
						System.out.println("Vamos por la posicion"+i+"y la columna"+j);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				
			}
	
		}
		if(pathtype==2) {
	
			for(int j=0;j< rowquantity;j++) {
	
				for(int i=0;i<columnquantity;i++) {
					
	
					String currentAnotation= new String(currentmat+","+Integer.toString(i)+","+Integer.toString(j)+","+PA+","+conteo);
	
					if(conteo<pagesize-intsize) {
					 conteo+=intsize;
					 
					}
					else {
						conteo=0;
						PA+=1;		
					}
					
					try {
						newFile.write(currentAnotation);
						System.out.println(currentAnotation);
						System.out.println("Vamos por la posicion"+i+"y la columna"+j);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				
			}
	
		}
		return PA;
	}

	public static void cargarDatos() {
		Scanner scanner = new Scanner (System.in);
		System.out.print("Numero de marcos de pagina que se van a utilizar para la simulacion: ");  
		numMarcosDePaginaRAM = scanner.nextInt(); 
		Scanner sc = new Scanner(System.in);
		while (!carga) {
			try {
				if (pathToProperties == "") {
					System.out.print(
							"Cambie el path del archivo de referencias o simplemente presione enter para utilizar el archivo default 'referencia/referencia1.txt' \n");
					pathToProperties = sc.nextLine();
					if (pathToProperties == "") {
						pathToProperties = "referencia/referencia1.txt";
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
			try {
				file = new File("referencia/referencia1.txt");
				boolean value = file.createNewFile();
				if (value) {
				  System.out.println("Archivo creado");
				}
				else {
				  System.out.println("Ya existe este archivo");
				}
			  }
			  catch(Exception e) {
				e.getStackTrace();
			  }
			  FileWriter newFile=null;
			  int actua =0;
			  int actua2=0;
			  int actua3=0;
			  try {
				  newFile = new FileWriter("referencia/referencia1.txt");
				  for(int y=0;y<3;y++) {
				  if(y==0) {
					  actua=PositionResult(1,8,4,4,2,newFile,"A",0);
					  System.out.println("pag actual matriza final"+actua);
				  }
				  if(y==1) {
					  actua2=PositionResult(1,8,4,4,2,newFile,"B",actua);
					  System.out.println("pag actual matrizb final"+actua2);
	  
				  }
				  if(y==2) {
					  actua3=PositionResult(1,8,4,4,2,newFile,"C",actua2);
					  System.out.println("pag actual matrizc final"+actua3);
				  }			
				  }
	  
			  } catch (IOException e) {
				  e.printStackTrace();
			  }
	  
			  try {
				  newFile.close();
			  } catch (IOException e) {
				  e.printStackTrace();
			  }		
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
			
			new ActualizadorTablaThread(secuenciaReferencias, tabla, numReferencias, barrera).start();			
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
