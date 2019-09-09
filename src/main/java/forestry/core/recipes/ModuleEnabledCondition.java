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

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import forestry.api.core.ForestryAPI;
import forestry.core.config.Constants;

public class ModuleEnabledCondition implements ICondition {

	private final String container;
	private final String module;

	public ModuleEnabledCondition(String container, String module) {
		this.container = container;
		this.module = module;
	}

	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(Constants.MOD_ID, "module");
	}

	@Override
	public boolean test() {
		return ForestryAPI.enabledModules.contains(new ResourceLocation(container, module));
	}

	@Override
	public String toString() {
		return "module_enabled(container=\"" + container + "\",module=\"" + module + "\")";
	}

	public static class Serializer implements IConditionSerializer<ModuleEnabledCondition> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, ModuleEnabledCondition value) {
			json.addProperty("module", value.module);
			json.addProperty("container", value.container);
		}

		@Override
		public ModuleEnabledCondition read(JsonObject json) {
			String module = json.get("module").getAsString();
			JsonElement conElement = json.get("container");
			String container = conElement == null ? "forestry" : conElement.getAsString();

			return new ModuleEnabledCondition(container, module);
		}

		@Override
		public ResourceLocation getID() {
			return new ResourceLocation(Constants.MOD_ID, "module");
		}
	}
}
