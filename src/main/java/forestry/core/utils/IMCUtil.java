package forestry.core.utils;

import net.minecraftforge.fml.common.event.FMLInterModComms;

public class IMCUtil {
	public static String getInvalidIMCMessageText(FMLInterModComms.IMCMessage message) {
		final Object messageValue;
		if (message.isItemStackMessage()) {
			messageValue = message.getItemStackValue().toString();
		} else if (message.isNBTMessage()) {
			messageValue = message.getNBTValue();
		} else if (message.isStringMessage()) {
			messageValue = message.getStringValue();
		} else {
			messageValue = "";
		}

		return String.format("Received an invalid '%s' request '%s' from mod '%s'. Please contact the author and report this issue.", message.key, messageValue, message.getSender());
	}

	public static void logInvalidIMCMessage(FMLInterModComms.IMCMessage message) {
		String invalidIMCMessageText = getInvalidIMCMessageText(message);
		Log.warning(invalidIMCMessageText);
	}
}
