/*
 *******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 *******************************************************************************
 */
package forestry.core.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.function.BooleanSupplier;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import forestry.api.core.ForestryAPI;

@SuppressWarnings("unused")
public class DisableRecipe implements IConditionFactory {

	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		String module = json.get("module").getAsString();
		JsonElement conElement = json.get("container");
		String container = conElement == null ? "forestry" : conElement.getAsString();

		return () -> ForestryAPI.enabledModules.contains(new ResourceLocation(container, module));
	}
}
