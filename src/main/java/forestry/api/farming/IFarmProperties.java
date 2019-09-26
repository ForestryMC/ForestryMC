package forestry.api.farming;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

/**
 * @since Forestry 5.8
 */
public interface IFarmProperties {

	default void registerSoil(Block block) {
		registerSoil(new ItemStack(block), block.getDefaultState());
	}
	/**
	 * Can be used to register a {@link Block} as a valid soil.
	 */
	void registerSoil(ItemStack resource, BlockState soilState);

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
	boolean isAcceptedSoil(BlockState block);

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
