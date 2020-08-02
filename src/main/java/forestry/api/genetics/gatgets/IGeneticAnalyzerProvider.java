package forestry.api.genetics.gatgets;

import net.minecraft.item.ItemStack;

public interface IGeneticAnalyzerProvider {

    ItemStack getSpecimen(int index);

    default boolean onUpdateSelected() {
        return false;
    }

    default void onSelection(int index, boolean changed) {

    }

    int getSelectedSlot(int index);

    int getSlotCount();

    int getFirstSlot();
}
