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
package forestry.plugins;

import forestry.core.proxy.Proxies;

public class PluginModule {

	private final String pluginName;
	private final ForestryPlugin instance;

	public PluginModule(Class<? extends ForestryPlugin> pluginClass) {
		Plugin info = pluginClass.getAnnotation(Plugin.class);
		if (info == null) {
			String error = "Forestry Plugin missing @Plugin annotation: " + pluginClass.getName();
			Proxies.log.severe(error);
			throw new RuntimeException(error);
		}
		this.pluginName = info.name();
		ForestryPlugin plugin;

		try {
			plugin = pluginClass.newInstance();
			if (!plugin.isAvailable())
				plugin = null;
		} catch (Throwable e) {
			plugin = null;
		}

		this.instance = plugin;
	}

	public ForestryPlugin instance() {
		return instance;
	}

	@Override
	public String toString() {
		return this.pluginName;
	}
}
