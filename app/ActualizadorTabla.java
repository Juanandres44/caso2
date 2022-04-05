
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

public class ActualizadorTabla extends Thread {
	private ArrayList<String> referencias;
	private ConcurrentHashMap<Integer, Pagina> tabla;
	private int cantidadReferencias;
	private CyclicBarrier barrera;

	public ActualizadorTabla(ArrayList<String> referencias, ConcurrentHashMap<Integer, Pagina> tabla,
			int cantidadReferencias, CyclicBarrier barrera) {
		this.referencias = referencias;
		this.tabla = tabla;
		this.cantidadReferencias = cantidadReferencias;
		this.barrera = barrera;
	}

	@Override
	public void run() {
		for (int i = 0; i < cantidadReferencias; i++) {
			String[] referencia = referencias.get(i).split(",");
			String matriz = referencia[0];
			Integer numPagina = Integer.parseInt(referencia[1]);
			Integer desplazamiento = Integer.parseInt(referencia[2]);
			try {
				actualizarTabla(numPagina);
                Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			barrera.await();
		} catch (InterruptedException | BrokenBarrierException e) 
        {
			e.printStackTrace();
		}
	}

	private synchronized void actualizarTabla(int numPagina) throws InterruptedException {
		Pagina page = tabla.get(numPagina);
		if (page.isLoaded() == false && App.RAM.size() < App.numMarcosDePaginaRAM) {
			page.load();
			App.RAM.add(numPagina);
			App.falloGenerado();
			App.cargarPagina();
		} else if (page.isLoaded() == false) {
			App.falloGenerado();
			new AlgoritmoActualizacion(tabla, numPagina).start();
		}
		tabla.replace(numPagina, page);
	}
}
