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
import java.util.List;

import com.oracle.webservices.internal.api.message.BasePropertySet.PropertyMapEntry;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelObject;
import forestry.api.core.IVariantObject;
import forestry.core.fluids.FluidHelper;
import forestry.core.gadgets.BlockBase.IEnumMachineDefinition;
import forestry.core.interfaces.IOwnable;
import forestry.core.items.ItemNBTTile;
import forestry.core.proxy.Proxies;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Utils;

public class BlockBase extends BlockForestry implements IVariantObject, IModelObject {

	private static final List<String> variants = new ArrayList<String>();
	private final List<MachineDefinition> definitions = new ArrayList<MachineDefinition>();
	private final boolean hasTESR;
	private final Class<? extends IEnumMachineDefinition> enumDefinition;
	private PropertyEnum META;
	
	public interface IEnumMachineDefinition extends IStringSerializable
	{
		int getMeta();
	}

	public BlockBase(Material material, Class<? extends IEnumMachineDefinition> enumDefinition) {
		this(material, false, enumDefinition);
	}

	public BlockBase(Material material, boolean hasTESR, Class<? extends IEnumMachineDefinition> enumDefinition) {
		super(material);
		this.enumDefinition = enumDefinition;
		this.hasTESR = hasTESR;
		META = PropertyEnum.create("meta", enumDefinition);
	}

	public MachineDefinition addDefinition(MachineDefinition definition) {
		definition.setBlock(this);

		while (definitions.size() <= definition.meta) {
			definitions.add(null);
		}

		variants.add(StringUtil.cleanTags(definition.teIdent));
		definitions.set(definition.meta, definition);
		return definition;
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((IEnumMachineDefinition)state.getProperties().get(META)).getMeta();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(META, meta);
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

	private MachineDefinition getDefinition(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return getDefinition(state.getBlock().getMetaFromState(state));
	}

	private MachineDefinition getDefinition(int metadata) {
		if (metadata >= definitions.size() || definitions.get(metadata) == null) {
			return definitions.get(0);
		}

		return definitions.get(metadata);
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

	/* BLOCK DROPS */
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		int metadata = getMetaFromState(state);
		if (getDefinition(metadata).handlesDrops()) {
			return getDefinition(metadata).getDrops(world, pos, state, fortune);
		} else {
			return super.getDrops(world, pos, state, fortune);
		}
	}

	/* INTERACTION */
	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		MachineDefinition definition = getDefinition(world, pos);
		return definition != null && definition.isSolidOnSide(world, pos, side);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityliving, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, entityliving, stack);

		TileForestry tile = (TileForestry) world.getTileEntity(pos);

		if (stack.getItem() instanceof ItemNBTTile && stack.hasTagCompound()) {
			tile.readFromNBT(stack.getTagCompound());
			tile.setPos(pos);
		}

		tile.rotateAfterPlacement(world, pos, entityliving, stack);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (getDefinition(world, pos).onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ)) {
			return true;
		}

		if (player.isSneaking()) {
			return false;
		}

		TileBase tile = (TileBase) world.getTileEntity(pos);
		if (!Utils.isUseableByPlayer(player, tile)) {
			return false;
		}

		ItemStack current = player.getCurrentEquippedItem();
		if (current != null && current.getItem() != Items.bucket && tile instanceof IFluidHandler && tile.allowsAlteration(player)) {
			if (FluidHelper.handleRightClick((IFluidHandler) tile, side, player, true, tile.canDrainWithBucket())) {
				return true;
			}
		}

		if (!Proxies.common.isSimulating(world)) {
			return true;
		}

		if (tile.allowsViewing(player)) {
			tile.openGui(player, tile);
		} else {
			player.addChatMessage(new ChatComponentTranslation("for.chat.accesslocked", PlayerUtil.getOwnerName(tile)));
		}
		return true;
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing side) {
		return getDefinition(world, pos).rotateBlock(world, pos, side);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);
		getDefinition(world, pos).onBlockAdded(world, pos, state);
	}

	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {

		IOwnable tile = (IOwnable) world.getTileEntity(pos);
		if (tile == null) {
			return world.setBlockToAir(pos);
		}

		if (tile.isOwnable() && !tile.allowsRemoval(player)) {
			return false;
		}

		if (getDefinition(world, pos).removedByPlayer(world, player, pos)) {
			return world.setBlockToAir(pos);
		} else {
			return false;
		}
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing face) {
		IBlockState state = world.getBlockState(pos);
		int metadata = getMetaFromState(state);
		if (metadata >= definitions.size() || definitions.get(metadata) == null) {
			metadata = 0;
		}
		return definitions.get(metadata).canConnectRedstone(world, pos, face);
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
	public ModelType getModelType() {
		return ModelType.META;
	}

	@Override
	public String[] getVariants() {
		return variants.toArray(new String[variants.size()]);
	}
}
