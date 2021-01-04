/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.fluids;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;
import java.util.List;

public class PipetteContents {

    private final FluidStack contents;

    @Nullable
    public static PipetteContents create(ItemStack itemStack) {
        FluidStack contents = FluidUtil.getFluidContained(itemStack).orElse(FluidStack.EMPTY);
        if (contents.isEmpty()) {
            return null;
        }
        return new PipetteContents(contents);
    }

    public PipetteContents(FluidStack contents) {
        this.contents = contents;
    }

    public FluidStack getContents() {
        return contents;
    }

    public boolean isFull() {
        return contents.getAmount() >= FluidAttributes.BUCKET_VOLUME;
    }

    public void addTooltip(List<ITextComponent> list) {
        TextComponent descr = new TranslationTextComponent(contents.getFluid().getAttributes().getTranslationKey(contents));
        descr.appendString(" (" + contents.getAmount() + " mb)");

        list.add(descr);
    }
}
