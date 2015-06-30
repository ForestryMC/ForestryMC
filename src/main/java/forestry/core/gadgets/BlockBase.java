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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.fluids.FluidHelper;
import forestry.core.interfaces.IAccessHandler;
import forestry.core.interfaces.IRestrictedAccessTile;
import forestry.core.items.ItemNBTTile;
import forestry.core.proxy.Proxies;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.Utils;

public class BlockBase extends BlockForestry {

	private final List<MachineDefinition> definitions = new ArrayList<MachineDefinition>();
	private final boolean hasTESR;

	public BlockBase(Material material) {
		this(material, false);
	}

	public BlockBase(Material material, boolean hasTESR) {
		super(material);

		this.hasTESR = hasTESR;
	}

	public MachineDefinition addDefinition(MachineDefinition definition) {
		definition.setBlock(this);

		while (definitions.size() <= definition.meta) {
			definitions.add(null);
		}

		definitions.set(definition.meta, definition);

		return definition;
	}

	@Override
	public boolean isOpaqueCube() {
		return !hasTESR;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return !hasTESR;
	}

	@Override
	public int getRenderType() {
		if (hasTESR) {
			return Proxies.common.getByBlockModelId();
		} else {
			return 0;
		}
	}

	private MachineDefinition getDefinition(IBlockAccess world, int x, int y, int z) {
		return getDefinition(world.getBlockMetadata(x, y, z));
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
	public TileEntity createTileEntity(World world, int metadata) {
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
		return createTileEntity(world, meta); // TODO: refactor to just use Block, not BlockContainer
	}

	/* BLOCK DROPS */
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		if (getDefinition(metadata).handlesDrops()) {
			return getDefinition(metadata).getDrops(world, x, y, z, metadata, fortune);
		} else {
			return super.getDrops(world, x, y, z, metadata, fortune);
		}
	}

	/* INTERACTION */
	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		MachineDefinition definition = getDefinition(world, x, y, z);
		return definition != null && definition.isSolidOnSide(world, x, y, z, side.ordinal());
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, entityliving, stack);

		TileForestry tile = (TileForestry) world.getTileEntity(x, y, z);

		if (stack.getItem() instanceof ItemNBTTile && stack.hasTagCompound()) {
			tile.readFromNBT(stack.getTagCompound());
			tile.xCoord = x;
			tile.yCoord = y;
			tile.zCoord = z;
		}

		tile.rotateAfterPlacement(world, x, y, z, entityliving, stack);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9) {
		if (getDefinition(world, x, y, z).onBlockActivated(world, x, y, z, player, side, par7, par8, par9)) {
			return true;
		}

		if (player.isSneaking()) {
			return false;
		}

		TileBase tile = (TileBase) world.getTileEntity(x, y, z);
		if (!Utils.isUseableByPlayer(player, tile)) {
			return false;
		}

		IAccessHandler access = tile.getAccessHandler();

		ItemStack current = player.getCurrentEquippedItem();
		if (current != null && current.getItem() != Items.bucket && tile instanceof IFluidHandler && access.allowsAlteration(player)) {
			if (FluidHelper.handleRightClick((IFluidHandler) tile, ForgeDirection.getOrientation(side), player, true, tile.canDrainWithBucket())) {
				return true;
			}
		}

		if (!Proxies.common.isSimulating(world)) {
			return true;
		}

		if (access.allowsViewing(player)) {
			tile.openGui(player, tile);
		} else {
			player.addChatMessage(new ChatComponentTranslation("for.chat.accesslocked", PlayerUtil.getOwnerName(access)));
		}
		return true;
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		return getDefinition(world, x, y, z).rotateBlock(world, x, y, z, axis);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		getDefinition(world, x, y, z).onBlockAdded(world, x, y, z);
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {

		IRestrictedAccessTile tile = (IRestrictedAccessTile) world.getTileEntity(x, y, z);
		if (tile == null) {
			return world.setBlockToAir(x, y, z);
		}

		IAccessHandler accessHandler = tile.getAccessHandler();

		if (accessHandler.isOwned() && !accessHandler.allowsRemoval(player)) {
			return false;
		}

		if (getDefinition(world, x, y, z).removedByPlayer(world, player, x, y, z)) {
			return world.setBlockToAir(x, y, z);
		} else {
			return false;
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {

		if (!Proxies.common.isSimulating(world)) {
			return;
		}

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof IInventory) {
			IInventory inventory = (IInventory) tile;
			Utils.dropInventory(inventory, world, x, y, z);
			if (tile instanceof TileForestry) {
				((TileForestry) tile).onRemoval();
			}
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		int metadata = world.getBlockMetadata(x, y, z);
		if (metadata >= definitions.size() || definitions.get(metadata) == null) {
			metadata = 0;
		}
		return definitions.get(metadata).canConnectRedstone(world, x, y, z, side);
	}

	@Override
	public int damageDropped(int metadata) {
		return metadata;
	}

	/* TEXTURES */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		for (MachineDefinition def : definitions) {
			if (def == null) {
				continue;
			}
			def.registerIcons(register);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata) {
		if (metadata >= definitions.size() || definitions.get(metadata) == null) {
			return null;
		}
		return definitions.get(metadata).getBlockTextureFromSideAndMetadata(side, metadata);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int metadata = world.getBlockMetadata(x, y, z);
		if (metadata >= definitions.size() || definitions.get(metadata) == null) {
			metadata = 0;
		}
		return definitions.get(metadata).getIcon(world, x, y, z, side, metadata);
	}

	@Override
	public boolean getUseNeighborBrightness() {
		return hasTESR;
	}
}
