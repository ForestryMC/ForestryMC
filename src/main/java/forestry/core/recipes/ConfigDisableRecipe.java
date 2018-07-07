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
package forestry.core.recipes;

import com.google.gson.JsonObject;

import java.util.function.BooleanSupplier;

import net.minecraft.util.JsonUtils;

import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import forestry.api.core.ForestryAPI;
import forestry.core.config.Config;

@SuppressWarnings("unused")
public class ConfigDisableRecipe implements IConditionFactory {
	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		String category = JsonUtils.getString(json, "category");
		boolean def;
		if (JsonUtils.hasField(json, "default")) {
			def = JsonUtils.getBoolean(json, "default");
		} else {
			def = true;
		}
		String[] split = category.split(":");
		if (split.length == 2 && Config.configCommon.hasKey(split[0], split[1])) {
			return () -> Config.configCommon.getBooleanLocalized(split[0], split[1], def);
		} else {
			return () -> ForestryAPI.activeMode.getBooleanSetting(split[0]);
		}

	}
}