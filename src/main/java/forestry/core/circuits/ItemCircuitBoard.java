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

import java.util.List;
import java.util.Locale;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitLayout;
import forestry.core.items.ItemForestryMultiPass;
import forestry.plugins.PluginCore;

public class ItemCircuitBoard extends ItemForestryMultiPass {

	public ItemCircuitBoard() {
		super();
		setHasSubtypes(true);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(createCircuitboard(EnumCircuitBoardType.BASIC, null, new ICircuit[]{}));
		itemList.add(createCircuitboard(EnumCircuitBoardType.ENHANCED, null, new ICircuit[]{}));
		itemList.add(createCircuitboard(EnumCircuitBoardType.REFINED, null, new ICircuit[]{}));
		itemList.add(createCircuitboard(EnumCircuitBoardType.INTRICATE, null, new ICircuit[]{}));
	}

	/**
	 * @return true if the item's stackTagCompound needs to be synchronized over SMP.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int pass) {
		EnumCircuitBoardType type = EnumCircuitBoardType.values()[itemstack.getItemDamage()];
		if (pass == 0) {
			return type.getPrimaryColor();
		} else {
			return type.getSecondaryColor();
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		EnumCircuitBoardType type = EnumCircuitBoardType.values()[stack.getItemDamage()];
		return "item.for.circuitboard." + type.toString().toLowerCase(Locale.ENGLISH);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		ICircuitBoard circuitboard = ChipsetManager.circuitRegistry.getCircuitboard(itemstack);
		if (circuitboard != null) {
			circuitboard.addTooltip(list);
		}
	}

	public static ItemStack createCircuitboard(EnumCircuitBoardType type, ICircuitLayout layout, ICircuit[] circuits) {
		ItemStack chipset = PluginCore.items.circuitboards.get(type);
		saveChipset(chipset, new CircuitBoard(type, layout, circuits));
		return chipset;
	}

	private static void saveChipset(ItemStack itemstack, ICircuitBoard circuitboard) {
		if (circuitboard == null) {
			itemstack.setTagCompound(null);
			return;
		}

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		circuitboard.writeToNBT(nbttagcompound);
		itemstack.setTagCompound(nbttagcompound);
	}

	public ItemStack get(EnumCircuitBoardType type) {
		return new ItemStack(this, 1, type.ordinal());
	}
}
