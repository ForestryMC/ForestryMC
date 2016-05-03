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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.ITextureManager;
import forestry.core.access.IAccessHandler;
import forestry.core.circuits.ISocketable;
import forestry.core.fluids.FluidHelper;
import forestry.core.proxy.Proxies;
import forestry.core.render.MachineParticleCallback;
import forestry.core.render.MachineStateMapper;
import forestry.core.render.ParticleHelper;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.PlayerUtil;

import jline.internal.Log;

public class BlockBase<P extends Enum<P> & IBlockType & IStringSerializable> extends BlockForestry implements IItemModelRegister, ISpriteRegister, IStateMapperRegister, IBlockWithMeta {
	private final Map<P, IMachineProperties> properties = new HashMap<>();
	private final boolean hasTESR;
	private final boolean hasCustom;
	private final Class<P> machinePropertiesClass;
	
	/* PROPERTIES */
	private final PropertyEnum<P> TYPE;
	private final PropertyEnum<EnumFacing> FACE;
	
	private final ParticleHelper.Callback particleCallback;
	
	protected final BlockStateContainer blockState;
	
	private boolean canCreateProps = false;

	public BlockBase(Class<P> machinePropertiesClass) {
		super(Material.IRON);

		this.hasTESR = IBlockTypeTesr.class.isAssignableFrom(machinePropertiesClass);
		this.hasCustom = IBlockTypeCustom.class.isAssignableFrom(machinePropertiesClass);
		this.lightOpacity = (!hasTESR && !hasCustom) ? 255 : 0;
		this.machinePropertiesClass = machinePropertiesClass;
		
		canCreateProps = true;
		
		TYPE = PropertyEnum.create("type", machinePropertiesClass);
		FACE = PropertyEnum.create("face", EnumFacing.class);
		
		this.blockState = this.createBlockState();
		IBlockState state = this.blockState.getBaseState().withProperty(FACE, EnumFacing.NORTH);
		this.setDefaultState(state);
		
		particleCallback = new MachineParticleCallback<>(this, TYPE);
	}

	public PropertyEnum<P> getTypeProperty() {
		return TYPE;
	}

	@SafeVarargs
	public final void addDefinitions(P... blockTypes) {
		for (P blockType : blockTypes) {
			IMachineProperties<?> machineProperties = blockType.getMachineProperties();
			if (machineProperties != null) {
				machineProperties.setBlock(this);
				this.properties.put(blockType, machineProperties);
			} else {
				Log.error("Null Definition found {}", Arrays.toString(blockTypes));
			}
		}
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

	private IMachineProperties getDefinition(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (!(state.getBlock() instanceof BlockBase)) {
			return null;
		}
		return state.getValue(TYPE).getMachineProperties();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		IMachineProperties definition = getDefinition(worldIn, pos);
		if (definition == null) {
			return super.getCollisionBoundingBox(blockState, worldIn, pos);
		}
		return definition.getBoundingBox(pos, blockState);
	}

	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
		IMachineProperties definition = getDefinition(worldIn, pos);
		if (definition == null) {
			return super.collisionRayTrace(blockState, worldIn, pos, start, end);
		} else {
			return definition.collisionRayTrace(worldIn, pos, start, end);
		}
	}

