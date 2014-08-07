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
package forestry.core.proxy;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import forestry.core.config.Defaults;

public class ProxyLog {

	/* LOGGING */
	private static Logger forestryLogger;

	private void initLogger() {
		forestryLogger = LogManager.getLogger(Defaults.MOD);
	}

	/* FINEST */
	public void finest(String message) {
		log(Level.TRACE, message);
	}

	public void finest(String message, Object param) {
		log(Level.TRACE, message, param);
	}

	public void finest(String message, Object... params) {
		log(Level.TRACE, message, params);
	}

	/* FINER */
	public void finer(String message) {
		log(Level.TRACE, message);
	}

	public void finer(String message, Object param) {
		log(Level.TRACE, message, param);
	}

	public void finer(String message, Object... params) {
		log(Level.TRACE, message, params);
	}

	/* FINE */
	public void fine(String message) {
		log(Level.DEBUG, message);
	}

	public void fine(String message, Object param) {
		log(Level.DEBUG, message, param);
	}

	public void fine(String message, Object... params) {
		log(Level.DEBUG, message, params);
	}

	/* INFO */
	public void info(String message) {
		log(Level.INFO, message);
	}

	public void info(String message, Object param) {
		log(Level.INFO, message, param);
	}

	public void info(String message, Object... params) {
		log(Level.INFO, message, params);
	}

	/* WARNING */
	public void warning(String message) {
		log(Level.WARN, message);
	}

	public void warning(String message, Object param) {
		log(Level.WARN, message, param);
	}

	public void warning(String message, Object... params) {
		log(Level.WARN, message, params);
	}

	/* SEVERE */
	public void severe(String message) {
		log(Level.FATAL, message);
	}

	public void severe(String message, Object param) {
		log(Level.FATAL, message, param);
	}

	public void severe(String message, Object... params) {
		log(Level.FATAL, message, params);
	}

	/* GENERIC */
	public void log(Level logLevel, String message) {
		if (forestryLogger == null)
			initLogger();

		forestryLogger.log(logLevel, message);
	}

	public void log(Level logLevel, String message, Object... params) {
		log(logLevel, String.format(message, params));
	}

	public void log(Level logLevel, String message, Object param) {
		log(logLevel, String.format(message, param));
	}

}
