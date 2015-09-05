package forestry.core.data;

import java.util.HashMap;

public class AutoJsonItem {

	public String parent;
	public HashMap<String, Object> display;
	
	public AutoJsonItem(String parent, HashMap<String, Object> display) {
		this.parent = parent;
		this.display = display;
	}
	
}