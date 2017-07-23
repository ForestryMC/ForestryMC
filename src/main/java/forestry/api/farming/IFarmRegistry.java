/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraft.item.ItemStack;

public interface IFarmRegistry {

	/**
	 * Can be used to add IFarmables to some of the vanilla farm logics.
	 * <p>
	 * Identifiers: farmArboreal farmWheat farmGourd farmInfernal farmPoales farmSucculentes farmVegetables farmShroom
	 */
	void registerFarmables(String identifier, IFarmable... farmable);
	
	Collection<IFarmable> getFarmables(String identifier);
	
	/**
	 * Can be used to create a simple version of a farm logic, like the vanilla vegetable or wheat farm logic.
	 *
	 * @return Null if the farming plugin is not active.
	 */
	@Nullable
	IFarmLogic createLogic(ISimpleFarmLogic simpleFarmLogic);
	
	/**
	 * 
	 * @param itemStack
	 * @param value The value of the fertitlizer.The value of the forestry fertelizer is 500.
	 */
	void registerFertilizer(ItemStack itemStack, int value);
	
	/**
	 * @return The value of the fertitlizer
	 */
	int getFertilizeValue(ItemStack itemStack);
	
}
