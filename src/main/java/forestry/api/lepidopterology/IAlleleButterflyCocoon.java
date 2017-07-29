/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

import java.util.Map;

import forestry.api.genetics.IAlleleProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public interface IAlleleButterflyCocoon extends IAlleleProperty<IAlleleButterflyCocoon> {

	ModelResourceLocation getCocoonItemModel(int age);

	String getCocoonName();

	Map<ItemStack, Float> getCocoonLoot();
	
	void clearLoot();

	void addLoot(ItemStack loot, float chance);
}
