import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AlgThread extends Thread {
	
	private static ConcurrentHashMap tabla = new ConcurrentHashMap<>();
	private static int pageNum;
	private static int oldestAgeValue;
	private static int oldestPage;
	private static int msbForAge;
	
	public AlgThread(ConcurrentHashMap tabla, int pageNum) {
		AlgThread.tabla = tabla;
		AlgThread.pageNum = pageNum;
	}

	public static void updateAges() {
		
		for(Object kv: tabla.entrySet())
		{
			Map.Entry kvPair = (Map.Entry)kv;
			int ageVal = (int) kvPair.getValue();
			
	        ageVal = ageVal >> 1;
			tabla.replace(kvPair.getKey(), ageVal);
			
			if(ageVal<oldestAgeValue){
				oldestPage = (int) kvPair.getKey();
			}
			oldestAgeValue = ageVal;	
		}
	}
	
	public static void computePageFaultCount(int marcosPagina) {
		int currFrameCount = 0;
		int i=0;
		int numReferencias = App.numReferencias;
		tabla = new ConcurrentHashMap<>();
		
		while(currFrameCount<marcosPagina && i<numReferencias) {
			
			int entry = pageNum;
			
			updateAges(); 
			
			if(tabla.containsKey(entry)){
				int ageValue = (int) tabla.get(entry);
				ageValue = ageValue | msbForAge;
				tabla.replace(entry, ageValue);
			} 
			else 
			{ 
				App.falloGenerado();
				
				if(tabla.size()<marcosPagina)
				{
					tabla.put(entry, msbForAge);
				}
				else if(tabla.size()==marcosPagina)
				{ 
					tabla.remove(oldestPage);
					tabla.put(entry,msbForAge); 
				}
			}
			
			currFrameCount++;
			if(currFrameCount>=marcosPagina){
				currFrameCount = 0;
			}
			
			i++;
		}
	}


	public void run() {
		msbForAge = 16777216;
		oldestAgeValue = msbForAge;
		int marcosPagina = App.numMarcosDePaginaRAM;
		computePageFaultCount(marcosPagina);
	}
}
