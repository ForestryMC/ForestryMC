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
package forestry.farming.multiblock;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.farming.IFarmComponent;
import forestry.core.circuits.ISocketable;
import forestry.core.config.Config;
import forestry.core.interfaces.IHintSource;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.MultiblockControllerBase;
import forestry.core.multiblock.MultiblockValidationException;
import forestry.core.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.network.IStreamableGui;

public abstract class TileFarm extends RectangularMultiblockTileEntityBase implements IFarmComponent, IHintSource, ISocketable, IStreamableGui, IErrorLogicSource {

	public static final int TYPE_PLAIN = 0;
	public static final int TYPE_REVERSE = 1;
	public static final int TYPE_TOP = 2;
	public static final int TYPE_BAND = 3;
	public static final int TYPE_GEARS = 4;
	public static final int TYPE_HATCH = 5;
	public static final int TYPE_VALVE = 6;
	public static final int TYPE_CONTROL = 7;

	private EnumFarmBlockTexture farmBlockTexture = EnumFarmBlockTexture.BRICK_STONE;

	@Override
	public void openGui(EntityPlayer player) {
		if (this.isConnected()) {
			player.openGui(ForestryAPI.instance, GuiId.MultiFarmGUI.ordinal(), worldObj, pos.getX(), pos.getY(), pos.getZ());
		}
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return getFarmController().getInternalInventory();
	}

	@Override
	public boolean allowsAutomation() {
		return false;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		farmBlockTexture = EnumFarmBlockTexture.getFromCompound(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		farmBlockTexture.saveToCompound(nbttagcompound);
	}

	/* CONSTRUCTION MATERIAL */

	public void setFarmBlockTexture(EnumFarmBlockTexture farmBlockTexture) {
		if (this.farmBlockTexture != farmBlockTexture) {
			this.farmBlockTexture = farmBlockTexture;
			worldObj.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	public EnumFarmBlockTexture getFarmBlockTexture() {
		return farmBlockTexture;
	}

	/* TILEFORESTRY */

	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		farmBlockTexture.saveToCompound(packetData);
	}

	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		EnumFarmBlockTexture farmBlockTexture = EnumFarmBlockTexture.getFromCompound(packetData);
		setFarmBlockTexture(farmBlockTexture);
	}

	@Override
	public void onMachineActivated() {

	}

	@Override
	public void onMachineDeactivated() {

	}

	@Override
	public MultiblockControllerBase createNewMultiblock() {
		return new FarmController(worldObj);
	}

	@Override
	public Class<? extends MultiblockControllerBase> getMultiblockControllerType() {
		return FarmController.class;
	}

	public IFarmController getFarmController() {
		if (isConnected()) {
			return (IFarmController) super.getMultiblockController();
		} else {
			return FakeFarmController.instance;
		}
	}

	@Override
	public void isGoodForExteriorLevel(int level) throws MultiblockValidationException {
		if (level == 2 && !(this instanceof TileFarmPlain)) {
			throw new MultiblockValidationException(StatCollector.translateToLocal("for.multiblock.farm.error.needPlainBand"));
		}
	}

	@Override
	public void isGoodForInterior() throws MultiblockValidationException {
		if (!(this instanceof TileFarmPlain)) {
			throw new MultiblockValidationException(StatCollector.translateToLocal("for.multiblock.farm.error.needPlainInterior"));
		}
	}

	/* IHintSource */
	@Override
	public boolean hasHints() {
		return Config.hints.get("farm").length > 0;
	}

	@Override
	public String[] getHints() {
		return Config.hints.get("farm");
	}

	/* ISocketable */
	@Override
	public int getSocketCount() {
		return getFarmController().getSocketCount();
	}

	@Override
	public ItemStack getSocket(int slot) {
		return getFarmController().getSocket(slot);
	}

	@Override
	public void setSocket(int slot, ItemStack stack) {
		getFarmController().setSocket(slot, stack);
	}

	/* IStreamableGui */

	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		getFarmController().writeGuiData(data);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		getFarmController().readGuiData(data);
	}

	/* IErrorLogicSource */
	@Override
	public IErrorLogic getErrorLogic() {
		return getFarmController().getErrorLogic();
	}
	
	@Override
	public BlockPos getCoordinates() {
		return pos;
	}
	
	//Fields

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		
	}

	@Override
	public IChatComponent getDisplayName() {
		return null;
	}
}
