package utils.math;

import java.util.Collections;
import java.util.List;

public class ListPrinter {
	public static void plot(List<Integer> list){
		if(list.isEmpty()) {
			System.out.println("empty List.");
			return;
		}
		Collections.sort( list );
		
		StringBuilder sb = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		int val = list.get(0)-1;
		for(int i=0; i<list.size(); i++){
			if(val != list.get(i)){
				val = list.get(i);
				sb.append(newLine+"val: "+val+" - .");
			}
			else sb.append(".");
		}
		System.out.println(sb.toString());
	}
}
