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
package forestry.core.circuits;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.core.utils.ResourceUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public abstract class Circuit implements ICircuit {
    private final String uid;

    protected Circuit(String uid) {
        this.uid = uid;

        ChipsetManager.circuitRegistry.registerCircuit(this);
    }

    @Override
    public String getUID() {
        return "forestry." + this.uid;
    }

    @Override
    public String getTranslationKey() {
        return "for.circuit." + this.uid;
    }

    @Override
    public void addTooltip(List<ITextComponent> list) {
        list.add(new TranslationTextComponent(getTranslationKey()).mergeStyle(TextFormatting.GRAY));

        int i = 1;
        while (true) {
            String unlocalizedDescription = getTranslationKey() + ".description." + i;
            TranslationTextComponent component = new TranslationTextComponent(unlocalizedDescription);
            if (!ResourceUtil.canTranslate(component)) {
                break;
            }
            list.add(new StringTextComponent(" - ").append(component).mergeStyle(TextFormatting.GRAY));
            i++;
        }
    }
}
