package forestry.core.translation;

import java.util.IllegalFormatException;

import net.minecraft.client.resources.I18n;

import forestry.core.utils.Log;

public class TranslatorClient implements TranslatorProxy {

	@Override
	public String translateToLocal(String key) {
		return I18n.format(key);
	}

	@Override
	public boolean canTranslateToLocal(String key) {
		return I18n.hasKey(key);
	}

	@Override
	public String translateToLocalFormatted(String key, Object... format) {
		try {
			return I18n.format(key, format);
		} catch (IllegalFormatException e) {
			String errorMessage = "Format error: " + I18n.format(key);
			Log.error(errorMessage, e);
			return errorMessage;
		}
	}
}
