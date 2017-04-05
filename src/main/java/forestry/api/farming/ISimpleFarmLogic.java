package forestry.api.farming;

import net.minecraft.item.ItemStack;

/**
 * A simple version of the IFarmLogic. 
 */
public interface ISimpleFarmLogic {

	/**
	 * @return the itemStack that represents this farm logic. Used as an icon for the farm logic.
	 */
	ItemStack getIconItemStack();
	
	Iterable<IFarmable> getSeeds();

	String getName();
	
}
