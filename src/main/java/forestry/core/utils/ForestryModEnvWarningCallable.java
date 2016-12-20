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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;
import forestry.core.config.Constants;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.IForestryPlugin;
import forestry.plugins.PluginManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;

/**
 * ICrashCallable for listing disabled modules for crash reports.
 **/
public class ForestryModEnvWarningCallable implements ICrashCallable {
	private final String disabledModulesMessage;

	public static void register() {
		Set<IForestryPlugin> configDisabledPlugins = PluginManager.configDisabledPlugins;
		if (!configDisabledPlugins.isEmpty()) {
			List<String> disabledPluginNames = new ArrayList<>();
			for (IForestryPlugin plugin : configDisabledPlugins) {
				ForestryPlugin info = plugin.getClass().getAnnotation(ForestryPlugin.class);
				disabledPluginNames.add(info.name());
			}

			String disabledModulesMessage = "Plugins have been disabled in the config: " + Joiner.on(", ").join(disabledPluginNames);
			ForestryModEnvWarningCallable callable = new ForestryModEnvWarningCallable(disabledModulesMessage);
			FMLCommonHandler.instance().registerCrashCallable(callable);
		}
	}

	private ForestryModEnvWarningCallable(String disabledModulesMessage) {
		this.disabledModulesMessage = disabledModulesMessage;
	}

	@Override
	public String call() {
		return disabledModulesMessage;
	}

	@Override
	public String getLabel() {
		return Constants.MOD_ID + " ";
	}

}
