package forestry.core.utils;

import net.minecraft.client.resources.I18n;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//TODO - sides issues
@Deprecated
@OnlyIn(Dist.CLIENT)
public class Translator {
    private Translator() {

    }

    public static String translateToLocal(String key) {
        return translateToLocalFormatted(key);
    }

    public static boolean canTranslateToLocal(String key) {
        return I18n.hasKey(key);
    }

    public static String translateToLocalFormatted(String key, Object... format) {
        return I18n.format(key, format);
    }
}
