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

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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
import forestry.api.core.IModelManager;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IStateMapperRegister;
import forestry.core.access.IAccessHandler;
import forestry.core.circuits.ISocketable;
import forestry.core.fluids.FluidHelper;
import forestry.core.proxy.Proxies;
import forestry.core.render.StateMapperMachine;
import forestry.core.tiles.MachineDefinition;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.StringUtil;

public class BlockBase<C extends Enum<C> & IMachineProperties & IStringSerializable> extends BlockForestry implements IItemModelRegister, IStateMapperRegister {
	private final List<MachineDefinition> definitions = new ArrayList<>();
	private final boolean hasTESR;
	private final Class<C> clazz;
	
	/* PROPERTYS */
	private final PropertyEnum<C> META;
	private final PropertyEnum<EnumFacing> FACE;
	
    protected final BlockState blockState;
	
	public boolean isReady = false;
	
	public BlockBase(Class<C> clazz) {
		this(false, clazz);
	}

	public BlockBase(boolean hasTESR, Class<C> clazz) {
		super(Material.iron);

		this.hasTESR = hasTESR;
		this.lightOpacity = this.isOpaqueCube() ? 255 : 0;
		this.clazz = clazz;
		
		isReady = true;
		
		META = PropertyEnum.create("meta", clazz);
		FACE = PropertyEnum.create("face", EnumFacing.class);
		
        this.blockState = this.createBlockState();
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACE, EnumFacing.NORTH));
	}

	public void addDefinitions(MachineDefinition... definitions) {
		for (MachineDefinition definition : definitions) {
			addDefinition(definition);
		}
	}

	public void addDefinition(MachineDefinition definition) {
		definition.setBlock(this);

		while (definitions.size() <= definition.getMeta()) {
			definitions.add(null);
		}

		definitions.set(definition.getMeta(), definition);
	}

	@Override
	public boolean isOpaqueCube() {
		return !hasTESR;
	}
	
	@Override
	public boolean isNormalCube() {
		return !hasTESR;
	}

	@Override
	public int getRenderType() {
		if (hasTESR) {
			return 2;
		} else {
			return 3;
		}
	}

	private MachineDefinition getDefinition(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (!(state.getBlock() instanceof BlockBase)) {
			return null;
		}
		IMachineProperties meta = state.getValue(META);
		return getDefinition(meta.getMeta());
	}
	
	private MachineDefinition getDefinition(int metadata) {
		if (metadata < 0 || metadata >= definitions.size()) {
			return null;
		}

		return definitions.get(metadata);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		MachineDefinition definition = getDefinition(world, pos);
		if (definition == null) {
			return super.getCollisionBoundingBox(world, pos, state);
		}
		return definition.getBoundingBox(pos, state);
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
		MachineDefinition definition = getDefinition(world, pos);
		if (definition == null) {
			return super.collisionRayTrace(world, pos, start, end);
		} else {
			return definition.collisionRayTrace(world, pos, start, end);
		}
	}

	/* CREATIVE INVENTORY */
	@SuppressWarnings("rawtypes")
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (MachineDefinition definition : definitions) {
			if (definition == null) {
				continue;
			}
			definition.getSubBlocks(item, tab, list);
		}
	}

	/* TILE ENTITY CREATION */
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		IMachineProperties definitionP = state.getValue(META);
		MachineDefinition definition = getDefinition(definitionP.getMeta());
		if (definition == null) {
			return null;
		}
		return definition.createMachine();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return createTileEntity(world, getStateFromMeta(meta)); // TODO: refactor to just use Block, not BlockContainer
	}

	/* INTERACTION */
	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		MachineDefinition definition = getDefinition(world, pos);
		return definition != null && definition.isSolidOnSide(world, pos, side);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) {
			return false;
		}

		TileBase tile = (TileBase) world.getTileEntity(pos);
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
		MachineDefinition definition = getDefinition(world, pos);
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
		for (MachineDefinition def : definitions) {
			if (def != null) {
				def.register();
			}
		}
	}

	/* ITEM MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (MachineDefinition def : definitions) {
			if (def == null)
				continue;
			C type = clazz.getEnumConstants()[def.getMeta()];
			if (type == null)
				return;
			manager.registerItemModel(item, def.getMeta(), StringUtil.cleanItemName(item) + "_" + type.getName());
		}
	}
	
	/* STATES */
	@Override
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new StateMapperMachine<C>(clazz, META, FACE));
	}

	@Override
	protected BlockState createBlockState() {
		if(!isReady)
			return super.createBlockState();
		return new BlockState(this, new IProperty[] { META, FACE });
	}
	

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((IMachineProperties)state.getProperties().get(META)).getMeta();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		for(C c : clazz.getEnumConstants()){
			if(c.getMeta() == meta){
				return getDefaultState().withProperty(META, c);
			}
		}
		return getDefaultState();
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileForestry) {
			TileForestry tileF = (TileForestry) tile;
			state = state.withProperty(FACE, tileF.getOrientation());
		}
		return super.getActualState(state, world, pos);
	}
	
    @Override
	public BlockState getBlockState()
    {
        return this.blockState;
    }

	@Override
	public boolean getUseNeighborBrightness() {
		return hasTESR;
	}

	public final ItemStack get(C type) {
		return new ItemStack(this, 1, type.getMeta());
	}
}
