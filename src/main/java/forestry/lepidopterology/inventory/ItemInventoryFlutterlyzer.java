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
package forestry.lepidopterology.inventory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.inventory.ItemInventoryAlyzer;

public class ItemInventoryFlutterlyzer extends ItemInventoryAlyzer implements IHintSource {

	public ItemInventoryFlutterlyzer(EntityPlayer player, ItemStack itemStack) {
		super(ButterflyManager.butterflyRoot, player, itemStack);
	}

	/* IHintSource */
	@Override
	public List<String> getHints() {
		return Config.hints.get("flutterlyzer");
	}
}
