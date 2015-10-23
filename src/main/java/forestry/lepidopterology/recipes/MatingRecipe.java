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
package forestry.lepidopterology.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IButterfly;

public class MatingRecipe implements IRecipe {

	private final ItemStack unknown;
	private ItemStack cached;
	
	public MatingRecipe() {
		unknown = ButterflyManager.butterflyRoot.getMemberStack(ButterflyManager.butterflyRoot.getIndividualTemplates().get(0), EnumFlutterType.BUTTERFLY.ordinal());
	}
	
	@Override
	public boolean matches(InventoryCrafting crafting, World world) {
		
		boolean mated = true;
		int butterflies = 0;
		int sera = 0;
		
		for (int i = 0; i < crafting.getSizeInventory(); i++) {
			if (ButterflyManager.butterflyRoot.isMember(crafting.getStackInSlot(i), EnumFlutterType.BUTTERFLY.ordinal())) {
				butterflies++;
				mated = ButterflyManager.butterflyRoot.isMated(crafting.getStackInSlot(i));
				cached = crafting.getStackInSlot(i);
			} else if (ButterflyManager.butterflyRoot.isMember(crafting.getStackInSlot(i), EnumFlutterType.SERUM.ordinal())) {
				sera++;
			}
		}
		
		return !mated && butterflies == 1 && sera == 1;
	}

	@Override
	public ItemStack getRecipeOutput() {
		if (cached != null) {
			return cached;
		} else {
			return unknown;
		}
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting) {
		IButterfly butterfly = null;
		IButterfly serum = null;
		for (int i = 0; i < crafting.getSizeInventory(); i++) {
			if (ButterflyManager.butterflyRoot.isMember(crafting.getStackInSlot(i), EnumFlutterType.BUTTERFLY.ordinal())) {
				butterfly = ButterflyManager.butterflyRoot.getMember(crafting.getStackInSlot(i));
			} else if (ButterflyManager.butterflyRoot.isMember(crafting.getStackInSlot(i), EnumFlutterType.SERUM.ordinal())) {
				serum = ButterflyManager.butterflyRoot.getMember(crafting.getStackInSlot(i));
			}
		}
		if (butterfly == null || serum == null) {
			return null;
		}
		
		IButterfly mated = butterfly.copy();
		mated.mate(serum);
		return ButterflyManager.butterflyRoot.getMemberStack(mated, EnumFlutterType.BUTTERFLY.ordinal());
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

}
