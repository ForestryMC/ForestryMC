package forestry.core.translation;

import java.util.IllegalFormatException;

import net.minecraftforge.fml.common.SidedProxy;

import forestry.core.config.Constants;
import forestry.core.utils.Log;

public class Translator {
	@SidedProxy(clientSide = "forestry.core.translation.TranslatorClient",
			serverSide = "forestry.core.translation.TranslatorServer",
			modId = Constants.MOD_ID)
	private static TranslatorProxy proxy;

	public static String translateToLocal(String key) {
		return proxy.translateToLocal(key);
	}

	public static boolean canTranslateToLocal(String key) {
		return proxy.canTranslateToLocal(key);
	}

	public static String translateToLocalFormatted(String key, Object... format) {
		try {
			return proxy.translateToLocalFormatted(key, format);
		} catch (IllegalFormatException e) {
			String errorMessage = "Format error: " + proxy.translateToLocal(key);
			Log.error(errorMessage, e);
			return errorMessage;
		}
	}
}
