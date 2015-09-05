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
package forestry.core.gadgets;

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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
import forestry.api.core.IModelRegister;
import forestry.core.circuits.ISocketable;
import forestry.core.fluids.FluidHelper;
import forestry.core.interfaces.IAccessHandler;
import forestry.core.items.ItemNBTTile;
import forestry.core.proxy.Proxies;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.Utils;

public class BlockBase extends BlockForestry implements IModelRegister {

	public static final PropertyEnum META = PropertyEnum.create("meta", MachineDefinitionTypes.class);
	public static final PropertyEnum FACE = PropertyEnum.create("face", EnumFacing.class, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH);
	
	private final List<MachineDefinition> definitions = new ArrayList<MachineDefinition>();
	private final boolean hasTESR;
	private final int definitionID;

	public BlockBase(Material material, int definitionID) {
		this(material, false, definitionID);
	}

	public BlockBase(Material material, boolean hasTESR, int definitionID) {
		super(material);
		this.definitionID = definitionID;
		this.hasTESR = hasTESR;
		setDefaultState(this.blockState.getBaseState().withProperty(META, MachineDefinitionTypes.getType(definitionID, 0)).withProperty(FACE, EnumFacing.WEST));
	}

	public MachineDefinition addDefinition(MachineDefinition definition) {
		definition.setBlock(this);

		while (definitions.size() <= definition.getMeta()) {
			definitions.add(null);
		}

		definitions.set(definition.getMeta(), definition);

		return definition;
	}
	
	public void registerStateMapper()
	{
		Proxies.render.registerStateMapper(this, new BaseStateMapper());
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((MachineDefinitionTypes)state.getProperties().get(META)).getMeta();
	}
	
    @Override
	protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {META, FACE});
    }
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(META,  MachineDefinitionTypes.getType(definitionID, meta));
	}

	@Override
	public boolean isOpaqueCube() {
		return !hasTESR;
	}

	@Override
	public int getRenderType() {
		if (hasTESR) {
			return Proxies.common.getByBlockModelId();
		} else {
			return 3;
		}
	}
	
	public int getDefinitionID() {
		return definitionID;
	}

	private MachineDefinition getDefinition(IBlockAccess world, BlockPos pos) {
		return getDefinition(getMetaFromState(world.getBlockState(pos)));
	}

	private MachineDefinition getDefinition(int metadata) {
		if (metadata >= definitions.size() || definitions.get(metadata) == null) {
			return definitions.get(0);
		}

		return definitions.get(metadata);
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileForestry)
		{
			TileForestry tileF = (TileForestry) tile;
			state = state.withProperty(FACE, tileF.getOrientation());
		}
		return super.getActualState(state, world, pos);
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
		int metadata = getMetaFromState(state);
		if (metadata >= definitions.size() || definitions.get(metadata) == null) {
			metadata = 0;
		}

		MachineDefinition definition = definitions.get(metadata);
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
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLiving, ItemStack stack) {
		TileForestry tile = (TileForestry) world.getTileEntity(pos);

		if (stack.getItem() instanceof ItemNBTTile && stack.hasTagCompound()) {
			tile.readFromNBT(stack.getTagCompound());
			tile.setPos(pos);
		}

		tile.rotateAfterPlacement(entityLiving);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (getDefinition(world, pos).onBlockActivated(world, pos, player, side)) {
			return true;
		}

		if (player.isSneaking()) {
			return false;
		}

		TileBase tile = (TileBase) world.getTileEntity(pos);
		if (!Utils.isUseableByPlayer(player, tile)) {
			return false;
		}

		IAccessHandler access = tile.getAccessHandler();

		ItemStack current = player.getCurrentEquippedItem();
		if (current != null && current.getItem() != Items.bucket && tile instanceof IFluidHandler && access.allowsAlteration(player)) {
			if (FluidHelper.handleRightClick((IFluidHandler) tile, side, player, true, tile.canDrainWithBucket())) {
				return true;
			}
		}

		if (!Proxies.common.isSimulating(world)) {
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
		return getDefinition(world, pos).rotateBlock(world, pos, axis);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		
		if (!Proxies.common.isSimulating(world)) {
			return;
		}

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof IInventory) {
			IInventory inventory = (IInventory) tile;
			Utils.dropInventory(inventory, world, pos);
			if (tile instanceof TileForestry) {
				((TileForestry) tile).onRemoval();
			}
			if (tile instanceof ISocketable) {
				Utils.dropSockets((ISocketable) tile, tile.getWorld(), tile.getPos());
			}
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public boolean getUseNeighborBrightness() {
		return hasTESR;
	}

	@Override
	public void registerModel(Item item, IModelManager manager) {
		for(int i = 0; i < definitions.size();i++)
		{
			if(definitions.get(i) == null)
				return;
			MachineDefinitionTypes type = MachineDefinitionTypes.getType(definitionID, i);
			if(type == null)
				return;
			manager.registerItemModel(item, i, "_" + type.getName());
		}
	}
	
	@SideOnly(Side.CLIENT)
	public class BaseStateMapper implements IStateMapper{

	    protected Map mapStateModelLocations = Maps.newLinkedHashMap();
	    
	    public String getPropertyString(Map p_178131_1_)
	    {
	        StringBuilder stringbuilder = new StringBuilder();
	        Iterator iterator = p_178131_1_.entrySet().iterator();

	        while (iterator.hasNext())
	        {
	            Entry entry = (Entry)iterator.next();

	            if (stringbuilder.length() != 0)
	            {
	                stringbuilder.append(",");
	            }

	            IProperty iproperty = (IProperty)entry.getKey();
	            Comparable comparable = (Comparable)entry.getValue();
	            stringbuilder.append(iproperty.getName());
	            stringbuilder.append("=");
	            stringbuilder.append(iproperty.getName(comparable));
	        }

	        if (stringbuilder.length() == 0)
	        {
	            stringbuilder.append("normal");
	        }

	        return stringbuilder.toString();
	    }
	    
		@Override
		public Map putStateModelLocations(Block block) {
			for(MachineDefinitionTypes definition : MachineDefinitionTypes.values())
			{
				if(definition.getDefinitionID() != ((BlockBase)block).getDefinitionID() || ((BlockBase)block).hasTESR)
					continue;
				for(EnumFacing facing : EnumFacing.values()){
					if(facing == EnumFacing.DOWN || facing == EnumFacing.UP)
						continue;
					IBlockState state = getDefaultState().withProperty(META, definition).withProperty(FACE, facing);
					LinkedHashMap linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
					String s = String.format("%s:%s", ((ResourceLocation)Block.blockRegistry.getNameForObject(block)).getResourceDomain(), ((ResourceLocation)Block.blockRegistry.getNameForObject(block)).getResourcePath() + "_" + META.getName((Comparable)linkedhashmap.remove(META)));;
					mapStateModelLocations.put(state, new ModelResourceLocation(s, getPropertyString(linkedhashmap)));
				}
			}
			return this.mapStateModelLocations;
		}
		
	}
	
}
