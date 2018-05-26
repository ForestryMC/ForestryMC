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

import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import forestry.api.core.ForestryAPI;

@SuppressWarnings("unused")

public class DisableRecipe implements IConditionFactory {

	@Nullable
	private static Set<String> enabledModuleUIDs;

	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		if (enabledModuleUIDs == null) {
			getEnabledModules();
		}
		return () -> enabledModuleUIDs.contains(json.get("module").getAsString());
	}

	private static void getEnabledModules() {
		enabledModuleUIDs = new HashSet<>();
		Set<ResourceLocation> enabledModuleRLs = ForestryAPI.enabledModules;
		enabledModuleRLs.forEach(rl -> enabledModuleUIDs.add(rl.getResourcePath()));
	}
}
