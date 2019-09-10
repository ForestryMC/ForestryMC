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
package forestry.farming.blocks;

import com.google.common.collect.ImmutableTable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;
import forestry.farming.items.ItemBlockFarm;
import forestry.farming.models.EnumFarmBlockTexture;

public class BlockRegistryFarming extends BlockRegistry {
	public final BlockMushroom mushroom;
	public final ImmutableTable<EnumFarmBlockType, EnumFarmBlockTexture, BlockFarm> farms;

	public BlockRegistryFarming() {
		mushroom = new BlockMushroom();
		registerBlock(mushroom, new ItemBlockForestry<>(mushroom, new Item.Properties().group(null)), "mushroom");

		ImmutableTable.Builder<EnumFarmBlockType, EnumFarmBlockTexture, BlockFarm> builder = ImmutableTable.builder();
		for (EnumFarmBlockTexture texture : EnumFarmBlockTexture.values()) {
			for (EnumFarmBlockType type : EnumFarmBlockType.VALUES) {
				BlockFarm block = new BlockFarm(type, texture);
				registerBlock(block, new ItemBlockFarm(block), "ffarm_" + type.getName() + "_" + texture.getUid());
				builder.put(type, texture, block);
			}
		}

		farms = builder.build();
	}


	public ItemStack getFarmBlock(EnumFarmBlockType type, EnumFarmBlockTexture texture, int amount) {
		return new ItemStack(farms.get(type, texture), amount);
	}
}
