
public class Pagina {
	private int numPagina;
	private boolean loaded;
	private boolean referenced;

	public Pagina(int numPagina) {
		this.numPagina = numPagina;
		this.referenced = false;
		this.loaded = false;
	}

	public int getNumPagina() {
		return this.numPagina;
	}


	public boolean isLoaded() {
		return this.loaded;
	}

	public boolean isReferenced(){
		return this.referenced;
	}

	public synchronized void reference() {
		this.referenced = true;
	}


	public synchronized void load() {
		this.loaded = true;
	}


	public synchronized void unload() {
		this.loaded = false;
	}
}
