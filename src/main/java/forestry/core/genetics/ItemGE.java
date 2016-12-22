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

import java.util.List;

import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.core.items.ItemForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Translator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public abstract class ItemGE extends ItemForestry {
	protected ItemGE(CreativeTabs creativeTab) {
		super(creativeTab);
		setHasSubtypes(true);
	}

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
		IAlleleSpecies species = getSpecies(stack);
		return species.hasEffect();
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {
		if (itemstack.getTagCompound() == null) {
			return;
		}

		IIndividual individual = getIndividual(itemstack);

		if (individual.isAnalyzed()) {
			if (Proxies.common.isShiftDown()) {
				individual.addTooltip(list);
			} else {
				list.add(TextFormatting.ITALIC + "<" + Translator.translateToLocal("for.gui.tooltip.tmi") + ">");
			}
		} else {
			list.add("<" + Translator.translateToLocal("for.gui.unknown") + ">");
		}
	}
}
