package forestry.core.translation;

import java.util.IllegalFormatException;

import forestry.core.utils.Log;

public class TranslatorServer implements TranslatorProxy {

	@Override
	public String translateToLocal(String key) {
		return key;
	}

	@Override
	public boolean canTranslateToLocal(String key) {
		return false;
	}

	@Override
	public String translateToLocalFormatted(String key, Object... format) {
		try {
			return String.format(key, format);
		} catch (IllegalFormatException e) {
			String errorMessage = "Format error: " + key;
			Log.error(errorMessage, e);
			return errorMessage;
		}
	}
}
