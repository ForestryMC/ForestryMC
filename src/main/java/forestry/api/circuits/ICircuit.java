/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.circuits;

import java.util.List;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public interface ICircuit {
    String getUID();

    String getTranslationKey();

    default ITextComponent getDisplayName() {
        return new TranslationTextComponent(getTranslationKey());
    }

    boolean isCircuitable(Object tile);

    void onInsertion(int slot, Object tile);

    void onLoad(int slot, Object tile);

    void onRemoval(int slot, Object tile);

    void onTick(int slot, Object tile);

    void addTooltip(List<ITextComponent> list);
}
