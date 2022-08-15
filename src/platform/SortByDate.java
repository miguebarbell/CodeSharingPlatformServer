package platform;

import java.util.Comparator;

public class SortByDate implements Comparator<Code> {
	public int compare(Code o1, Code o2) {
//		return o1.getDate().compareTo(o2.getDate());
		return o2.getDate().compareTo(o1.getDate());
	}
}
