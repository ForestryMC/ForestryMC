package forestry.api.farming;

import java.util.Collection;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

/**
 * @since Forestry 5.8
 */
public interface IFarmProperties {

	/**
	 * Can be used to register a {@link IBlockState} as a valid soil.
	 */
	default void registerSoil(ItemStack resource, IBlockState soilState){
		registerSoil(resource, soilState, false);
	}

	/**
	 * Can be used to register a {@link IBlockState} as a valid soil.
	 */
	void registerSoil(ItemStack resource, IBlockState soilState, boolean hasMetaData);

	/**
	 * Adds the {@link IFarmable}s that where registered with the given identifier.
	 */
	void registerFarmables(String identifier);

	/**
	 * @return true if the given block state is a valid soil state.
	 */
	boolean isAcceptedSoil(IBlockState blockState);

	/**
	 * @return true if the given stack is the {@link ItemStack} of a soil.
	 */
	boolean isAcceptedResource(ItemStack itemStack);

	Collection<ISoil> getSoils();

	Collection<IFarmable> getFarmables();

	/**
	 * Returns the instance of the manual or managed {@link IFarmLogic}.
	 */
	IFarmLogic getLogic(boolean manuel);
}
