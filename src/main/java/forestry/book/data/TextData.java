package forestry.book.data;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextData {
	public String text = "";

	public String color = "black";
	public boolean bold = false;
	public boolean italic = false;
	public boolean underlined = false;
	public boolean strikethrough = false;
	public boolean obfuscated = false;
	public boolean paragraph = false;
	public boolean dropshadow = false;

	public TextData() {
	}

	public TextData(String text) {
		this.text = text;
	}
}
