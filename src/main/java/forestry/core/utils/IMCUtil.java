package forestry.core.utils;

import net.minecraftforge.fml.InterModComms;

public class IMCUtil {
    public static String getInvalidIMCMessageText(InterModComms.IMCMessage message) {
        //TODO new imc
        //		final Object messageValue;
        //		if (message.isItemStackMessage()) {
        //			messageValue = message.getItemStackValue().toString();
        //		} else if (message.isNBTMessage()) {
        //			messageValue = message.getNBTValue();
        //		} else if (message.isStringMessage()) {
        //			messageValue = message.getStringValue();
        //		} else {
        //			messageValue = "";
        //		}
        //
        //		return String.format("Received an invalid '%s' request '%s' from mod '%s'. Please contact the author and report this issue.", message.key, messageValue, message.getSender());
        return "INVALID MESSAGE";
    }

    public static void logInvalidIMCMessage(InterModComms.IMCMessage message) {
        String invalidIMCMessageText = getInvalidIMCMessageText(message);
        Log.warning(invalidIMCMessageText);
    }
}
