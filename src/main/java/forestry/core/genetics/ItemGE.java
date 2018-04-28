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

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.core.items.ItemForestry;
import forestry.core.utils.Translator;

public abstract class ItemGE extends ItemForestry {
	protected ItemGE(CreativeTabs creativeTab) {
		super(creativeTab);
		setHasSubtypes(true);
	}

	@Nullable
	public abstract IIndividual getIndividual(ItemStack itemstack);

	protected abstract IAlleleSpecies getSpecies(ItemStack itemStack);

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		if (!stack.hasTagCompound()) { // villager trade wildcard bees
			return false;
		}
		IAlleleSpecies species = getSpecies(stack);
		return species.hasEffect();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		if (itemstack.getTagCompound() == null) {
			return;
		}

		IIndividual individual = getIndividual(itemstack);

		if (individual != null && individual.isAnalyzed()) {
			if (GuiScreen.isShiftKeyDown()) {
				individual.addTooltip(list);
			} else {
				list.add(TextFormatting.ITALIC + "<" + Translator.translateToLocal("for.gui.tooltip.tmi") + ">");
			}
		} else {
			list.add("<" + Translator.translateToLocal("for.gui.unknown") + ">");
		}
	}

	@Nullable
	@Override
	public String getCreatorModId(ItemStack itemStack) {
		IAlleleSpecies species = getSpecies(itemStack);
		return species.getModID();
	}
}
