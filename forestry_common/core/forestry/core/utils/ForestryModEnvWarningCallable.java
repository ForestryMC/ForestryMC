/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.utils;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;

import forestry.core.config.Defaults;

public class ForestryModEnvWarningCallable implements ICrashCallable {

	private final List<String> modIDs;

	public ForestryModEnvWarningCallable() {
		this.modIDs = new ArrayList<String>();

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT && FMLClientHandler.instance().hasOptifine()) {
			modIDs.add("Optifine");
		}

		if (Loader.isModLoaded("gregtech_addon")) {
			modIDs.add("GregTech");
		}

		try {
			@SuppressWarnings("unused")
			Class<?> c = Class.forName("org.bukkit.Bukkit");
			modIDs.add("Bukkit, MCPC+, or other Bukkit replacement");
		}
		catch (Throwable t) {} // No need to do anything.

		// Add other bad mods here.

		if (modIDs.size() > 0) {
			FMLCommonHandler.instance().registerCrashCallable(this);
		}
	}

	@Override
	public String call() throws Exception {
		String message = "[" + Defaults.MOD + "] Warning: You have mods that change the behavior of Minecraft, ForgeModLoader, and/or Minecraft Forge to your client: \r\n";
		message = message + modIDs.get(0);
		for (int i = 1; i < modIDs.size(); ++i) {
			message = message + ", " + modIDs.get(i);
		}
		message = message + "\r\nThese may have caused this error, and may not be supported. Try reproducing the crash WITHOUT these mods, and report it then.";
		return message;
	}

	@Override
	public String getLabel() {
		return Defaults.MOD + " ";
	}

}
