package forestry.api.genetics.gatgets;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.elements.DatabaseElement;

import genetics.api.individual.IIndividual;

/**
 * A tab of the database screen that shows some information about a {@link IIndividual}.
 */
@OnlyIn(Dist.CLIENT)
public interface IDatabaseTab<I extends IIndividual> {
	/**
	 * Creates the gui elements that are displayed if this tab is selected in the database.
	 *
	 * @param container  A helper to create the gui elements.
	 * @param individual The individual that is currently in the database selected.
	 */
	void createElements(DatabaseElement container, I individual, ItemStack itemStack);

	ItemStack getIconStack();

	/**
	 * Can be used to give the tab a custom tooltip.
	 */
	default Component getTooltip(I individual) {
		return Component.empty();
	}

	default DatabaseMode getMode() {
		return DatabaseMode.ACTIVE;
	}
}
