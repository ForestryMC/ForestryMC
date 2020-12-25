/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.genetics;

import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.core.items.ItemForestry;
import genetics.api.GeneticHelper;
import genetics.api.individual.IIndividual;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public abstract class ItemGE extends ItemForestry {
    protected ItemGE(Item.Properties properties) {
        super(properties.setNoRepair());
    }

    protected abstract IAlleleForestrySpecies getSpecies(ItemStack itemStack);

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        if (!stack.hasTag()) { // villager trade wildcard bees
            return false;
        }
        IAlleleForestrySpecies species = getSpecies(stack);
        return species.hasEffect();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(
            ItemStack itemstack,
            @Nullable World world,
            List<ITextComponent> list,
            ITooltipFlag flag
    ) {
        if (!itemstack.hasTag()) {
            return;
        }

        Optional<IIndividual> optionalIndividual = GeneticHelper.getIndividual(itemstack)
                                                                .filter(IIndividual::isAnalyzed);
        if (optionalIndividual.isPresent()) {
            IIndividual individual = optionalIndividual.get();
            if (Screen.hasShiftDown()) {
                individual.addTooltip(list);
            } else {
                list.add(new TranslationTextComponent("for.gui.tooltip.tmi", "< %s >")
                        .mergeStyle(TextFormatting.GRAY)
                        .mergeStyle(TextFormatting.ITALIC));
            }
        } else {
            list.add(new TranslationTextComponent("for.gui.unknown", "< %s >").mergeStyle(TextFormatting.GRAY));
        }
    }

    @Nullable
    @Override
    public String getCreatorModId(ItemStack itemStack) {
        IAlleleForestrySpecies species = getSpecies(itemStack);
        return species.getRegistryName().getNamespace();
    }
}