	/* CREATIVE INVENTORY */
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (IMachineProperties definition : properties.values()) {
			definition.getSubBlocks(item, tab, list);
		}
	}

	/* TILE ENTITY CREATION */
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		IMachineProperties definition = getStateFromMeta(meta).getValue(TYPE).getMachineProperties();
		if (definition == null) {
			return null;
		}
		return definition.createTileEntity();
	}

	/* INTERACTION */
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		IMachineProperties definition = getDefinition(world, pos);
		return definition != null && definition.isSolidOnSide(world, pos, side);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (playerIn.isSneaking()) {
			return false;
		}

		TileBase tile = TileUtil.getTile(worldIn, pos, TileBase.class);
		if (tile == null) {
			return false;
		}

		if (!TileUtil.isUsableByPlayer(playerIn, tile)) {
			return false;
		}

		IAccessHandler access = tile.getAccessHandler();

		if (heldItem != null && heldItem.getItem() != Items.BUCKET && tile instanceof IFluidHandler && access.allowsAlteration(playerIn)) {
			if (FluidHelper.handleRightClick((IFluidHandler) tile, side, playerIn, true, tile.canDrainWithBucket())) {
				return true;
			}
		}

		if (worldIn.isRemote) {
			return true;
		}

		if (access.allowsViewing(playerIn)) {
			tile.openGui(playerIn, heldItem);
		} else {
			playerIn.addChatMessage(new TextComponentTranslation("for.chat.accesslocked", PlayerUtil.getOwnerName(access)));
		}
		return true;
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		IMachineProperties definition = getDefinition(world, pos);
		if (definition == null) {
			return super.rotateBlock(world, pos, axis);
		}
		return definition.rotateBlock(world, pos, axis);
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

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	public void init() {
		for (IMachineProperties def : properties.values()) {
			def.registerTileEntity();
		}
	}

	/* ITEM MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (IMachineProperties def : properties.values()) {
			def.registerModel(item, manager);
		}
	}
	
	/* STATES */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new MachineStateMapper<>(machinePropertiesClass, TYPE, FACE));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		if (!canCreateProps) {
			return super.createBlockState();
		}
		return new BlockStateContainer(this, TYPE, FACE);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		IBlockType blockType = (IBlockType) state.getProperties().get(this.TYPE);
		IMachineProperties machineProperties = blockType.getMachineProperties();
		return machineProperties.getMeta();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		for (Map.Entry<P, IMachineProperties> entry : properties.entrySet()) {
			IMachineProperties<?> machineProperties = entry.getValue();
			if (machineProperties.getMeta() == meta) {
				P blockType = entry.getKey();
				return getDefaultState().withProperty(this.TYPE, blockType);
			}
		}
		return getDefaultState();
	}

	@Override
	public String getNameFromMeta(int meta) {
		IBlockState state = getStateFromMeta(meta);
		IBlockType blockType = (IBlockType) state.getProperties().get(TYPE);
		IMachineProperties machineProperties = blockType.getMachineProperties();
		return machineProperties.getName();
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileForestry tile = TileUtil.getTile(world, pos, TileForestry.class);
		if (tile != null) {
			state = state.withProperty(FACE, tile.getOrientation());
		}
		return super.getActualState(state, world, pos);
	}
	
	@Override
	public BlockStateContainer getBlockState() {
		return this.blockState;
	}

	@Override
	public boolean getUseNeighborBrightness(IBlockState state) {
		return hasTESR;
	}

	/* Particles */
	@SideOnly(Side.CLIENT)
	@Override
	public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, EffectRenderer effectRenderer) {
		P property = state.getValue(TYPE);
		if(property.getMachineProperties() instanceof IMachinePropertiesTesr){
			return ParticleHelper.addHitEffects(worldObj, this, target, effectRenderer, particleCallback);
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer) {
		IBlockState state = world.getBlockState(pos);
		
		P property = state.getValue(TYPE);
		if(property.getMachineProperties() instanceof IMachinePropertiesTesr){
			return ParticleHelper.addDestroyEffects(world, this, world.getBlockState(pos), pos, effectRenderer, particleCallback);
		}
		return false;
	}
	
	

	public final ItemStack get(P type) {
		return new ItemStack(this, 1, type.getMachineProperties().getMeta());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerSprites(ITextureManager manager) {
		for(P property : properties.keySet()){
			if(property.getMachineProperties() instanceof IMachinePropertiesTesr){
				Proxies.common.getClientInstance().getTextureMapBlocks().registerSprite(new ResourceLocation(((IMachinePropertiesTesr)property.getMachineProperties()).getParticleTextureLocation()));
			}
		}
	}
}
