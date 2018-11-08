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

import javax.annotation.Nullable;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeHooks;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.config.Constants;

public class MatingRecipe implements IRecipe {

	private final ItemStack unknown;
	@Nullable
	private ItemStack cached;

	public MatingRecipe() {
		unknown = ButterflyManager.butterflyRoot.getMemberStack(ButterflyManager.butterflyRoot.getIndividualTemplates().get(0), EnumFlutterType.BUTTERFLY);
	}

	@Override
	public boolean matches(InventoryCrafting crafting, World world) {

		boolean mated = true;
		int butterflies = 0;
		int sera = 0;

		for (int i = 0; i < crafting.getSizeInventory(); i++) {
			if (ButterflyManager.butterflyRoot.isMember(crafting.getStackInSlot(i), EnumFlutterType.BUTTERFLY)) {
				butterflies++;
				mated = ButterflyManager.butterflyRoot.isMated(crafting.getStackInSlot(i));
				cached = crafting.getStackInSlot(i);
			} else if (ButterflyManager.butterflyRoot.isMember(crafting.getStackInSlot(i), EnumFlutterType.SERUM)) {
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
			if (ButterflyManager.butterflyRoot.isMember(crafting.getStackInSlot(i), EnumFlutterType.BUTTERFLY)) {
				butterfly = ButterflyManager.butterflyRoot.getMember(crafting.getStackInSlot(i));
			} else if (ButterflyManager.butterflyRoot.isMember(crafting.getStackInSlot(i), EnumFlutterType.SERUM)) {
				serum = ButterflyManager.butterflyRoot.getMember(crafting.getStackInSlot(i));
			}
		}
		if (butterfly == null || serum == null) {
			return ItemStack.EMPTY;
		}

		IButterfly mated = butterfly.copy();
		mated.mate(serum);
		return ButterflyManager.butterflyRoot.getMemberStack(mated, EnumFlutterType.BUTTERFLY);
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> aitemstack = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			aitemstack.set(i, ForgeHooks.getContainerItem(itemstack));
		}
		return aitemstack;
	}

	@Override
	public IRecipe setRegistryName(ResourceLocation name) {
		throw new IllegalStateException();
	}

	@Nullable
	@Override
	public ResourceLocation getRegistryName() {
		return new ResourceLocation(Constants.MOD_ID, "butterflyMating");
	}

	@Override
	public Class<IRecipe> getRegistryType() {
		return IRecipe.class;
	}
}
