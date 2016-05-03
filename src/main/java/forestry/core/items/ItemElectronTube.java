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

import java.util.Collection;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.core.CreativeTabForestry;
import forestry.core.circuits.SolderManager;
import forestry.core.config.Config;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Translator;

public class ItemElectronTube extends ItemOverlay {

	public ItemElectronTube() {
		super(CreativeTabForestry.tabForestry, EnumElectronTube.VALUES);
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {
		Multimap<ICircuitLayout, ICircuit> circuits = getCircuits(itemstack);
		if (!circuits.isEmpty()) {
			if (Proxies.common.isShiftDown()) {
				for (ICircuitLayout circuitLayout : circuits.keys()) {
					String circuitLayoutName = circuitLayout.getUsage();
					list.add(TextFormatting.WHITE.toString() + TextFormatting.UNDERLINE + circuitLayoutName);
					for (ICircuit circuit : circuits.get(circuitLayout)) {
						circuit.addTooltip(list);
					}
				}
			} else {
				list.add(TextFormatting.ITALIC + "<" + Translator.translateToLocal("for.gui.tooltip.tmi") + ">");
			}
		} else {
			list.add("<" + Translator.translateToLocal("for.gui.noeffect") + ">");
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
		for (int i = 0; i < overlays.length; i++) {
			if (Config.isDebug || !overlays[i].isSecret()) {
				ItemStack itemStack = new ItemStack(this, 1, i);
				if (Config.isDebug || !getCircuits(itemStack).isEmpty()) {
					itemList.add(itemStack);
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
