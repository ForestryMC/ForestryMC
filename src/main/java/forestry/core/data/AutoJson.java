package forestry.core.data;

import java.util.HashMap;

public class AutoJson {
	
	public AutoJson(String parent, HashMap textures) {
		this.parent = parent;
		this.textures = textures;
	}
	
	public String parent;
	public HashMap<String, String> textures;
	
}
