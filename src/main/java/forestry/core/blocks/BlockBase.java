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

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

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
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
		return 0.2F;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		if (hasTESR) {
			return BlockRenderType.ENTITYBLOCK_ANIMATED;
		} else {
			return BlockRenderType.MODEL;
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return getDefinition().createTileEntity();
	}


	private IMachineProperties<?> getDefinition() {
		return blockType.getMachineProperties();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		IMachineProperties<?> definition = getDefinition();
		return definition.getShape(state, reader, pos, context);
	}

	/* INTERACTION */
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit) {
		TileBase tile = TileUtil.getTile(worldIn, pos, TileBase.class);
		if (tile == null) {
			return ActionResultType.FAIL;
		}
		if (TileUtil.isUsableByPlayer(playerIn, tile)) {

			if (!playerIn.isShiftKeyDown()) { //isSneaking
				if (FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, hit.getDirection())) {
					return ActionResultType.SUCCESS;
				}
			}

			if (!worldIn.isClientSide) {
				ServerPlayerEntity sPlayer = (ServerPlayerEntity) playerIn;
				tile.openGui(sPlayer, pos);
			}
		}
		return ActionResultType.SUCCESS;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.playerWillDestroy(world, pos, state, player);
		if (world.isClientSide) {
			return;
		}

		TileEntity tile = TileUtil.getTile(world, pos);
		if (tile instanceof IInventory) {
			IInventory inventory = (IInventory) tile;
			InventoryHelper.dropContents(world, pos, inventory);
		}
		if (tile instanceof TileForestry) {
			((TileForestry) tile).onRemoval();
		}
		if (tile instanceof ISocketable) {
			InventoryUtil.dropSockets((ISocketable) tile, tile.getLevel(), tile.getBlockPos());
		}
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (world.isClientSide) {
			return;
		}

		if (placer instanceof PlayerEntity) {
			TileUtil.actOnTile(world, pos, IOwnedTile.class, tile -> {
				IOwnerHandler ownerHandler = tile.getOwnerHandler();
				PlayerEntity player = (PlayerEntity) placer;
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
