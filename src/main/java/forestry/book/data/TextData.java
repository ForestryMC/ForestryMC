package forestry.book.data;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextData {
    public String text = "";

    public String color = "BLACK";
    public boolean bold = false;
    public boolean italic = false;
    public boolean underlined = false;
    public boolean strikethrough = false;
    public boolean obfuscated = false;
    public boolean paragraph = false;
    public boolean shadow = false;

    public TextData(String text) {
        this.text = text;
    }
}
