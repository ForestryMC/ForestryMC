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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.core.items.ItemForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public abstract class ItemGE extends ItemForestry {
	protected ItemGE(CreativeTabs creativeTab) {
		super(creativeTab);
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
	public boolean hasEffect(ItemStack stack) {
		IAlleleSpecies species = getSpecies(stack);
		return species != null && species.hasEffect();
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {
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
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		// Need to disable normal registration.
	}

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
