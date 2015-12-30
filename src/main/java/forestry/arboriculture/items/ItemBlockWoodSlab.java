package forestry.arboriculture.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumWoodType;
import forestry.arboriculture.blocks.BlockSlab;
import forestry.arboriculture.tiles.TileWood;
import forestry.core.tiles.TileUtil;

public class ItemBlockWoodSlab extends ItemBlockWood {
	private final BlockSlab slab;
	private final BlockSlab doubleSlab;
	private final boolean isDoubleSlab;

	public ItemBlockWoodSlab(Block block, BlockSlab doubleSlab, BlockSlab slab) {
		super(block);
		this.doubleSlab = doubleSlab;
		this.slab = slab;
		this.isDoubleSlab = (block == doubleSlab);
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (isDoubleSlab) {
			return super.onItemUse(itemStack, player, world, x, y, z, side, hitX, hitY, hitZ);
		} else if (itemStack.stackSize == 0) {
			return false;
		} else if (!player.canPlayerEdit(x, y, z, side, itemStack)) {
			return false;
		} else {
			Block block = world.getBlock(x, y, z);
			int i1 = world.getBlockMetadata(x, y, z);
			boolean flag = (i1 & 8) != 0;

			EnumWoodType blockWoodType = null;
			EnumWoodType stackWoodType = getWoodType(itemStack);
			TileWood tile = TileUtil.getTile(world, x, y, z, TileWood.class);
			if (tile != null) {
				blockWoodType = tile.getWoodType();
			}

			if ((side == 1 && !flag || side == 0 && flag) && block == this.slab && blockWoodType == stackWoodType) {
				if (world.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBoxFromPool(world, x, y, z))) {
					if (placeWood(itemStack, stackWoodType, doubleSlab, player, world, x, y, z, 0)) {
						world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), this.doubleSlab.stepSound.func_150496_b(), (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlab.stepSound.getPitch() * 0.8F);
						--itemStack.stackSize;
					}
				}
				
				return true;
			} else {
				if (this.func_150946_a(itemStack, player, world, x, y, z, side)) {
					return true;
				} else {
					return super.onItemUse(itemStack, player, world, x, y, z, side, hitX, hitY, hitZ);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack itemStack) {
		int i1 = x;
		int j1 = y;
		int k1 = z;
		Block block = world.getBlock(x, y, z);
		int l1 = world.getBlockMetadata(x, y, z);
		boolean flag = (l1 & 8) != 0;

		EnumWoodType blockWoodType = null;
		EnumWoodType stackWoodType = getWoodType(itemStack);
		TileWood tile = TileUtil.getTile(world, x, y, z, TileWood.class);
		if (tile != null) {
			blockWoodType = tile.getWoodType();
		}
		
		if ((side == 1 && !flag || side == 0 && flag) && block == this.slab && stackWoodType == blockWoodType) {
			return true;
		} else {
			if (side == 0) {
				--y;
			}
			
			if (side == 1) {
				++y;
			}
			
			if (side == 2) {
				--z;
			}
			
			if (side == 3) {
				++z;
			}
			
			if (side == 4) {
				--x;
			}
			
			if (side == 5) {
				++x;
			}
			
			Block block1 = world.getBlock(x, y, z);
			tile = TileUtil.getTile(world, x, y, z, TileWood.class);
			if (tile == null) {
				blockWoodType = null;
			} else {
				blockWoodType = tile.getWoodType();
			}

			if (block1 == this.slab && stackWoodType == blockWoodType) {
				return true;
			} else {
				return super.func_150936_a(world, i1, j1, k1, side, player, itemStack);
			}
		}
	}
	
	private boolean func_150946_a(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side) {
		if (side == 0) {
			--y;
		}
		
		if (side == 1) {
			++y;
		}
		
		if (side == 2) {
			--z;
		}
		
		if (side == 3) {
			++z;
		}
		
		if (side == 4) {
			--x;
		}
		
		if (side == 5) {
			++x;
		}
		
		Block block = world.getBlock(x, y, z);
		EnumWoodType blockWoodType = null;
		EnumWoodType stackWoodType = getWoodType(itemStack);
		TileWood tile = TileUtil.getTile(world, x, y, z, TileWood.class);
		if (tile != null) {
			blockWoodType = tile.getWoodType();
		}
		
		if (block == this.slab && blockWoodType == stackWoodType) {
			if (world.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBoxFromPool(world, x, y, z))) {
				if (placeWood(itemStack, stackWoodType, doubleSlab, player, world, x, y, z, 0)) {
					world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), this.doubleSlab.stepSound.func_150496_b(), (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlab.stepSound.getPitch() * 0.8F);
					--itemStack.stackSize;
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
}
