
public class Pagina {
	private int numPagina;
	private boolean loaded;
	private boolean dirt;
	private boolean referenced;

	public Pagina(int numPagina) {
		this.numPagina = numPagina;
		this.referenced = true;
		this.loaded = false;
	}

	public int getNumPagina() {
		return this.numPagina;
	}

	/*
	public void setDirty(){
		dirty = true;	
	}
	
	public boolean isDirty(){
		return dirty;
	}
	*/

	public boolean isLoaded() {
		return this.loaded;
	}

	public synchronized boolean reference() {
		return referenced;
	}


	public synchronized void load() {
		this.loaded = true;
	}

	public synchronized void unload() {
		this.loaded = false;
	}
}
