/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.message.MessageFormatMessage;

import forestry.core.config.Constants;

public abstract class Log {

	/* FINEST */
	public static void finest(String message) {
		log(Level.TRACE, message);
	}

	public static void finest(String message, Object param) {
		log(Level.TRACE, message, param);
	}

	public static void finest(String message, Object... params) {
		log(Level.TRACE, message, params);
	}

	/* FINER */
	public static void finer(String message) {
		log(Level.TRACE, message);
	}

	public static void finer(String message, Object param) {
		log(Level.TRACE, message, param);
	}

	public static void finer(String message, Object... params) {
		log(Level.TRACE, message, params);
	}

	/* FINE */
	public static void fine(String message) {
		log(Level.DEBUG, message);
	}

	public static void fine(String message, Object param) {
		log(Level.DEBUG, message, param);
	}

	public static void fine(String message, Object... params) {
		log(Level.DEBUG, message, params);
	}

	/* INFO */
	public static void info(String message) {
		log(Level.INFO, message);
	}

	public static void info(String message, Object param) {
		log(Level.INFO, message, param);
	}

	public static void info(String message, Object... params) {
		log(Level.INFO, message, params);
	}

	/* WARNING */
	public static void warning(String message) {
		log(Level.WARN, message);
	}

	public static void warning(String message, Object param) {
		log(Level.WARN, message, param);
	}

	public static void warning(String message, Object... params) {
		log(Level.WARN, message, params);
	}

	/* SEVERE */
	public static void severe(String message) {
		log(Level.FATAL, message);
	}

	public static void severe(String message, Object param) {
		log(Level.FATAL, message, param);
	}

	public static void severe(String message, Object... params) {
		log(Level.FATAL, message, params);
	}

	/* GENERIC */
	private static void log(Level logLevel, String message) {
		LogManager.getLogger(Constants.MOD).log(logLevel, message);
	}

	private static void log(Level logLevel, String message, Object... params) {
		LogManager.getLogger(Constants.MOD).log(logLevel, new MessageFormatMessage(String.format(message, params), params));
	}

	/* EXCEPTIONS */
	public static void logThrowable(String msg, Throwable error, Object... args) {
		logThrowable(Level.ERROR, msg, 3, error, args);
	}

	public static void logThrowable(String msg, int lines, Throwable error, Object... args) {
		logThrowable(Level.ERROR, msg, lines, error, args);
	}

	public static void logThrowable(Level level, String msg, int lines, Throwable error, Object... args) {
		StackTraceElement[] oldtrace = error.getStackTrace();
		if (lines < oldtrace.length) {
			StackTraceElement[] newtrace = new StackTraceElement[lines];
			System.arraycopy(oldtrace, 0, newtrace, 0, newtrace.length);
			error.setStackTrace(newtrace);
		}
		LogManager.getLogger(Constants.MOD).log(level, new MessageFormatMessage(msg, args), error);
	}

	public static void logErrorAPI(String mod, Throwable error, Class classFile) {
		StringBuilder msg = new StringBuilder(mod);
		msg.append(" API error, please update your mods. Error: ").append(error);
		logThrowable(Level.ERROR, msg.toString(), 2, error);

		if (classFile != null) {
			msg = new StringBuilder(mod);
			msg.append(" API error: ").append(classFile.getSimpleName()).append(" is loaded from ").append(classFile.getProtectionDomain().getCodeSource().getLocation());
			log(Level.ERROR, msg.toString());
		}
	}

}
