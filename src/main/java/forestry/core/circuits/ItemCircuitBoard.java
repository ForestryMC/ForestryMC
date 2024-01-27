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

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitLayout;
import forestry.core.features.CoreItems;
import forestry.core.items.ItemForestry;
import forestry.core.items.definitions.IColoredItem;

public class ItemCircuitBoard extends ItemForestry implements IColoredItem {

	private final EnumCircuitBoardType type;

	public ItemCircuitBoard(EnumCircuitBoardType type) {
		this.type = type;
	}

	public EnumCircuitBoardType getType() {
		return type;
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> subItems) {
		if (this.allowedIn(tab)) {
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
	public void appendHoverText(ItemStack itemstack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		ICircuitBoard circuitboard = ChipsetManager.circuitRegistry.getCircuitBoard(itemstack);
		if (circuitboard != null) {
			circuitboard.addTooltip(list);
		}
	}

	public static ItemStack createCircuitboard(EnumCircuitBoardType type, @Nullable ICircuitLayout layout, ICircuit[] circuits) {
		CompoundTag compoundNBT = new CompoundTag();
		new CircuitBoard(type, layout, circuits).write(compoundNBT);
		ItemStack stack = CoreItems.CIRCUITBOARDS.stack(type, 1);
		stack.setTag(compoundNBT);
		return stack;
	}

	public ItemStack get(EnumCircuitBoardType type) {
		return CoreItems.CIRCUITBOARDS.stack(type, 1);
	}
}
