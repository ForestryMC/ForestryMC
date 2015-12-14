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
package forestry.apiculture.inventory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.BeeManager;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.inventory.ItemInventoryAlyzer;

public class ItemInventoryBeealyzer extends ItemInventoryAlyzer implements IHintSource {

	public ItemInventoryBeealyzer(EntityPlayer player, ItemStack itemStack) {
		super(BeeManager.beeRoot, player, itemStack);
	}

	/* IHintSource */
	@Override
	public List<String> getHints() {
		return Config.hints.get("beealyzer");
	}
}
