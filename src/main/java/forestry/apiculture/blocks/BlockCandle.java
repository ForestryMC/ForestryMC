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
package forestry.apiculture.blocks;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.Tabs;
import forestry.apiculture.tiles.TileCandle;
import forestry.core.blocks.IColoredBlock;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;

public class BlockCandle extends BlockTorch implements IItemModelRegister, ITileEntityProvider, IColoredBlock {

	private static final ImmutableMap<String, Integer> colours;
	public static final Set<Item> lightingItems;
	public static final String colourTagName = "colour";

	public static final PropertyEnum<State> STATE = PropertyEnum.create("state", State.class);

	enum State implements IStringSerializable {
		ON("on"), OFF("off");

		private final String name;

		State(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}

	static {
		colours = ImmutableMap.<String, Integer>builder()
			.put("dyeWhite", new Color(255, 255, 255).getRGB())
			.put("dyeOrange", new Color(219, 125, 62).getRGB())
			.put("dyeMagenta", new Color(255, 20, 255).getRGB())
			.put("dyeLightBlue", new Color(107, 138, 201).getRGB())
			.put("dyeYellow", new Color(255, 255, 20).getRGB())
			.put("dyeLime", new Color(20, 255, 20).getRGB())
			.put("dyePink", new Color(208, 132, 153).getRGB())
			.put("dyeGray", new Color(74, 74, 74).getRGB())
			.put("dyeLightGray", new Color(154, 161, 161).getRGB())
			.put("dyeCyan", new Color(20, 255, 255).getRGB())
			.put("dyePurple", new Color(126, 61, 181).getRGB())
			.put("dyeBlue", new Color(20, 20, 255).getRGB())
			.put("dyeBrown", new Color(79, 50, 31).getRGB())
			.put("dyeGreen", new Color(53, 70, 27).getRGB())
			.put("dyeRed", new Color(150, 52, 48).getRGB())
			.put("dyeBlack", new Color(20, 20, 20).getRGB())
			.build();

		lightingItems = new HashSet<>(Arrays.asList(
			Items.FLINT_AND_STEEL,
			Items.FLINT,
			Item.getItemFromBlock(Blocks.TORCH)
		));
	}

