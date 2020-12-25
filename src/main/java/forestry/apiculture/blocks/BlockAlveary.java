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

import forestry.apiculture.MaterialBeehive;
import forestry.apiculture.multiblock.*;
import forestry.apiculture.network.packets.PacketAlvearyChange;
import forestry.core.blocks.BlockStructure;
import forestry.core.tiles.IActivatable;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemTooltipUtil;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BlockAlveary extends BlockStructure {
    private static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);
    private static final EnumProperty<AlvearyPlainType> PLAIN_TYPE = EnumProperty.create(
            "type",
            AlvearyPlainType.class
    );

    private enum State implements IStringSerializable {
        ON, OFF;

        @Override
        public String getString() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

    private enum AlvearyPlainType implements IStringSerializable {
        NORMAL, ENTRANCE, ENTRANCE_LEFT, ENTRANCE_RIGHT;

        @Override
        public String getString() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

    private final BlockAlvearyType type;

    public BlockAlveary(BlockAlvearyType type) {
        super(Block.Properties.create(MaterialBeehive.BEEHIVE_ALVEARY)
                              .hardnessAndResistance(1f)
                              .sound(SoundType.WOOD)
                              .harvestTool(ToolType.AXE)
                              .harvestLevel(0)
        );
        this.type = type;
        BlockState defaultState = this.getStateContainer().getBaseState();
        if (type == BlockAlvearyType.PLAIN) {
            defaultState = defaultState.with(PLAIN_TYPE, AlvearyPlainType.NORMAL);
        } else if (type.activatable) {
            defaultState = defaultState.with(STATE, State.OFF);
        }
        setDefaultState(defaultState);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
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
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
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

    @Override
    public BlockState getStateAtViewpoint(
            BlockState state, IBlockReader world, BlockPos pos, Vector3d viewpoint
    ) {
        return null;
    }

    @Override
    public BlockState updatePostPlacement(
            BlockState state,
            Direction facing,
            BlockState facingState,
            IWorld world,
            BlockPos pos,
            BlockPos facingPos
    ) {
        TileAlveary tile = TileUtil.getTile(world, pos, TileAlveary.class);
        if (tile == null) {
            return super.updatePostPlacement(state, facing, facingState, world, pos, facingPos);
        }

        if (tile instanceof IActivatable) {
            if (((IActivatable) tile).isActive()) {
                state = state.with(STATE, State.ON);
            } else {
                state = state.with(STATE, State.OFF);
            }
        } else if (getType() == BlockAlvearyType.PLAIN) {
            if (!tile.getMultiblockLogic().getController().isAssembled()) {
                state = state.with(PLAIN_TYPE, AlvearyPlainType.NORMAL);
            } else {
                BlockState blockStateAbove = world.getBlockState(pos.up());
                Block blockAbove = blockStateAbove.getBlock();
                if (blockAbove.isIn(BlockTags.WOODEN_SLABS)) {
                    List<Direction> blocksTouching = getBlocksTouching(world, pos);
                    switch (blocksTouching.size()) {
                        case 3:
                            state = state.with(PLAIN_TYPE, AlvearyPlainType.ENTRANCE);
                            break;
                        case 2:
                            if (blocksTouching.contains(Direction.SOUTH) && blocksTouching.contains(Direction.EAST) ||
                                blocksTouching.contains(Direction.NORTH) && blocksTouching.contains(Direction.WEST)
                            ) {
                                state = state.with(PLAIN_TYPE, AlvearyPlainType.ENTRANCE_LEFT);
                            } else {
                                state = state.with(PLAIN_TYPE, AlvearyPlainType.ENTRANCE_RIGHT);
                            }
                            break;
                        default:
                            state = state.with(PLAIN_TYPE, AlvearyPlainType.NORMAL);
                            break;
                    }
                } else {
                    state = state.with(PLAIN_TYPE, AlvearyPlainType.NORMAL);
                }
            }
        }

        RenderUtil.markForUpdate(pos);
        return super.updatePostPlacement(state, facing, facingState, world, pos, facingPos);
    }

    private static List<Direction> getBlocksTouching(IBlockReader world, BlockPos blockPos) {
        List<Direction> touching = new ArrayList<>();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState blockState = world.getBlockState(blockPos.offset(direction));
            if (blockState.getBlock() instanceof BlockAlveary) {
                touching.add(direction);
            }
        }
        return touching;
    }

    @Override
    public void neighborChanged(
            BlockState state,
            World worldIn,
            BlockPos pos,
            Block blockIn,
            BlockPos fromPos,
            boolean isMoving
    ) {
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
    public void addInformation(
            ItemStack stack,
            @Nullable IBlockReader world,
            List<ITextComponent> tooltip,
            ITooltipFlag flag
    ) {
        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent("block.forestry.alveary_tooltip"));
        } else {
            ItemTooltipUtil.addShiftInformation(stack, world, tooltip, flag);
        }
    }
}
