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
package forestry.core.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ForestryGuiConfig extends GuiConfig {

	public ForestryGuiConfig(GuiScreen parent) {
		super(parent, getConfigCategories(), Constants.MOD_ID, true, true, Constants.MOD_ID);
	}

	private static List<IConfigElement> getConfigCategories() {
		List<IConfigElement> configElements = new ArrayList<>();

		LocalizedConfiguration configCommon = Config.configCommon;
		if (configCommon != null) {
			List<String> commonCategoryNames = Arrays.asList("crafting", "difficulty", "genetics", "performance", "structures", "tweaks", "world");
			for (String categoryName : commonCategoryNames) {
				ConfigCategory category = configCommon.getCategory(categoryName);
				configElements.add(new ConfigElement(category));
			}
		}

		LocalizedConfiguration configFluid = Config.configFluid;
		if (configFluid != null) {
			List<String> fluidCategoryNames = Arrays.asList("enableFluid", "enableFluidBlock");
			for (String categoryName : fluidCategoryNames) {
				ConfigCategory category = configFluid.getCategory(categoryName);
				configElements.add(new ConfigElement(category));
			}
		}

		return configElements;
	}

}
