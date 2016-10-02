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

import javax.annotation.Nonnull;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.ITextureManager;
import forestry.core.circuits.ISocketable;
import forestry.core.proxy.Proxies;
import forestry.core.render.MachineParticleCallback;
import forestry.core.render.MachineStateMapper;
import forestry.core.render.ParticleHelper;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.InventoryUtil;

public class BlockBase<P extends Enum<P> & IBlockType & IStringSerializable> extends BlockForestry implements IItemModelRegister, ISpriteRegister, IStateMapperRegister, IBlockRotatable {
	/** use this instead of {@link BlockHorizontal#FACING} so the blocks rotate in a circle instead of NSWE order. */
	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.DOWN, EnumFacing.UP);

	private final boolean hasTESR;
	private final boolean hasCustom;
	@Nonnull
	public final P blockType;

	/* PROPERTIES */

	private final ParticleHelper.Callback particleCallback;
	
	protected final BlockStateContainer blockState;

	public BlockBase(@Nonnull P blockType, Material material) {
		super(material);

		this.blockType = blockType;
		blockType.getMachineProperties().setBlock(this);

		this.hasTESR = blockType instanceof IBlockTypeTesr;
		this.hasCustom = blockType instanceof IBlockTypeCustom;
		this.lightOpacity = (!hasTESR && !hasCustom) ? 255 : 0;

		this.blockState = this.createBlockState();
		IBlockState state = this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH);
		this.setDefaultState(state);
		
		particleCallback = new MachineParticleCallback<>(this, blockType);
	}
	
	public BlockBase(@Nonnull P blockType) {
		this(blockType, Material.IRON);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return !hasTESR && !hasCustom;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return !hasTESR && !hasCustom;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		if (hasTESR) {
			return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
		} else {
			return EnumBlockRenderType.MODEL;
		}
	}

	@Nonnull
	private IMachineProperties getDefinition() {
		return blockType.getMachineProperties();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		IMachineProperties definition = getDefinition();
		return definition.getBoundingBox(pos, blockState);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		IMachineProperties definition = getDefinition();
		return definition.getBoundingBox(pos, state).offset(pos);
	}

	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
		IMachineProperties definition = getDefinition();
		return definition.collisionRayTrace(worldIn, pos, start, end);
	}

	/* TILE ENTITY CREATION */
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return getDefinition().createTileEntity();
	}

	/* INTERACTION */
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileBase tile = TileUtil.getTile(worldIn, pos, TileBase.class);
		if (tile != null) {
			if (TileUtil.isUsableByPlayer(playerIn, tile)) {

				if (!playerIn.isSneaking()) {
					if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) {
						IFluidHandler tileFluidHandler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
						if (FluidUtil.interactWithFluidHandler(heldItem, tileFluidHandler, playerIn)) {
							return true;
						}
					}
				}

				if (!worldIn.isRemote) {
					tile.openGui(playerIn, heldItem);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void rotateAfterPlacement(EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		IBlockState state = world.getBlockState(pos);

		EnumFacing facing = getPlacementRotation(player, world, pos, side);
		world.setBlockState(pos, state.withProperty(FACING, facing));
	}

	protected EnumFacing getPlacementRotation(EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		int l = MathHelper.floor_double(player.rotationYaw * 4F / 360F + 0.5D) & 3;
		if (l == 1) {
			return EnumFacing.EAST;
		}
		if (l == 2) {
			return EnumFacing.SOUTH;
		}
		if (l == 3) {
			return EnumFacing.WEST;
		}
		return EnumFacing.NORTH;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {

		if (world.isRemote) {
			return;
		}

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof IInventory) {
			IInventory inventory = (IInventory) tile;
			InventoryUtil.dropInventory(inventory, world, pos);
			if (tile instanceof TileForestry) {
				((TileForestry) tile).onRemoval();
			}
			if (tile instanceof ISocketable) {
				InventoryUtil.dropSockets((ISocketable) tile, tile.getWorld(), tile.getPos());
			}
		}
		super.breakBlock(world, pos, state);
	}

	public void init() {
		blockType.getMachineProperties().registerTileEntity();
	}

	/* ITEM MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		blockType.getMachineProperties().registerModel(item, manager);
	}
	
	/* STATES */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new MachineStateMapper<>(blockType));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}
	
	@Override
	public BlockStateContainer getBlockState() {
		return this.blockState;
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return super.withMirror(state, mirrorIn);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		EnumFacing facing = state.getValue(FACING);
		return state.withProperty(FACING, rot.rotate(facing));
	}

	@Override
	public boolean getUseNeighborBrightness(IBlockState state) {
		return hasTESR;
	}

	/* Particles */
	@SideOnly(Side.CLIENT)
	@Override
	public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager effectRenderer) {
		if (blockType.getMachineProperties() instanceof IMachinePropertiesTesr) {
			return ParticleHelper.addBlockHitEffects(worldObj, target.getBlockPos(), target.sideHit, effectRenderer, particleCallback);
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager effectRenderer) {
		if (blockType.getMachineProperties() instanceof IMachinePropertiesTesr) {
			IBlockState blockState = world.getBlockState(pos);
			return ParticleHelper.addDestroyEffects(world, this, blockState, pos, effectRenderer, particleCallback);
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerSprites(ITextureManager manager) {
		IMachineProperties<?> machineProperties = blockType.getMachineProperties();
		if (machineProperties instanceof IMachinePropertiesTesr) {
			TextureMap textureMapBlocks = Proxies.common.getClientInstance().getTextureMapBlocks();
			String particleTextureLocation = ((IMachinePropertiesTesr) machineProperties).getParticleTextureLocation();
			textureMapBlocks.registerSprite(new ResourceLocation(particleTextureLocation));
		}
	}
}
