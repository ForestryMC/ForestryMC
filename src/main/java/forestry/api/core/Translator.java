package forestry.api.core;

import java.util.IllegalFormatException;

import net.minecraft.client.resources.I18n;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import forestry.core.utils.Log;

public class Translator {

	private static Side side = FMLCommonHandler.instance().getSide();

	private Translator() {

	}

	public static String translateToLocal(String key) {
		return side == Side.CLIENT ? I18n.format(key) : key;
	}

	public static boolean canTranslateToLocal(String key) {
		return side == Side.CLIENT && I18n.hasKey(key);
	}

	public static String translateToLocalFormatted(String key, Object... format) {
		try {
			return side == Side.CLIENT ? I18n.format(key, format) : String.format(key, format);
		} catch (IllegalFormatException e) {
			String errorMessage = "Format error: " + I18n.format(key);
			Log.error(errorMessage, e);
			return errorMessage;
		}
	}
}
