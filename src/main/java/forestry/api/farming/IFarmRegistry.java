/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.BiFunction;

import net.minecraft.item.ItemStack;

public interface IFarmRegistry {

	/**
	 * Registers farming logic in registry under given identifier
	 * @param identifier Valid identifiers: farmArboreal farmCrops farmGourd farmInfernal farmPoales farmSucculentes farmShroom
	 * @param logic corresponding instance of logic
	 *
	 * @deprecated Since Forestry 5.8. Use {@link #registerLogic(String, BiFunction, String...)} or
	 *             {@link #registerLogic(String, IFarmProperties)}.
	 */
	@Deprecated
	void registerLogic(String identifier, IFarmLogic logic);

	/**
	 * Registers farming logic in registry
	 * @since Forestry 5.8
	 */
	IFarmProperties registerLogic(String identifier, IFarmProperties farmInstance);

	/**
	 * Registers farming logic in registry under given identifier
	 *
	 * @param identifier Valid identifiers: farmArboreal farmCrops farmGourd farmInfernal farmPoales farmSucculentes farmShroom
	 * @param logicFactory factory that creates the corresponding instance of logic
	 * @param farmablesIdentifiers Identifiers: farmArboreal farmCrops farmGourd farmInfernal farmPoales farmSucculentes farmShroom
	 * @since Forestry 5.8
	 */
	IFarmProperties registerLogic(String identifier, BiFunction<IFarmProperties, Boolean, IFarmLogic> logicFactory, String... farmablesIdentifiers);

	/**
	 * Can be used to add IFarmables to some of the vanilla farm logics.
	 * <p>
	 * Identifiers: farmArboreal farmCrops farmGourd farmInfernal farmPoales farmSucculentes farmShroom
	 */
	void registerFarmables(String identifier, IFarmable... farmable);
	
	Collection<IFarmable> getFarmables(String identifier);
	
	/**
	 * Can be used to create a simple version of a farm logic, like the vanilla vegetable or wheat farm logic.
	 *
	 * @return Null if the farming plugin is not active.
	 *
	 * @deprecated Since Forestry 5.8. Use {@link #createCropLogic(IFarmProperties, boolean, ISimpleFarmLogic)} instead.
	 */
	@Nullable
	@Deprecated
	default IFarmLogic createLogic(ISimpleFarmLogic simpleFarmLogic){
		return null;
	}
	
	/**
	 * @param itemStack
	 * @param value The value of the fertitlizer. The value of the forestry fertelizer is 500.
	 */
	void registerFertilizer(ItemStack itemStack, int value);
	
	/**
	 * @return The value of the fertitlizer
	 */
	int getFertilizeValue(ItemStack itemStack);

	/**
	 * Returns a fake {@link IFarmProperties} that returns the given logic at {@link IFarmProperties#getLogic(boolean)}.
	 *
	 * @since Forestry 5.8
	 * @deprecated Only for backwards comparability.
	 */
	@Deprecated
	IFarmProperties createFakeInstance(IFarmLogic logic);

	/**
	 * Can be used to create a simple version of a farm logic, like the vanilla vegetable or wheat farm logic.
	 *
	 * @return Null if the farming plugin is not active.
	 */
	@Nullable
	IFarmLogic createCropLogic(IFarmProperties instance, boolean isManual, ISimpleFarmLogic simpleFarmLogic);

	/**
	 * @since Forestry 5.8
	 */
	@Nullable
	IFarmProperties getProperties(String identifier);
	
}
