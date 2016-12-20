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
package forestry.arboriculture.items;

import forestry.api.arboriculture.EnumPileType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.blocks.BlockPile;
import forestry.arboriculture.tiles.TilePile;
import forestry.core.items.ItemBlockForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.Translator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockPile<B extends Block> extends ItemBlockForestry<Block> {

	public ItemBlockPile(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public B getBlock() {
		//noinspection unchecked
		return (B) super.getBlock();
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if(block == PluginArboriculture.blocks.piles.get(EnumPileType.WOOD)){
			if (itemstack.getTagCompound() == null) {
				return "Unknown";
			}
			IAlleleTreeSpecies species = BlockPile.getTreeSpecies(itemstack);
			if (species == null) {
				return "Unknown";
			}

			String customTreeKey = "for.trees.custom.pile." + species.getUnlocalizedName().replace("trees.species.", "");
			if (Translator.canTranslateToLocal(customTreeKey)) {
				return Translator.translateToLocal(customTreeKey);
			}
			String typeString = Translator.translateToLocal("for.trees.grammar.pile.type");
			return Translator.translateToLocal("for.trees.grammar.pile").replaceAll("%SPECIES", species.getName()).replaceAll("%TYPE", typeString);
		}else{
			return Translator.translateToLocal("for.trees.pile.dirt");
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		boolean placed = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);

		if (placed) {
			if (block == PluginArboriculture.blocks.piles.get(EnumPileType.WOOD)) {
				if (block.hasTileEntity(newState)) {
					if (stack.getTagCompound() != null) {
						TilePile tile = TileUtil.getTile(world, pos, TilePile.class);
						if (tile != null) {
							tile.readFromNBT(stack.getTagCompound());
							tile.setPos(pos);
							tile.markDirty();
						}
					}
				}
			}
		}

		return placed;
	}
}
