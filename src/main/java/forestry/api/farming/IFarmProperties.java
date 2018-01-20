package forestry.api.farming;

import java.util.Collection;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

/**
 * @since Forestry 5.8
 */
public interface IFarmProperties {

	default void registerSoil(ItemStack resource, IBlockState soilState){
		registerSoil(resource, soilState, false);
	}

	void registerFarmables(String identifier);

	void registerSoil(ItemStack resource, IBlockState soilState, boolean hasMetaData);

	boolean isAcceptedSoil(IBlockState blockState);

	boolean isAcceptedResource(ItemStack itemStack);

	String getIdentifier();

	Collection<ISoil> getSoils();

	Collection<IFarmable> getFarmables();

	IFarmLogic getLogic(boolean manuel);
}
