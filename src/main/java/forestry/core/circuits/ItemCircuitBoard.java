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

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitLayout;
import forestry.core.ModuleCore;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemForestry;

public class ItemCircuitBoard extends ItemForestry implements IColoredItem {

	private final EnumCircuitBoardType type;

	public ItemCircuitBoard(EnumCircuitBoardType type) {
		this.type = type;
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> subItems) {
		if (this.isInGroup(tab)) {
			subItems.add(createCircuitboard(type, null, new ICircuit[]{}));
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getColorFromItemStack(ItemStack itemstack, int tintIndex) {
		if (tintIndex == 0) {
			return type.getPrimaryColor();
		} else {
			return type.getSecondaryColor();
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack itemstack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);
		ICircuitBoard circuitboard = ChipsetManager.circuitRegistry.getCircuitBoard(itemstack);
		if (circuitboard != null) {
			circuitboard.addTooltip(list);
		}
	}

	public static ItemStack createCircuitboard(EnumCircuitBoardType type, @Nullable ICircuitLayout layout, ICircuit[] circuits) {
		CompoundNBT compoundNBT = new CompoundNBT();
		new CircuitBoard(type, layout, circuits).write(compoundNBT);
		ItemStack stack = ModuleCore.getItems().getCircuitBoard(type, 1);
		stack.setTag(compoundNBT);
		return stack;
	}

	public ItemStack get(EnumCircuitBoardType type) {
		return ModuleCore.getItems().getCircuitBoard(type, 1);
	}
}
