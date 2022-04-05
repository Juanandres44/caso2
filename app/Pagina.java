
public class Pagina {
	private int numPagina;
	private boolean loaded;
	private boolean M;
	private boolean R;
	private boolean referenced;

	public Pagina(int numPagina) {
		this.numPagina = numPagina;
		this.referenced = false;
		this.M = false;
		this.R = false;
		this.loaded = false;
	}

	public int getNumPagina() {
		return this.numPagina;
	}
	public boolean getM() {
		return this.M;
	}

	public boolean getR() {
		return this.R;
	}

	public boolean isLoaded() {
		return this.loaded;
	}

	public boolean isReferenced(){
		return this.referenced;
	}

	public synchronized void reference() {
		this.R = true;
	}


	public synchronized void load() {
		this.loaded = true;
	}

	public synchronized void resetR() {
		this.R = false;
	}

	public synchronized void resetM() {
		this.M = false;
	}

	public synchronized void unload() {
		this.loaded = false;
		resetR();
	}

	public synchronized void reset() {
		this.R = false;
		this.M = false;
	}
}
