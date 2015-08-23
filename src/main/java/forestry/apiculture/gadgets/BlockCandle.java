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
package forestry.apiculture.gadgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import forestry.api.core.IModelObject;
import forestry.api.core.IVariantObject;
import forestry.api.core.Tabs;
import forestry.core.ForestryClient;
import forestry.core.config.Defaults;
import forestry.core.gadgets.BlockResourceStorageBlock.Resources;
import forestry.core.render.TextureManager;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;

public class BlockCandle extends BlockTorch implements IModelObject, IVariantObject {

	private static final PropertyEnum STATE = PropertyEnum.create("state", State.class);
	
	public enum State implements IStringSerializable{
		ON, OFF;

		@Override
		public String getName() {
			return name();
		}
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((State)state.getValue(STATE)).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(STATE, State.values()[meta]);
	}
	
	private final ArrayList<Item> lightingItems = new ArrayList<Item>();

	public BlockCandle() {
		super();
		this.setHardness(0.0F);
		this.setStepSound(soundTypeWood);
		setCreativeTab(Tabs.tabApiculture);

		lightingItems.add(Items.flint_and_steel);
		lightingItems.add(Items.flint);
		lightingItems.add(Item.getItemFromBlock(Blocks.torch));
		setDefaultState(this.blockState.getBaseState().withProperty(STATE, Resources.APATITE).withProperty(FACING, EnumFacing.UP));
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, STATE, FACING);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileCandle();
	}

	@Override
	public int getRenderType() {
		return ForestryClient.candleRenderId;
	}
	
	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos) {
		int meta = getMetaFromState(world.getBlockState(pos));
		return (isLit(meta)) ? 14 : 0;
	}
	
	@Override
	public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
		if(renderPass == 1)
			return ((TileCandle)world.getTileEntity(pos)).getColour();
		return super.colorMultiplier(world, pos, renderPass);
	}

	public int getColourFromItemStack(ItemStack stack) {
		int colour = 0xffffff;
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			colour = (tag.getByte("red") << 16) | (tag.getByte("green") << 8) | tag.getByte("blue");
		}
		return colour;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
	}

	// Dye names correspond to colour values as below.
	private static final String[] dyes = {"dyeWhite", "dyeOrange", "dyeMagenta", "dyeLightBlue",
			"dyeYellow", "dyeLime", "dyePink", "dyeGray",
			"dyeLightGray", "dyeCyan", "dyePurple", "dyeBlue",
			"dyeBrown", "dyeGreen", "dyeRed", "dyeBlack"};

	private static final int[][] colours = {{255, 255, 255}, {219, 125, 62}, {255, 20, 255}, {107, 138, 201},
			{255, 255, 20}, {20, 255, 20}, {208, 132, 153}, {74, 74, 74},
			{154, 161, 161}, {20, 255, 255}, {126, 61, 181}, {20, 20, 255},
			{79, 50, 31}, {53, 70, 27}, {150, 52, 48}, {20, 20, 20}};

	public static final String colourTagName = "colour";
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing facing, float facingX, float facingY, float facingZ) {
		boolean flag = false;
		int meta = state.getBlock().getMetaFromState(state);
		boolean toggleLitState = true;
		ItemStack held = player.getCurrentEquippedItem();

		if (!isLit(meta)) {
			if (held == null || !lightingItems.contains(held.getItem())) {
				toggleLitState = false;
			} else if (StackUtils.equals(this, held) && isLit(held)) {
				toggleLitState = true;
			}
		}

		if (held != null) {
			// Ensure a TileEntity exists. May be able to remove this in future versions.
			TileCandle te = (TileCandle) world.getTileEntity(pos);
			if (te == null) {
				world.setTileEntity(pos, this.createTileEntity(world, getStateFromMeta(meta)));
				te = (TileCandle) world.getTileEntity(pos);
			}

			if (StackUtils.equals(this, held)) {
				if (!isLit(held)) {
					// Copy the colour of an unlit, coloured candle.
					if (held.hasTagCompound() && held.getTagCompound().hasKey(colourTagName)) {
						te.setColour(held.getTagCompound().getInteger(colourTagName));
					} else {
						// Reset to white if item has no
						te.setColour(0xffffff);
					}
				} else {
					toggleLitState = true;
				}
				flag = true;
			} else {
				// Check for dye-able ness.
				boolean matched = false;
				for (int i = 0; i < dyes.length; ++i) {
					for (ItemStack stack : OreDictionary.getOres(dyes[i])) {
						if (OreDictionary.itemMatches(stack, held, true)) {
							if (isLit(meta)) {
								te.setColour(colours[i][0], colours[i][1], colours[i][2]);
							} else {
								te.addColour(colours[i][0], colours[i][1], colours[i][2]);
							}
							world.markBlockForUpdate(pos);
							matched = true;
							toggleLitState = false;
							flag = true;
							break;
						}
					}
					if (matched) {
						break;
					}
				}
			}
		}

		if (toggleLitState) {
			meta = this.toggleLitStatus(meta);
			world.setBlockState(pos, getStateFromMeta(meta), Defaults.FLAG_BLOCK_SYNCH | Defaults.FLAG_BLOCK_UPDATE);
			flag = true;
		}
		return flag;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		int meta = getMetaFromState(state);
		if (!world.isRemote) {
			TileCandle tc = (TileCandle) world.getTileEntity(pos);
			int newMeta = isLit(meta) ? 1 : 0;
			ItemStack stack = new ItemStack(this, 1, newMeta);
			if (tc != null && tc.getColour() != 0xffffff) {
				// When dropped, tag new item stack with colour. Unless it's white, then do no such thing for maximum stacking.
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger(colourTagName, tc.getColour());
				stack.setTagCompound(tag);
			}
			this.dropBlockAsItem(world, pos, state, 0);
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
		TileCandle tc = (TileCandle) (world.getTileEntity(pos));
		tc.setColour(this.getColourValueFromItemStack(itemStack));
		if (isLit(itemStack)) {
			int meta = getMetaFromState(state);
			world.setBlockState(pos, getStateFromMeta(meta), Defaults.FLAG_BLOCK_SYNCH | Defaults.FLAG_BLOCK_UPDATE);
		}
	}
	
	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		// Does nothing to prevent extra candles from falling.
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		// Does nothing to prevent extra candles from falling.
	}
	
	@Override
	protected boolean checkForDrop(World world, BlockPos pos, IBlockState state) {
		if (!this.canPlaceBlockAt(world, pos)) {
			if (world.getBlockState(pos).getBlock() == this) {
				world.setBlockToAir(pos);
			}
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (isLit(getMetaFromState(state))) {
			int l = getMetaFromState(state) & 0x07;
			double d0 = pos.getX() + 0.5F;
			double d1 = pos.getY() + 0.7F;
			double d2 = pos.getZ() + 0.5F;
			double d3 = 0.2199999988079071D;
			double d4 = 0.27000001072883606D;

			if (l == 1) {
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
				world.spawnParticle(EnumParticleTypes.FLAME, d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			} else if (l == 2) {
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
				world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			} else if (l == 3) {
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
				world.spawnParticle(EnumParticleTypes.FLAME, d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
			} else if (l == 4) {
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
				world.spawnParticle(EnumParticleTypes.FLAME, d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
			} else {
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				world.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected boolean onNeighborChangeInternal(World world, BlockPos pos, IBlockState state) {
		if (checkForDrop(world, pos, state)) {
			int i1 = getMetaFromState(state) & 0x07;
			boolean flag = false;

			if (!world.isSideSolid(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()), EnumFacing.EAST, true) && i1 == 1) {
				flag = true;
			}

			if (!world.isSideSolid(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()), EnumFacing.WEST, true) && i1 == 2) {
				flag = true;
			}

			if (!world.isSideSolid(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1), EnumFacing.SOUTH, true) && i1 == 3) {
				flag = true;
			}

			if (!world.isSideSolid(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1), EnumFacing.NORTH, true) && i1 == 4) {
				flag = true;
			}

			if (!this.canPlaceTorchOn(world, new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())) && i1 == 5) {
				flag = true;
			}

			if (flag) {
				this.dropBlockAsItem(world, pos, state, 0);
				world.setBlockToAir(pos);
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	// Yes, function hiding. Go away.
	public boolean canPlaceTorchOn(World par1World, BlockPos pos) {
		if (World.doesBlockHaveSolidTopSurface(par1World, pos)) {
			return true;
		} else {
			Block block = par1World.getBlockState(pos).getBlock();
			return block.canPlaceTorchOnTop(par1World, pos);
		}
	}

	protected int getColourValueFromItemStack(ItemStack itemStack) {
		int value = 0xffffff; // default to white.
		if (itemStack.hasTagCompound()) {
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag.hasKey(colourTagName)) {
				value = tag.getInteger(colourTagName);
			}
		}
		return value;
	}

	public static boolean isLit(int meta) {
		return (meta & 0x08) > 0;
	}

	public static boolean isLit(ItemStack itemStack) {
		return itemStack.getItemDamage() > 0;
	}

	protected int toggleLitStatus(int meta) {
		return meta ^ 0x08;
	}

	public void addItemToLightingList(Item item) {
		if (item == null) {
			throw new NullPointerException();
		}

		if (!this.lightingItems.contains(item)) {
			this.lightingItems.add(item);
		}
	}
	
	@Override
	public int getRenderColor(IBlockState state) {
		return super.getRenderColor(state);
	}

	@Override
	public String[] getVariants() {
		return new String[]{"on", "off"};
	}

	@Override
	public ModelType getModelType() {
		return ModelType.META;
	}

}
