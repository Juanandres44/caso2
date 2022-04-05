import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgingThreadF extends Thread {
	
	private static ConcurrentHashMap tabla = new ConcurrentHashMap<>();
	private int numItems = 0;
	private static int pageNum;
	private static int oldestAgeValue;
	private static int oldestPage;
	private static int msbForAge;
	
	public AgingThreadF(ConcurrentHashMap tabla, int pageNum) {
		AgingThreadF.tabla = tabla;
		AgingThreadF.pageNum = pageNum;
	}

	//Update ages of all page entries in the pade table
	public synchronized static void updateAges() {
		
		for(Object kv: tabla.entrySet())
		{
			Map.Entry kvPair = (Map.Entry)kv;
			int ageVal = (int) kvPair.getValue();
			
	        ageVal = ageVal >> 1;
			tabla.replace(kvPair.getKey(), ageVal);
			
			if(ageVal<oldestAgeValue){ //keep track of oldest page entry in the page table
				oldestPage = (int) kvPair.getKey();
			}
			oldestAgeValue = ageVal;	
		}
	}
	
	//This funciton counts nuber of page faults for the given frame size
	public synchronized static int computePageFaultCount(int frameSize) {
		int currFrameCount = 0;
		int i=0;
		int faultCount = 0;
		int numReferencias = App.numReferencias;
		tabla = new ConcurrentHashMap<>();
		
		while(currFrameCount<frameSize && i<numReferencias) {
			
			//Get the current page reference
			int entry = pageNum;
			
			//since a page is referenced, update the ages of all the pages in the page table
			updateAges(); 
			
			if(tabla.containsKey(entry)){		//If page is available in the page table
				int ageValue = (int) tabla.get(entry);
				ageValue = ageValue | msbForAge;	//set MSB value for the age of page i.
				tabla.replace(entry, ageValue);	//Set the updated value in page table
			} 
			else //If the page entry is not present in the age table. 
			{ 
				faultCount = faultCount + 1; //fault has occurred
				
				if(tabla.size()<frameSize) // if there is room in the page table
				{
					tabla.put(entry, msbForAge); //add the page entry with msb set for age.
				}
				else if(tabla.size()==frameSize) //if page table is full.
				{ 
					tabla.remove(oldestPage);	// Remove the oldest page entry from the table
					tabla.put(entry,msbForAge); //put the current entry into the table and set its age MSB
				}
			}
			
			currFrameCount++;
			if(currFrameCount>=frameSize){	//Repeat for every frame size
				currFrameCount = 0;
			}
			
			i++;
		}
		return faultCount;
	}


	public void run() {
		msbForAge = 16777216; //This is to set the MSB 24bit age reference bit- 2^24
		oldestAgeValue = msbForAge; //This will track the oldest age value
		int frameSize = App.numMarcosDePaginaRAM;
		computePageFaultCount(frameSize);
	}
}
