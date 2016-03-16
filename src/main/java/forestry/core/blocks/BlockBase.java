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
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.core.access.IAccessHandler;
import forestry.core.circuits.ISocketable;
import forestry.core.fluids.FluidHelper;
import forestry.core.proxy.Proxies;
import forestry.core.render.MachineStateMapper;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.PlayerUtil;

import jline.internal.Log;

public class BlockBase<P extends Enum<P> & IBlockType & IStringSerializable> extends BlockForestry implements IItemModelRegister, IStateMapperRegister {
	private final Map<P, IMachineProperties> properties = new HashMap<>();
	private final boolean hasTESR;
	private final boolean hasCustom;
	private final Class<P> machinePropertiesClass;
	
	/* PROPERTIES */
	private final PropertyEnum<P> TYPE;
	private final PropertyEnum<EnumFacing> FACE;
	
	protected final BlockState blockState;
	
	private boolean isReady = false;

	public BlockBase(Class<P> machinePropertiesClass) {
		super(Material.iron);

		this.hasTESR = IBlockTypeTesr.class.isAssignableFrom(machinePropertiesClass);
		this.hasCustom = IBlockTypeCustom.class.isAssignableFrom(machinePropertiesClass);
		this.lightOpacity = this.isOpaqueCube() ? 255 : 0;
		this.machinePropertiesClass = machinePropertiesClass;
		
		isReady = true;
		
		TYPE = PropertyEnum.create("type", machinePropertiesClass);
		FACE = PropertyEnum.create("face", EnumFacing.class);
		
		this.blockState = this.createBlockState();
		IBlockState state = this.blockState.getBaseState().withProperty(FACE, EnumFacing.NORTH);
		this.setDefaultState(state);
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
	public boolean isOpaqueCube() {
		return !hasTESR && !hasCustom;
	}
	
	@Override
	public boolean isNormalCube() {
		return !hasTESR && !hasCustom;
	}

	@Override
	public int getRenderType() {
		if (hasTESR) {
			return 2;
		} else {
			return 3;
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
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		IMachineProperties definition = getDefinition(world, pos);
		if (definition == null) {
			return super.getCollisionBoundingBox(world, pos, state);
		}
		return definition.getBoundingBox(pos, state);
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
		IMachineProperties definition = getDefinition(world, pos);
		if (definition == null) {
			return super.collisionRayTrace(world, pos, start, end);
		} else {
			return definition.collisionRayTrace(world, pos, start, end);
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
	public TileEntity createTileEntity(World world, IBlockState state) {
		IMachineProperties definition = state.getValue(TYPE).getMachineProperties();
		if (definition == null) {
			return null;
		}
		return definition.createTileEntity();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		IBlockState state = getStateFromMeta(meta);
		return createTileEntity(world, state); // TODO: refactor to just use Block, not BlockContainer
	}

	/* INTERACTION */
	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		IMachineProperties definition = getDefinition(world, pos);
		return definition != null && definition.isSolidOnSide(world, pos, side);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) {
			return false;
		}

		TileBase tile = TileUtil.getTile(world, pos, TileBase.class);
		if (tile == null) {
			return false;
		}

		if (!TileUtil.isUsableByPlayer(player, tile)) {
			return false;
		}

		IAccessHandler access = tile.getAccessHandler();

		ItemStack current = player.getCurrentEquippedItem();
		if (current != null && current.getItem() != Items.bucket && tile instanceof IFluidHandler && access.allowsAlteration(player)) {
			if (FluidHelper.handleRightClick((IFluidHandler) tile, side, player, true, tile.canDrainWithBucket())) {
				return true;
			}
		}

		if (world.isRemote) {
			return true;
		}

		if (access.allowsViewing(player)) {
			tile.openGui(player);
		} else {
			player.addChatMessage(new ChatComponentTranslation("for.chat.accesslocked", PlayerUtil.getOwnerName(access)));
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
	protected BlockState createBlockState() {
		if (!isReady) {
			return super.createBlockState();
		}
		return new BlockState(this, TYPE, FACE);
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
	public BlockState getBlockState() {
		return this.blockState;
	}

	@Override
	public boolean getUseNeighborBrightness() {
		return hasTESR;
	}

	public final ItemStack get(P type) {
		return new ItemStack(this, 1, type.getMachineProperties().getMeta());
	}
}
