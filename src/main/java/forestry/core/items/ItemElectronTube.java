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

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.core.circuits.SolderManager;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public class ItemElectronTube extends ItemOverlay {

	public ItemElectronTube(CreativeTabs tab, OverlayInfo... overlays) {
		super(tab, overlays);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {

		Multimap<ICircuitLayout, ICircuit> circuits = ArrayListMultimap.create();
		for (ICircuitLayout circuitLayout : ChipsetManager.circuitRegistry.getRegisteredLayouts().values()) {
			ICircuit circuit = SolderManager.getCircuit(circuitLayout, itemstack);
			if (circuit != null) {
				circuits.put(circuitLayout, circuit);
			}
		}

		if (circuits.size() > 0) {
			if (Proxies.common.isShiftDown()) {
				for (ICircuitLayout circuitLayout : circuits.keys()) {
					String circuitLayoutName = circuitLayout.getUsage();
					list.add(EnumChatFormatting.WHITE.toString() + EnumChatFormatting.UNDERLINE + circuitLayoutName);
					for (ICircuit circuit : circuits.get(circuitLayout)) {
						circuit.addTooltip(list);
					}
				}
			} else {
				list.add(EnumChatFormatting.ITALIC + "<" + StringUtil.localize("gui.tooltip.tmi") + ">");
			}
		} else {
			list.add("<" + StringUtil.localize("gui.noeffect") + ">");
		}
	}
}
