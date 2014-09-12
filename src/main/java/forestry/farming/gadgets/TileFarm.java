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
package forestry.farming.gadgets;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IStructureLogic;
import forestry.api.core.ITileStructure;
import forestry.api.farming.Farmables;
import forestry.api.farming.IFarmComponent;
import forestry.api.farming.IFarmListener;
import forestry.core.gadgets.TileForestry;
import forestry.core.network.GuiId;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.TileInventoryAdapter;
import forestry.core.utils.Utils;

public abstract class TileFarm extends TileForestry implements IFarmComponent {

	public static final int TYPE_PLAIN = 0;
	public static final int TYPE_REVERSE = 1;
	public static final int TYPE_TOP = 2;
	public static final int TYPE_BAND = 3;
	public static final int TYPE_GEARS = 4;
	public static final int TYPE_HATCH = 5;
	public static final int TYPE_VALVE = 6;
	public static final int TYPE_CONTROL = 7;

	public enum EnumFarmBlock {

		BRICK_STONE(new ItemStack(Blocks.stonebrick, 1, 0)),
		BRICK_MOSSY(new ItemStack(Blocks.stonebrick, 1, 1)),
		BRICK_CRACKED(new ItemStack(Blocks.stonebrick, 1, 2)),
		BRICK(new ItemStack(Blocks.brick_block)),
		SANDSTONE_SMOOTH(new ItemStack(Blocks.sandstone, 1, 2)),
		SANDSTONE_CHISELED(new ItemStack(Blocks.sandstone, 1, 1)),
		BRICK_NETHER(new ItemStack(Blocks.nether_brick)),
		BRICK_CHISELED(new ItemStack(Blocks.stonebrick, 1, 3)),
		QUARTZ(new ItemStack(Blocks.quartz_block, 1, 0)),
		QUARTZ_CHISELED(new ItemStack(Blocks.quartz_block, 1, 1)),
		QUARTZ_LINES(new ItemStack(Blocks.quartz_block, 1, 2));
		public final ItemStack base;

		private EnumFarmBlock(ItemStack base) {
			this.base = base;
		}
		@SideOnly(Side.CLIENT)
		private static IIcon[] icons;

		@SideOnly(Side.CLIENT)
		public static void registerIcons(IIconRegister register) {

			icons = new IIcon[8];
			// for(int i = 0; i < values().length; i++) {
			// generateTexturesIfNeeded(values()[i]);

			icons[0] = TextureManager.getInstance().registerTex(register, "farm/plain");
			icons[1] = TextureManager.getInstance().registerTex(register, "farm/reverse");
			icons[2] = TextureManager.getInstance().registerTex(register, "farm/top");
			icons[3] = TextureManager.getInstance().registerTex(register, "farm/band");
			icons[4] = TextureManager.getInstance().registerTex(register, "farm/gears");
			icons[5] = TextureManager.getInstance().registerTex(register, "farm/hatch");
			icons[6] = TextureManager.getInstance().registerTex(register, "farm/valve");
			icons[7] = TextureManager.getInstance().registerTex(register, "farm/control");
			// }
		}

		@SideOnly(Side.CLIENT)
		public IIcon getIcon(int type) {
			return icons[type];
		}

		public void saveToCompound(NBTTagCompound compound) {
			compound.setInteger("FarmBlock", this.ordinal());
		}

		public String getName() {
			return base.getItem().getItemStackDisplayName(base);
		}

		public ItemStack getCraftingIngredient() {
			return base;
		}

		public static EnumFarmBlock getFromCompound(NBTTagCompound compound) {

			if (compound != null) {
				int farmBlockOrdinal = compound.getInteger("FarmBlock");
				if (farmBlockOrdinal < EnumFarmBlock.values().length)
					return EnumFarmBlock.values()[farmBlockOrdinal];
			}

			return EnumFarmBlock.BRICK_STONE;
		}
	}

	public TileFarm() {
		this.structureLogic = Farmables.farmInterface.createFarmStructureLogic(this);
	}

