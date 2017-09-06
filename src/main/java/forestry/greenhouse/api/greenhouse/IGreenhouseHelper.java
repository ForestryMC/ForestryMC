/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.greenhouse;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import forestry.api.multiblock.IGreenhouseController;

public interface IGreenhouseHelper {

	void registerWindowGlass(String name, ItemStack item, String texture);

	ItemStack getGlassItem(String name);

	String getGlassTexture(String name);

	Collection<String> getWindowGlasses();

	void registerLogic(IGreenhouseLogicFactory logicFactory);

	Collection<IGreenhouseLogic> createLogics(IGreenhouseController controller);

}
