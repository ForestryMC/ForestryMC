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
package forestry.core.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;


import java.util.Collection;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;


import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.core.CreativeTabForestry;
import forestry.core.circuits.SolderManager;
import forestry.core.config.Config;
import forestry.core.utils.Translator;

import forestry.core.utils.ItemTooltipUtil;

public class ItemElectronTube extends ItemOverlay {

	public ItemElectronTube() {
		super(CreativeTabForestry.tabForestry, EnumElectronTube.VALUES);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		super.addInformation(itemstack, world, list, flag);
		Multimap<ICircuitLayout, ICircuit> circuits = getCircuits(itemstack);
		if (!circuits.isEmpty()) {
			if (GuiScreen.isShiftKeyDown()) {
				for (ICircuitLayout circuitLayout : circuits.keys()) {
					String circuitLayoutName = circuitLayout.getUsage();
					list.add(TextFormatting.WHITE.toString() + TextFormatting.UNDERLINE + circuitLayoutName);
					for (ICircuit circuit : circuits.get(circuitLayout)) {
						circuit.addTooltip(list);
					}
				}
			} else {
				ItemTooltipUtil.addShiftInformation(itemstack, world, list, flag);
			}
		} else {
			list.add("<" + Translator.translateToLocal("for.gui.noeffect") + ">");
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)) {
			for (int i = 0; i < overlays.length; i++) {
				if (Config.isDebug || !overlays[i].isSecret()) {
					ItemStack itemStack = new ItemStack(this, 1, i);
					if (Config.isDebug || !getCircuits(itemStack).isEmpty()) {
						subItems.add(itemStack);
					}
				}
			}
		}
	}

	private static Multimap<ICircuitLayout, ICircuit> getCircuits(ItemStack itemStack) {
		Multimap<ICircuitLayout, ICircuit> circuits = ArrayListMultimap.create();
		Collection<ICircuitLayout> allLayouts = ChipsetManager.circuitRegistry.getRegisteredLayouts().values();
		for (ICircuitLayout circuitLayout : allLayouts) {
			ICircuit circuit = SolderManager.getCircuit(circuitLayout, itemStack);
			if (circuit != null) {
				circuits.put(circuitLayout, circuit);
			}
		}
		return circuits;
	}

	public ItemStack get(EnumElectronTube type, int amount) {
		return new ItemStack(this, amount, type.ordinal());
	}
}
