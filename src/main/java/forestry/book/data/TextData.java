package forestry.book.data;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextData {
    public String text = "";

    public final String color = "black";
    public final boolean bold = false;
    public final boolean italic = false;
    public final boolean underlined = false;
    public final boolean strikethrough = false;
    public final boolean obfuscated = false;
    public final boolean paragraph = false;
    public final boolean shadow = false;

    public TextData() {
    }

    public TextData(String text) {
        this.text = text;
    }
}
