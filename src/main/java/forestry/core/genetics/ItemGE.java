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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelObject;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public abstract class ItemGE extends Item {

	protected ItemGE() {
		super();
		// maxStackSize = 1;
		hasSubtypes = true;
	}

	protected abstract int getDefaultPrimaryColour();

	protected abstract int getDefaultSecondaryColour();

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
	public boolean hasEffect(ItemStack itemstack) {
		IAlleleSpecies species = getSpecies(itemstack);
		return species != null && species.hasEffect();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		if (!itemstack.hasTagCompound()) {
			return;
		}

		IIndividual individual = getIndividual(itemstack);

		if (individual.isAnalyzed()) {
			if (Proxies.common.isShiftDown()) {
				individual.addTooltip(list);
			} else {
				list.add(EnumChatFormatting.ITALIC + "<" + StringUtil.localize("gui.tooltip.tmi") + ">");
			}
		} else {
			list.add("<" + StringUtil.localize("gui.unknown") + ">");
		}
	}

	/* ICONS */

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {

		if (renderPass == 0) {
			return getDefaultPrimaryColour();
		} else if (renderPass == 1) {
			return getDefaultSecondaryColour();
		} else {
			return 0xffffff;
		}

	}

	public int getColourFromSpecies(IAlleleSpecies species, int renderPass) {
		return 0xffffff;
	}

}
