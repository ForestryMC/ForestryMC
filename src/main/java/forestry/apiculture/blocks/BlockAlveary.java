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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import forestry.apiculture.MaterialBeehive;
import forestry.apiculture.multiblock.IAlvearyControllerInternal;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.apiculture.multiblock.TileAlvearyFan;
import forestry.apiculture.multiblock.TileAlvearyHeater;
import forestry.apiculture.multiblock.TileAlvearyHygroregulator;
import forestry.apiculture.multiblock.TileAlvearyPlain;
import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.apiculture.multiblock.TileAlvearyStabiliser;
import forestry.apiculture.multiblock.TileAlvearySwarmer;
import forestry.apiculture.network.packets.PacketAlvearyChange;
import forestry.core.blocks.BlockStructure;
import forestry.core.tiles.IActivatable;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemTooltipUtil;
import forestry.core.utils.NetworkUtil;

public class BlockAlveary extends BlockStructure {
	private static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);
	private static final EnumProperty<AlvearyPlainType> PLAIN_TYPE = EnumProperty.create("type", AlvearyPlainType.class);

	private enum State implements StringRepresentable {
		ON, OFF;

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

	private enum AlvearyPlainType implements StringRepresentable {
		NORMAL, ENTRANCE, ENTRANCE_LEFT, ENTRANCE_RIGHT;

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

	private final BlockAlvearyType type;

	public BlockAlveary(BlockAlvearyType type) {
		super(Block.Properties.of(MaterialBeehive.BEEHIVE_ALVEARY)
				.strength(1f)
				.sound(SoundType.WOOD)
				.harvestTool(ToolType.AXE)
				.harvestLevel(0)
		);
		this.type = type;
		BlockState defaultState = this.getStateDefinition().any();
		if (type == BlockAlvearyType.PLAIN) {
			defaultState = defaultState.setValue(PLAIN_TYPE, AlvearyPlainType.NORMAL);
		} else if (type.activatable) {
			defaultState = defaultState.setValue(STATE, State.OFF);
		}
		registerDefaultState(defaultState);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(PLAIN_TYPE, STATE);
	}

	public BlockAlvearyType getType() {
		return type;
	}

	/*@Override
	public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
		return true;
	}*/

	@Nullable
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		switch (type) {
			case SWARMER:
				return new TileAlvearySwarmer();
			case FAN:
				return new TileAlvearyFan();
			case HEATER:
				return new TileAlvearyHeater();
			case HYGRO:
				return new TileAlvearyHygroregulator();
			case STABILISER:
				return new TileAlvearyStabiliser();
			case SIEVE:
				return new TileAlvearySieve();
			case PLAIN:
			default:
				return new TileAlvearyPlain();
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	public BlockState getNewState(TileAlveary tile) {
		BlockState state = this.defaultBlockState();
		Level world = tile.getLevel();
		BlockPos pos = tile.getBlockPos();

		if (tile instanceof IActivatable) {
			if (((IActivatable) tile).isActive()) {
				state = state.setValue(STATE, State.ON);
			} else {
				state = state.setValue(STATE, State.OFF);
			}
		} else if (getType() == BlockAlvearyType.PLAIN) {
			if (!tile.getMultiblockLogic().getController().isAssembled()) {
				state = state.setValue(PLAIN_TYPE, AlvearyPlainType.NORMAL);
			} else {
				BlockState blockStateAbove = world.getBlockState(pos.above());
				if (blockStateAbove.is(BlockTags.WOODEN_SLABS)) {
					List<Direction> blocksTouching = getBlocksTouching(world, pos);
					switch (blocksTouching.size()) {
						case 3:
							state = state.setValue(PLAIN_TYPE, AlvearyPlainType.ENTRANCE);
							break;
						case 2:
							if (blocksTouching.contains(Direction.SOUTH) && blocksTouching.contains(Direction.EAST) ||
									blocksTouching.contains(Direction.NORTH) && blocksTouching.contains(Direction.WEST)) {
								state = state.setValue(PLAIN_TYPE, AlvearyPlainType.ENTRANCE_LEFT);
							} else {
								state = state.setValue(PLAIN_TYPE, AlvearyPlainType.ENTRANCE_RIGHT);
							}
							break;
						default:
							state = state.setValue(PLAIN_TYPE, AlvearyPlainType.NORMAL);
							break;
					}
				} else {
					state = state.setValue(PLAIN_TYPE, AlvearyPlainType.NORMAL);
				}
			}
		}
		return state;
	}

	private static List<Direction> getBlocksTouching(BlockGetter world, BlockPos blockPos) {
		List<Direction> touching = new ArrayList<>();
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockState blockState = world.getBlockState(blockPos.relative(direction));
			if (blockState.getBlock() instanceof BlockAlveary) {
				touching.add(direction);
			}
		}
		return touching;
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
		TileUtil.actOnTile(worldIn, pos, TileAlveary.class, tileAlveary -> {
			// We must check that the slabs on top were not removed
			IAlvearyControllerInternal alveary = tileAlveary.getMultiblockLogic().getController();
			alveary.reassemble();
			BlockPos referenceCoord = alveary.getReferenceCoord();
			NetworkUtil.sendNetworkPacket(new PacketAlvearyChange(referenceCoord), referenceCoord, worldIn);
		});
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
		if (Screen.hasShiftDown()) {
			tooltip.add(new TranslatableComponent("block.forestry.alveary_tooltip"));
		} else {
			ItemTooltipUtil.addShiftInformation(stack, world, tooltip, flag);
		}
	}
}