	public BlockCandle() {
		this.setHardness(0.0F);
		this.setSoundType(SoundType.WOOD);
		setCreativeTab(Tabs.tabApiculture);
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(STATE, State.OFF));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, STATE);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileCandle tileCandle = TileUtil.getTile(world, pos, TileCandle.class);
		if (tileCandle != null && tileCandle.isLit()) {
			state = state.withProperty(STATE, State.ON);
		}
		return super.getActualState(state, world, pos);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCandle();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "candle");
		manager.registerItemModel(item, 1, "candle");
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileCandle candle = TileUtil.getTile(world, pos, TileCandle.class);
		if (candle != null && candle.isLit()) {
			return 14;
		}
		return 0;
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, 0));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileCandle tileCandle = TileUtil.getTile(worldIn, pos, TileCandle.class);
		if (tileCandle == null) {
			return false;
		}
		final boolean isLit = tileCandle.isLit();

		boolean flag = false;
		boolean toggleLitState = true;

		ItemStack heldItem = playerIn.getHeldItem(hand);

		if (!isLit) {
			if (heldItem.isEmpty() || !lightingItems.contains(heldItem.getItem())) {
				toggleLitState = false;
			} else if (ItemStackUtil.equals(this, heldItem) && isLit(heldItem)) {
				toggleLitState = true;
			}
		}

		if (!heldItem.isEmpty()) {
			if (ItemStackUtil.equals(this, heldItem)) {
				if (!isLit(heldItem)) {
					// Copy the colour of an unlit, coloured candle.
					if (heldItem.getTagCompound() != null && heldItem.getTagCompound().hasKey(colourTagName)) {
						tileCandle.setColour(heldItem.getTagCompound().getInteger(colourTagName));
					} else {
						// Reset to white if item has no
						tileCandle.setColour(0xffffff);
					}
				} else {
					toggleLitState = true;
				}
				flag = true;
			} else {
				boolean dyed = tryDye(heldItem, isLit, tileCandle);
				if (dyed) {
					worldIn.markBlockRangeForRenderUpdate(pos, pos);
					toggleLitState = false;
					flag = true;
				}
			}
		}

		if (toggleLitState) {
			tileCandle.setLit(!isLit);
			worldIn.markBlockRangeForRenderUpdate(pos, pos);
			worldIn.profiler.startSection("checkLight");
			worldIn.checkLight(pos);
			worldIn.profiler.endSection();
			flag = true;
		}
		return flag;
	}

	private static boolean tryDye(ItemStack held, boolean isLit, TileCandle tileCandle) {
		// Check for dye-able.
		for (Map.Entry<String, Integer> colour : colours.entrySet()) {
			String colourName = colour.getKey();
			for (ItemStack stack : OreDictionary.getOres(colourName)) {
				if (OreDictionary.itemMatches(stack, held, true)) {
					if (isLit) {
						tileCandle.setColour(colour.getValue());
					} else {
						tileCandle.addColour(colour.getValue());
					}
					return true;
				}
			}
		}
		return false;
	}

	/* DROP HANDLING */
	// Hack: 	When harvesting we need to get the drops in onBlockHarvested,
	// 			because Mojang destroys the block and tile before calling getDrops.
	private final ThreadLocal<ItemStack> drop = new ThreadLocal<>();

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (!world.isRemote) {
			ItemStack itemStack = getCandleDrop(world, pos);
			drop.set(itemStack);
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack dropStack = drop.get();
		drop.remove();

		// not harvested, get drops normally
		if (dropStack == null) {
			dropStack = getCandleDrop(world, pos);
		}

		drops.add(dropStack);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return getCandleDrop(world, pos);
	}

	private ItemStack getCandleDrop(IBlockAccess world, BlockPos pos) {
		TileCandle tileCandle = TileUtil.getTile(world, pos, TileCandle.class);
		if (tileCandle == null) {
			return new ItemStack(this);
		}
		int colour = tileCandle.getColour();

		int newMeta = tileCandle.isLit() ? 1 : 0;
		ItemStack itemStack = new ItemStack(this, 1, newMeta);
		if (colour != 0xffffff) {
			// When dropped, tag new item stack with colour. Unless it's white, then do no such thing for maximum stacking.
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger(colourTagName, colour);
			itemStack.setTagCompound(tag);
		}
		return itemStack;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileCandle tileCandle = TileUtil.getTile(world, pos, TileCandle.class);
		if (tileCandle != null) {
			int colour = getColourValueFromItemStack(stack);
			boolean isLit = isLit(stack);
			tileCandle.setColour(colour);
			tileCandle.setLit(isLit);
			if (tileCandle.isLit()) {
				world.profiler.startSection("checkLight");
				world.checkLight(pos);
				world.profiler.endSection();
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		TileCandle tileCandle = TileUtil.getTile(worldIn, pos, TileCandle.class);
		if (tileCandle != null && tileCandle.isLit()) {
			super.randomDisplayTick(stateIn, worldIn, pos, rand);
		}
	}

	private static int getColourValueFromItemStack(ItemStack itemStack) {
		int value = 0xffffff; // default to white.
		if (itemStack.getTagCompound() != null) {
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag.hasKey(colourTagName)) {
				value = tag.getInteger(colourTagName);
			}
		}
		return value;
	}

	public static boolean isLit(ItemStack itemStack) {
		return itemStack.getItemDamage() > 0;
	}

	public static void addItemToLightingList(Item item) {
		lightingItems.add(item);
	}

	public ItemStack getUnlitCandle(int amount) {
		return new ItemStack(this, amount, 0);
	}

	public ItemStack getLitCandle(int amount) {
		return new ItemStack(this, amount, 1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
		if (worldIn != null && pos != null) {
			TileCandle tileCandle = TileUtil.getTile(worldIn, pos, TileCandle.class);
			if (tileCandle != null) {
				return tileCandle.getColour();
			}
		}
		return 0xffffff;
	}
}
