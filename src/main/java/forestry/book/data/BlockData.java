package forestry.book.data;

import com.google.gson.JsonObject;

import java.util.Map;

public class BlockData {
	public int[] pos;
	public int[] endPos;
	public String block;
	public byte meta;
	public JsonObject nbt;
	public Map<String, String> state;
}
