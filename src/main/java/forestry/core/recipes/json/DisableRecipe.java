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
package forestry.core.recipes.json;

import com.google.gson.JsonObject;

import java.util.function.BooleanSupplier;

import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import forestry.modules.ModuleHelper;

public class DisableRecipe implements IConditionFactory {

	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {    //TODO - cache enabled modules?
		return () -> ModuleHelper.isEnabled(json.get("module").getAsString());
	}
}
