import java.util.concurrent.ConcurrentHashMap;

public class AgingThread extends Thread {
	
	private Pagina [] ft;
	private ConcurrentHashMap<Integer, Pagina> tabla;
	private int numItems = 0;
	
	public AgingThread(ConcurrentHashMap<Integer, Pagina> tabla, int pageNum) {
		// counters to keep track 
		int faults = 0;
		int time = 0;
		
		
		//initialize PageTable object
		PageTable pt = new PageTable();
		//Create frame table to store Page objects
		ft = tabla.get(pageNum);
		
		String line = null;
		
		try{

				
				//increment time counter after every loop
				time++;
				
				if (time == refresh){
					shiftBits();
					time = 0;
				}
								
				//checks to see if the current page is already loaded into RAM
				if (ft.isLoaded()==true)
				{
					//if already loaded, grab the index that it's in the frame table in order to update
					int frameIndex = pt.getFrameIndex(pageNum);
					ft[frameIndex].setReferenced(true);
					ft[frameIndex].setDirty();
				}
				else{
					//else statement means that page was not found in RAM
					
					//creates page object based on whether it's a read or write operation
					Pagina p = new Pagina(pageNum, true);
					
					if (numItems != numframes){
						ft[numItems] = p;
						pt.setFrameNum(pageNum, numItems);	//set value in pagetable to equal its position in the queue
						numItems++;
						faults++;
						writer.println("page fault -- no eviction");
					}
					else{
						faults++;
						int evictPage = calculateValue();
						
						if (ft[evictPage].isDirty()){
							diskwrites++;
							writer.println("page fault -- evict dirty");
						}
						else{
							writer.println("page fault -- evict clean");
						}
						
						pt.setFrameNum(ft[evictPage].getPageNum(), -1);
						ft[evictPage] = p;
						pt.setFrameNum(pageNum, evictPage);
					}
				}
			
			System.out.println("Aging");
			System.out.println("Number of frames: " + numframes);
			System.out.println("Total memory accesses: " + memoryaccess);
			System.out.println("Total page faults: " + faults);
			System.out.println("Total writes to disk: " + diskwrites);
			
			writer.println();
			writer.println(filename);
			writer.println("Aging");
			writer.println("Number of frames: " + numframes);
			writer.println("Total memory accesses: " + memoryaccess);
			writer.println("Total page faults: " + faults);
			writer.println("Total writes to disk: " + diskwrites);
			writer.close();
		}
		catch(FileNotFoundException ex){
			System.out.println("Unable to open file '" + filename + "'");
		}
		catch(IOException ex){
			System.out.println("Error reading file");
		}
	}

	public void shiftBits(){
		for (int i = 0; i < numItems; i++){
			ft[i].shiftTime();
		}
	}
	
	public int calculateValue(){
		boolean [] temp;
		int minValue = 999999999;
		int currentValue = 0;
		int evictPage = 0;
		
		int exponent = 7;
		
		for (int i = 0; i < numItems; i++){
			currentValue = 0;
			temp = ft[i].getTime();
			exponent = 7;
			for (int j = 0; j < temp.length; j++){				
				if (temp[j]){
					currentValue += Math.pow(2, exponent);
				}
				exponent--;
			}
			
			if (ft[i].isReferenced()){
				currentValue += Math.pow(2, 8);
			}
			
			if (currentValue < minValue){
				minValue = currentValue;
				evictPage = i;
			}
		}
		
		return evictPage;
	}


	public void run() {
		while(true) {
			try {
				if ( m.getFinished() ) {
					break;
				}
				m.updateReferences();
				sleep(20);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
