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
package forestry.apiculture.network.packets;

import java.io.IOException;

import de.nedelosk.modularmachines.api.modular.ModularManager;
import de.nedelosk.modularmachines.api.modular.handlers.IModularHandlerTileEntity;
import de.nedelosk.modularmachines.api.modules.handlers.IModuleContentHandler;
import de.nedelosk.modularmachines.api.modules.state.IModuleState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.apiculture.BeekeepingLogic;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.core.network.packets.PacketCoordinates;
import forestry.core.proxy.Proxies;

public class PacketBeeLogicActiveModule extends PacketCoordinates implements IForestryPacketClient {
	private BeekeepingLogic beekeepingLogic;
	private int index;

	public PacketBeeLogicActiveModule() {
	}

	public PacketBeeLogicActiveModule(IBeeHousing beeHousing) {
		super(beeHousing.getCoordinates());
		this.beekeepingLogic = (BeekeepingLogic) beeHousing.getBeekeepingLogic();
		this.index = ((IModuleContentHandler)beeHousing).getModuleState().getIndex();
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.BEE_LOGIC_ACTIVE_MODULE;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeInt(index);
		beekeepingLogic.writeData(data);
	}
	
	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		index = data.readInt();
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		IModularHandlerTileEntity handler = (IModularHandlerTileEntity) tile.getCapability(ModularManager.MODULAR_HANDLER_CAPABILITY, null);
		IModuleState state = handler.getModular().getModule(index);
		IBeeHousing beeHousing = state.getContentHandler(IBeeHousing.class);
		IBeekeepingLogic beekeepingLogic = beeHousing.getBeekeepingLogic();
		if (beekeepingLogic instanceof BeekeepingLogic) {
			((BeekeepingLogic) beekeepingLogic).readData(data);
		}
	}
}
