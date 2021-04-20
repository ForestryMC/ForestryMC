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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.apiculture.tiles.TileCandle;
import forestry.core.blocks.IColoredBlock;
import forestry.core.config.Constants;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.RenderUtil;

public class BlockCandle extends TorchBlock implements IColoredBlock, ITileEntityProvider {

	private static final ImmutableMap<DyeColor, Integer> colours;
	public static final Set<Item> lightingItems;
	public static final String COLOUR_TAG_NAME = "colour";

	public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);

	enum State implements IStringSerializable {
		ON("on"), OFF("off");

		private final String name;

		State(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}

	static {
		colours = ImmutableMap.<DyeColor, Integer>builder()
				.put(DyeColor.WHITE, new Color(255, 255, 255).getRGB())
				.put(DyeColor.ORANGE, new Color(219, 125, 62).getRGB())
				.put(DyeColor.MAGENTA, new Color(255, 20, 255).getRGB())
				.put(DyeColor.LIGHT_BLUE, new Color(107, 138, 201).getRGB())
				.put(DyeColor.YELLOW, new Color(255, 255, 20).getRGB())
				.put(DyeColor.LIME, new Color(20, 255, 20).getRGB())
				.put(DyeColor.PINK, new Color(208, 132, 153).getRGB())
				.put(DyeColor.GRAY, new Color(74, 74, 74).getRGB())
				.put(DyeColor.LIGHT_GRAY, new Color(154, 161, 161).getRGB())
				.put(DyeColor.CYAN, new Color(20, 255, 255).getRGB())
				.put(DyeColor.PURPLE, new Color(126, 61, 181).getRGB())
				.put(DyeColor.BLUE, new Color(20, 20, 255).getRGB())
				.put(DyeColor.BROWN, new Color(79, 50, 31).getRGB())
				.put(DyeColor.GREEN, new Color(53, 70, 27).getRGB())
				.put(DyeColor.RED, new Color(150, 52, 48).getRGB())
				.put(DyeColor.BLACK, new Color(20, 20, 20).getRGB())
				.build();

		lightingItems = new HashSet<>(Arrays.asList(
				Items.FLINT_AND_STEEL,
				Items.FLINT,
				Item.byBlock(Blocks.TORCH)
		));
	}

	public BlockCandle() {
		super(Block.Properties.of(Material.DECORATION)
						.strength(0.0f)
						.sound(SoundType.WOOD)
						.noCollission()
						.instabreak(),
				ParticleTypes.FLAME
		);
		registerDefaultState(this.getStateDefinition().any().setValue(STATE, State.OFF));
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return new TileCandle();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(STATE);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState blockState, IWorld world, BlockPos pos, BlockPos blockPos) {
		TileCandle tileCandle = TileUtil.getTile(world, pos, TileCandle.class);
		return state.setValue(STATE, tileCandle != null && tileCandle.isLit() ? State.ON : State.OFF);
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		TileCandle candle = TileUtil.getTile(world, pos, TileCandle.class);
		if (candle != null && candle.isLit()) {
			return 14;
		}
		return 0;
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult rayTraceResult) {
		TileCandle tileCandle = TileUtil.getTile(worldIn, pos, TileCandle.class);
		if (tileCandle == null) {
			return ActionResultType.FAIL;
		}
		final boolean isLit = tileCandle.isLit();

		ActionResultType flag = ActionResultType.PASS;
		boolean toggleLitState = true;

		ItemStack heldItem = playerIn.getItemInHand(hand);

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
					if (heldItem.getTag() != null && heldItem.getTag().contains(COLOUR_TAG_NAME)) {
						tileCandle.setColour(heldItem.getTag().getInt(COLOUR_TAG_NAME));
					} else {
						// Reset to white if item has no
						tileCandle.setColour(0xffffff);
					}
					RenderUtil.markForUpdate(pos);
				} else {
					toggleLitState = true;
				}
				flag = ActionResultType.SUCCESS;
			} else {
				boolean dyed = tryDye(heldItem, isLit, tileCandle);
				if (dyed) {
					RenderUtil.markForUpdate(pos);
					toggleLitState = false;
					flag = ActionResultType.SUCCESS;
				}
			}
		}

		if (toggleLitState) {
			tileCandle.setLit(!isLit);
			worldIn.setBlock(pos, state.setValue(STATE, tileCandle.isLit() ? State.ON : State.OFF), Constants.FLAG_BLOCK_UPDATE);
			RenderUtil.markForUpdate(pos);

			worldIn.getProfiler().push("checkLight");
			worldIn.getChunkSource().getLightEngine().checkBlock(pos);
			worldIn.getProfiler().pop();

			flag = ActionResultType.SUCCESS;
			worldIn.playSound(playerIn, pos, !isLit ? heldItem.getItem() == Items.FLINT_AND_STEEL ? SoundEvents.FLINTANDSTEEL_USE : SoundEvents.FIRE_AMBIENT : SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.75F, worldIn.random.nextFloat() * 0.4F + 0.8F);
		}
		return flag;
	}

	private static boolean tryDye(ItemStack held, boolean isLit, TileCandle tileCandle) {
		// Check for dye-able.
		for (Map.Entry<DyeColor, Integer> colour : colours.entrySet()) {
			for (Item item : colour.getKey().getTag().getValues()) {
				if (held.getItem() == item) {
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

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		List<ItemStack> drops = new ArrayList<>();
		drops.add(getCandleDrop(builder.getParameter(LootParameters.BLOCK_ENTITY)));
		return drops;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return getCandleDrop(world, pos);
	}

	private ItemStack getCandleDrop(IBlockReader world, BlockPos pos) {
		return getCandleDrop(world.getBlockEntity(pos));
	}

	private ItemStack getCandleDrop(@Nullable TileEntity tileEntity) {
		if (!(tileEntity instanceof TileCandle)) {
			return new ItemStack(this);
		}
		TileCandle tileCandle = (TileCandle) tileEntity;
		int colour = tileCandle.getColour();

		//int newMeta = tileCandle.isLit() ? 1 : 0;// todo: meta ?
		ItemStack itemStack = new ItemStack(this);
		if (colour != 0xffffff) {
			// When dropped, tag new item stack with colour. Unless it's white, then do no such thing for maximum stacking.
			CompoundNBT tag = new CompoundNBT();
			tag.putInt(COLOUR_TAG_NAME, colour);
			itemStack.setTag(tag);
		}
		return itemStack;
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		TileCandle tileCandle = TileUtil.getTile(world, pos, TileCandle.class);
		if (tileCandle != null) {
			int colour = getColourValueFromItemStack(stack);
			boolean isLit = isLit(stack);
			tileCandle.setColour(colour);
			tileCandle.setLit(isLit);
			if (tileCandle.isLit()) {
				world.getProfiler().push("checkLight");
				world.getChunkSource().getLightEngine().checkBlock(pos);
				world.getProfiler().pop();
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		TileCandle tileCandle = TileUtil.getTile(worldIn, pos, TileCandle.class);
		if (tileCandle != null && tileCandle.isLit()) {
			super.animateTick(stateIn, worldIn, pos, rand);
		}
	}

	private static int getColourValueFromItemStack(ItemStack itemStack) {
		int value = 0xffffff; // default to white.
		if (itemStack.getTag() != null) {
			CompoundNBT tag = itemStack.getTag();
			if (tag.contains(COLOUR_TAG_NAME)) {
				value = tag.getInt(COLOUR_TAG_NAME);
			}
		}
		return value;
	}

	public static boolean isLit(ItemStack itemStack) {
		return false;//TODO properties or something itemStack.getItemDamage() > 0;
	}

	public static void addItemToLightingList(Item item) {
		lightingItems.add(item);
	}

	public ItemStack getUnlitCandle(int amount) {
		return new ItemStack(this, amount);//TODO flatten , 0);
	}

	public ItemStack getLitCandle(int amount) {
		return new ItemStack(this, amount);// TODO flatten , 1);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public int colorMultiplier(BlockState state, @Nullable IBlockReader worldIn, @Nullable BlockPos pos, int tintIndex) {
		if (worldIn != null && pos != null) {
			TileCandle tileCandle = TileUtil.getTile(worldIn, pos, TileCandle.class);
			if (tileCandle != null) {
				return tileCandle.getColour();
			}
		}
		return 0xffffff;
	}
}
