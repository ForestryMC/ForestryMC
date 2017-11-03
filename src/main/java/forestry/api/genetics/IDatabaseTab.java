package forestry.api.genetics;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IGuiElementHelper;

/**
 * A tab of the database screen that shows some information about a {@link IIndividual}.
 */
@SideOnly(Side.CLIENT)
public interface IDatabaseTab<I extends IIndividual> {
	/**
	 * Creates the gui elements that are displayed if this tab is selected in the database.
	 *
	 * @param individual The individual that is currently in the database selected.
	 * @param elementHelper A helper to create the gui elements.
	 */
	void createElements(IGuiElementHelper elementHelper, I individual, ItemStack itemStack);

	/**
	 * @return The slot of this tab.
	 */
	EnumDatabaseTab getTab();

	/**
	 * Can be used to give the tab a custom tooltip.
	 */
	@Nullable
	default String getTooltip(I individual){
		return null;
	}
}
