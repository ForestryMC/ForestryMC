package forestry.api.farming;

import java.util.Collection;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

/**
 * @since Forestry 5.8
 */
public interface IFarmProperties {

	/**
	 * Can be used to register a {@link BlockState} as a valid soil.
	 */
	default void registerSoil(ItemStack resource, BlockState soilState) {
		registerSoil(resource, soilState, false);
	}

	/**
	 * Can be used to register a {@link BlockState} as a valid soil.
	 */
	void registerSoil(ItemStack resource, BlockState soilState, boolean hasMetaData);

	void addGermlings(ItemStack... germlings);

	void addGermlings(Collection<ItemStack> germlings);

	void addProducts(ItemStack... products);

	void addProducts(Collection<ItemStack> products);

	/**
	 * Adds the {@link IFarmable}s that where registered with the given identifier.
	 */
	void registerFarmables(String identifier);

	/**
	 * @return true if the given block state is a valid soil state.
	 */
	boolean isAcceptedSoil(BlockState blockState);

	/**
	 * @return true if the given stack is the {@link ItemStack} of a soil.
	 */
	boolean isAcceptedResource(ItemStack itemStack);

	Collection<ISoil> getSoils();

	Collection<IFarmable> getFarmables();

	Collection<IFarmableInfo> getFarmableInfo();

	/**
	 * Returns the instance of the manual or managed {@link IFarmLogic}.
	 */
	IFarmLogic getLogic(boolean manuel);
}
