package forestry.api.genetics;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.gui.IElementGenetic;

/**
 * A tab of the database screen that shows some information about a {@link IIndividual}.
 */
@SideOnly(Side.CLIENT)
public interface IDatabaseTab<I extends IIndividual> {
	/**
	 * Creates the gui elements that are displayed if this tab is selected in the database.
	 *
	 * @param individual The individual that is currently in the database selected.
	 * @param container A helper to create the gui elements.
	 */
	void createElements(IElementGenetic container, I individual, ItemStack itemStack);

	ItemStack getIconStack();

	/**
	 * Can be used to give the tab a custom tooltip.
	 */
	@Nullable
	default String getTooltip(I individual){
		return null;
	}
}