	/* GUI */
	@Override
	public void openGui(EntityPlayer player) {
		if (this.isMaster())
			player.openGui(ForestryAPI.instance, GuiId.MultiFarmGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
		else if (this.hasMaster())
			player.openGui(ForestryAPI.instance, GuiId.MultiFarmGUI.ordinal(), worldObj, masterX, masterY, masterZ);
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		this.isMaster = nbttagcompound.getBoolean("IsMaster");
		this.masterX = nbttagcompound.getInteger("MasterX");
		this.masterY = nbttagcompound.getInteger("MasterY");
		this.masterZ = nbttagcompound.getInteger("MasterZ");

		farmBlock = EnumFarmBlock.getFromCompound(nbttagcompound);

		// Init for master state
		if (isMaster)
			makeMaster();

		if (inventory != null)
			inventory.readFromNBT(nbttagcompound);

		structureLogic.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setBoolean("IsMaster", isMaster);
		nbttagcompound.setInteger("MasterX", masterX);
		nbttagcompound.setInteger("MasterY", masterY);
		nbttagcompound.setInteger("MasterZ", masterZ);

		farmBlock.saveToCompound(nbttagcompound);

		if (inventory != null)
			inventory.writeToNBT(nbttagcompound);

		structureLogic.writeToNBT(nbttagcompound);
	}

	/* UPDATING */
	@Override
	public void initialize() {
	}

	@Override
	public void updateEntity() {

		if (!Proxies.common.isSimulating(worldObj))
			updateClientSide();
		else {

			if (!isInited) {
				initialize();
				isInited = true;
			}

			// Periodic validation if needed
			if (worldObj.getTotalWorldTime() % 200 == 0 && (!isIntegratedIntoStructure() || isMaster()))
				validateStructure();

			updateServerSide();
		}
	}

	protected void updateServerSide() {
	}

	protected void updateClientSide() {
	}

	/* CONSTRUCTION MATERIAL */
	EnumFarmBlock farmBlock = EnumFarmBlock.BRICK_STONE;
	protected int fixedType = -1;

	public void setFarmBlock(EnumFarmBlock block) {
		farmBlock = block;
		sendNetworkUpdate();
	}

	public EnumFarmBlock getFarmBlock() {
		return farmBlock;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		/*
		 * if(fixedType >= 0) return farmBlock.getIcon(fixedType);
		 * 
		 * int type = 0; if (meta == 1) type = TYPE_BAND;
		 * 
		 * if (meta == 0 && side == 2) type = TYPE_REVERSE; else if (meta == 0 && (side == 0 || side == 1)) type = TYPE_TOP;
		 * 
		 * return farmBlock.getIcon(type);
		 */
		return null;
	}

	/* INVENTORY MANAGMENT */
	protected TileInventoryAdapter inventory;

	protected abstract void createInventory();

	/* TILEFORESTRY */
	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = new PacketPayload(0, 1);
		payload.shortPayload[0] = (short) farmBlock.ordinal();
		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {
		EnumFarmBlock farmType = EnumFarmBlock.values()[payload.shortPayload[0]];
		if (this.farmBlock != farmType) {
			this.farmBlock = farmType;
			worldObj.func_147479_m(xCoord, yCoord, zCoord);
		}
	}

	/* ITILESTRUCTURE */
	IStructureLogic structureLogic;
	private boolean isMaster;
	private int masterX, masterZ;
	private int masterY = -99;

	@Override
	public String getTypeUID() {
		return structureLogic.getTypeUID();
	}

	@Override
	public void validateStructure() {
		structureLogic.validateStructure();
	}

	@Override
	public void makeMaster() {
		setCentralTE(null);
		this.isMaster = true;

		if (inventory == null)
			createInventory();
	}

	@Override
	public void onStructureReset() {
		setCentralTE(null);
		if (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 1)
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 0);
		isMaster = false;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public ITileStructure getCentralTE() {

		if (!isIntegratedIntoStructure())
			return null;

		if (!isMaster) {
			TileEntity tile = worldObj.getTileEntity(masterX, masterY, masterZ);
			if (tile instanceof ITileStructure) {
				ITileStructure master = (ITileStructure) worldObj.getTileEntity(masterX, masterY, masterZ);
				if (master.isMaster())
					return master;
				else
					return null;
			} else
				return null;
		} else
			return this;

	}

	private boolean isSameTile(TileEntity tile) {
		return tile.xCoord == xCoord && tile.yCoord == yCoord && tile.zCoord == zCoord;
	}

	@Override
	public void setCentralTE(TileEntity tile) {
		if (tile == null || tile == this || isSameTile(tile)) {
			this.masterX = this.masterZ = 0;
			this.masterY = -99;
			return;
		}

		this.isMaster = false;
		this.masterX = tile.xCoord;
		this.masterY = tile.yCoord;
		this.masterZ = tile.zCoord;

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public boolean isMaster() {
		return isMaster;
	}

	protected boolean hasMaster() {
		return this.masterY >= 0;
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public boolean isIntegratedIntoStructure() {
		return isMaster || this.masterY >= 0;
	}

	@Override
	public void registerListener(IFarmListener listener) {
	}

	@Override
	public void removeListener(IFarmListener listener) {
	}

	/* INTERACTION */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return Utils.isUseableByPlayer(player, this, worldObj, xCoord, yCoord, zCoord);
	}
}
