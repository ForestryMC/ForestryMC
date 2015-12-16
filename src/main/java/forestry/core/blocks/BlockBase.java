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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.resources.model.ModelResourceLocation;
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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
import forestry.api.core.IModelRegister;
import forestry.core.access.IAccessHandler;
import forestry.core.circuits.ISocketable;
import forestry.core.fluids.FluidHelper;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.MachineDefinition;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.PlayerUtil;

public class BlockBase<T extends IMachineProperties, C extends Enum & IMachineProperties & IStringSerializable> extends BlockForestry implements IModelRegister {
	
	private final List<MachineDefinition> definitions = new ArrayList<>();
	private final boolean hasTESR;
	public final Class<C> clazz;
	
	public int ID = 0;
	
	public PropertyEnum META;
	public PropertyEnum FACE;
	
	public static PropertyEnum METAS;
	public static PropertyEnum FACES;

	public BlockBase(Class<C> clazz) {
		this(false, clazz);
	}

	public BlockBase(boolean hasTESR, Class<C> clazz) {
		super(Material.iron);

		this.hasTESR = hasTESR;
		this.clazz = clazz;
		this.META = METAS;
		this.FACE = FACES;
	}
	
	protected static <C extends Enum & IMachineProperties & IStringSerializable> Class<C> setState(Class<C> clazz){
		METAS = PropertyEnum.create("meta", clazz);
		FACES = PropertyEnum.create("face", EnumFacing.class, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH);
		return clazz;
	}
	
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new BaseStateMapper());
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((IMachineProperties)state.getProperties().get(META)).getMeta();
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { META, FACE });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(META, MachineDefinitionTypes.getType(definitionID, meta));
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
	public int getRenderType() {
		if (hasTESR) {
			return Proxies.render.getByBlockModelRenderId();
		} else {
			return 0;
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

	/* Models */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < definitions.size(); i++) {
			if (definitions.get(i) == null)
				return;
			C type = clazz.getEnumConstants()[i];
			if (type == null)
				return;
			manager.registerItemModel(item, i, "_" + type.getName());
		}
	}

	@SideOnly(Side.CLIENT)
	public class BaseStateMapper implements IStateMapper {

		protected Map mapStateModelLocations = Maps.newLinkedHashMap();

		public String getPropertyString(Map p_178131_1_) {
			StringBuilder stringbuilder = new StringBuilder();
			Iterator iterator = p_178131_1_.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();

				if (stringbuilder.length() != 0) {
					stringbuilder.append(",");
				}

				IProperty iproperty = (IProperty) entry.getKey();
				Comparable comparable = (Comparable) entry.getValue();
				stringbuilder.append(iproperty.getName());
				stringbuilder.append("=");
				stringbuilder.append(iproperty.getName(comparable));
			}

			if (stringbuilder.length() == 0) {
				stringbuilder.append("normal");
			}

			return stringbuilder.toString();
		}

		@Override
		public Map putStateModelLocations(Block block) {
			for (C definition : clazz.getEnumConstants()) {
				if (((BlockBase) block).clazz.getEnumConstants()[definition.getMeta()] != definition || ((BlockBase) block).hasTESR)
					continue;
				for (EnumFacing facing : EnumFacing.values()) {
					if (facing == EnumFacing.DOWN || facing == EnumFacing.UP)
						continue;
					IBlockState state = getDefaultState().withProperty(META, definition).withProperty(FACE, facing);
					LinkedHashMap linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
					String s = String.format("%s:%s", Block.blockRegistry.getNameForObject(block), Block.blockRegistry.getNameForObject(block) + "_" + META.getName((Enum) linkedhashmap.remove(META)));
					;
					mapStateModelLocations.put(state, new ModelResourceLocation(s, getPropertyString(linkedhashmap)));
				}
			}
			return this.mapStateModelLocations;
		}

	}

	@Override
	public boolean getUseNeighborBrightness() {
		return hasTESR;
	}

	public final ItemStack get(T type) {
		return new ItemStack(this, 1, type.getMeta());
	}
}
