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
package forestry.core.blocks;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidUtil;

import forestry.api.core.ISpriteRegister;
import forestry.api.core.ISpriteRegistry;
import forestry.core.circuits.ISocketable;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.render.MachineParticleCallback;
import forestry.core.render.ParticleHelper;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.InventoryUtil;

public class BlockBase<P extends Enum<P> & IBlockType> extends BlockForestry implements ISpriteRegister {
	/**
	 * use this instead of {@link HorizontalBlock#FACING} so the blocks rotate in a circle instead of NSWE order.
	 */
	public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN, Direction.UP);

	private final boolean hasTESR;
	public final P blockType;

	private final ParticleHelper.Callback particleCallback;

	private static Block.Properties createProperties(IBlockType type, Block.Properties properties) {
		if (type instanceof IBlockTypeTesr || type instanceof IBlockTypeCustom) {
			properties = properties.noOcclusion();
		}
		return properties.strength(2.0f);
	}

	public BlockBase(P blockType, Block.Properties properties) {
		super(createProperties(blockType, properties));
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));

		this.blockType = blockType;
		blockType.getMachineProperties().setBlock(this);

		this.hasTESR = blockType instanceof IBlockTypeTesr;

		particleCallback = new MachineParticleCallback<>(this, blockType);
	}

	public BlockBase(P blockType, Material material) {
		this(blockType, Block.Properties.of(material));
	}

	public BlockBase(P blockType) {
		this(blockType, Material.METAL);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState p_220080_1_, BlockGetter p_220080_2_, BlockPos p_220080_3_) {
		return 0.2F;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		if (hasTESR) {
			return RenderShape.ENTITYBLOCK_ANIMATED;
		} else {
			return RenderShape.MODEL;
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return getDefinition().createTileEntity();
	}


	private IMachineProperties<?> getDefinition() {
		return blockType.getMachineProperties();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
		IMachineProperties<?> definition = getDefinition();
		return definition.getShape(state, reader, pos, context);
	}

	/* INTERACTION */
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit) {
		TileBase tile = TileUtil.getTile(worldIn, pos, TileBase.class);
		if (tile == null) {
			return InteractionResult.FAIL;
		}
		if (TileUtil.isUsableByPlayer(playerIn, tile)) {

			if (!playerIn.isShiftKeyDown()) { //isSneaking
				if (FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, hit.getDirection())) {
					return InteractionResult.SUCCESS;
				}
			}

			if (!worldIn.isClientSide) {
				ServerPlayer sPlayer = (ServerPlayer) playerIn;
				tile.openGui(sPlayer, pos);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
		super.playerWillDestroy(world, pos, state, player);
		if (world.isClientSide) {
			return;
		}

		BlockEntity tile = TileUtil.getTile(world, pos);
		if (tile instanceof Container) {
			Container inventory = (Container) tile;
			InventoryUtil.dropInventory(inventory, world, pos);
		}
		if (tile instanceof TileForestry) {
			((TileForestry) tile).onRemoval();
		}
		if (tile instanceof ISocketable) {
			InventoryUtil.dropSockets((ISocketable) tile, tile.getLevel(), tile.getBlockPos());
		}
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (world.isClientSide) {
			return;
		}

		if (placer instanceof Player) {
			TileUtil.actOnTile(world, pos, IOwnedTile.class, tile -> {
				IOwnerHandler ownerHandler = tile.getOwnerHandler();
				Player player = (Player) placer;
				GameProfile gameProfile = player.getGameProfile();
				ownerHandler.setOwner(gameProfile);
			});
		}
	}

	public void clientSetup() {
		blockType.getMachineProperties().clientSetup();
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		Direction facing = state.getValue(FACING);
		return state.setValue(FACING, rot.rotate(facing));
	}

	/* Particles - Client Only */
	/*@Override
	public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager effectRenderer) {
		if (blockType.getMachineProperties() instanceof IMachinePropertiesTesr) {
			if (target.getType() == RayTraceResult.Type.BLOCK) {
				BlockRayTraceResult result = (BlockRayTraceResult) target;
				return ParticleHelper.addBlockHitEffects(world, result.getBlockPos(), result.getDirection(), effectRenderer, particleCallback);
			}
		}
		return false;
	}*/

	@Override
	public void registerSprites(ISpriteRegistry registry) {
		IMachineProperties<?> machineProperties = blockType.getMachineProperties();
		if (machineProperties instanceof IMachinePropertiesTesr) {
			registry.addSprite(((IMachinePropertiesTesr) machineProperties).getParticleTexture());
		}
	}
}
