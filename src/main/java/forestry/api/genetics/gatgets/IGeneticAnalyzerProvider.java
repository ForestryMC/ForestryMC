package forestry.api.genetics.gatgets;

import net.minecraft.item.ItemStack;

public interface IGeneticAnalyzerProvider {

	ItemStack getSpecimen(int index);

	boolean onUpdateSelected();

	void onSelection(int index, boolean changed);

	int getSelectedSlot(int index);

	int getSlotCount();

	int getFirstSlot();
}
