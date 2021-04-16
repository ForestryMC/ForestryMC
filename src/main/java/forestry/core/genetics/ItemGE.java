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
package forestry.core.genetics;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

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

import forestry.api.core.tooltips.ToolTip;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.apiculture.DisplayHelper;
import forestry.core.items.ItemForestry;

import genetics.api.GeneticHelper;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;

public abstract class ItemGE extends ItemForestry {
	protected ItemGE(Item.Properties properties) {
		super(properties.setNoRepair());
	}

	protected abstract IAlleleForestrySpecies getSpecies(ItemStack itemStack);

	protected abstract IOrganismType getType();

	@Override
	public boolean canBeDepleted() {
		return false;
	}

	@Override
	public ITextComponent getName(ItemStack itemStack) {
		if (GeneticHelper.getOrganism(itemStack).isEmpty()) {
			return super.getName(itemStack);
		}
		IAlleleForestrySpecies species = getSpecies(itemStack);

		return species.getItemName(getType());
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		if (!stack.hasTag()) { // villager trade wildcard bees
			return false;
		}
		IAlleleForestrySpecies species = getSpecies(stack);
		return species.hasEffect();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemstack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		if (!itemstack.hasTag()) {
			return;
		}

		Optional<IIndividual> optionalIndividual = GeneticHelper.getIndividual(itemstack).filter(IIndividual::isAnalyzed);
		if (optionalIndividual.isPresent()) {
			IIndividual individual = optionalIndividual.get();
			if (Screen.hasShiftDown()) {
				ToolTip toolTip = new ToolTip();
				DisplayHelper.getInstance()
						.getTooltips(individual.getRoot().getUID(), getType())
						.forEach((provider) -> provider.addTooltip(toolTip, individual.getGenome(), individual));
				list.addAll(toolTip.getLines());
				if (toolTip.isEmpty()) {
					individual.addTooltip(list);
				}
			} else {
				list.add(new TranslationTextComponent("for.gui.tooltip.tmi", "< %s >").withStyle(TextFormatting.GRAY).withStyle(TextFormatting.ITALIC));
			}
		} else {
			list.add(new TranslationTextComponent("for.gui.unknown", "< %s >").withStyle(TextFormatting.GRAY));
		}
	}

	@Nullable
	@Override
	public String getCreatorModId(ItemStack itemStack) {
		IAlleleForestrySpecies species = getSpecies(itemStack);
		return species.getRegistryName().getNamespace();
	}
}
