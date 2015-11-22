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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

public class BlockBase<T extends IMachineProperties> extends BlockForestry {
	private final List<MachineDefinition> definitions = new ArrayList<>();
	private final boolean hasTESR;

	public BlockBase() {
		this(false);
	}

	public BlockBase(boolean hasTESR) {
		super(Material.iron);

		this.hasTESR = hasTESR;
		this.opaque = this.isOpaqueCube();
		this.lightOpacity = this.isOpaqueCube() ? 255 : 0;
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
	public boolean renderAsNormalBlock() {
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

	private MachineDefinition getDefinition(IBlockAccess world, int x, int y, int z) {
		if (!(world.getBlock(x, y, z) instanceof BlockBase)) {
			return null;
		}
		int meta = world.getBlockMetadata(x, y, z);
		return getDefinition(meta);
	}

	private MachineDefinition getDefinition(int metadata) {
		if (metadata < 0 || metadata >= definitions.size()) {
			return null;
		}

		return definitions.get(metadata);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		MachineDefinition definition = getDefinition(world, x, y, z);
		if (definition == null) {
			return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		}
		return definition.getBoundingBox(x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		MachineDefinition definition = getDefinition(world, x, y, z);
		if (definition == null) {
			return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		}
		return definition.getBoundingBox(x, y, z);
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 startVec, Vec3 endVec) {
		MachineDefinition definition = getDefinition(world, x, y, z);
		if (definition == null) {
			return super.collisionRayTrace(world, x, y, z, startVec, endVec);
		} else {
			return definition.collisionRayTrace(world, x, y, z, startVec, endVec);
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
	public TileEntity createTileEntity(World world, int metadata) {
		MachineDefinition definition = getDefinition(metadata);
		if (definition == null) {
			return null;
		}
		return definition.createMachine();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return createTileEntity(world, meta); // TODO: refactor to just use Block, not BlockContainer
	}

	/* INTERACTION */
	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		MachineDefinition definition = getDefinition(world, x, y, z);
		return definition != null && definition.isSolidOnSide(world, x, y, z, side.ordinal());
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9) {
		if (player.isSneaking()) {
			return false;
		}

		TileBase tile = (TileBase) world.getTileEntity(x, y, z);
		if (!TileUtil.isUsableByPlayer(player, tile)) {
			return false;
		}

		IAccessHandler access = tile.getAccessHandler();

		ItemStack current = player.getCurrentEquippedItem();
		if (current != null && current.getItem() != Items.bucket && tile instanceof IFluidHandler && access.allowsAlteration(player)) {
			if (FluidHelper.handleRightClick((IFluidHandler) tile, ForgeDirection.getOrientation(side), player, true, tile.canDrainWithBucket())) {
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
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		MachineDefinition definition = getDefinition(world, x, y, z);
		if (definition == null) {
			return super.rotateBlock(world, x, y, z, axis);
		}
		return definition.rotateBlock(world, x, y, z, axis);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {

		if (world.isRemote) {
			return;
		}

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof IInventory) {
			IInventory inventory = (IInventory) tile;
			InventoryUtil.dropInventory(inventory, world, x, y, z);
			if (tile instanceof TileForestry) {
				((TileForestry) tile).onRemoval();
			}
			if (tile instanceof ISocketable) {
				InventoryUtil.dropSockets((ISocketable) tile, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
			}
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public int damageDropped(int metadata) {
		return metadata;
	}

	public void init() {
		for (MachineDefinition def : definitions) {
			if (def != null) {
				def.register();
			}
		}
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
		return definitions.get(metadata).getBlockTextureForSide(side);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int metadata = world.getBlockMetadata(x, y, z);
		if (metadata >= definitions.size() || definitions.get(metadata) == null) {
			metadata = 0;
		}
		return definitions.get(metadata).getIcon(world, x, y, z, side);
	}

	@Override
	public boolean getUseNeighborBrightness() {
		return hasTESR;
	}

	public final ItemStack get(T type) {
		return new ItemStack(this, 1, type.getMeta());
	}
}
