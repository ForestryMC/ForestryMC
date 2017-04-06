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
package forestry.core.network.packets;

import java.io.IOException;
import de.nedelosk.modularmachines.api.modular.ModularManager;
import de.nedelosk.modularmachines.api.modular.handlers.IModularHandlerTileEntity;
import de.nedelosk.modularmachines.api.modules.IModule;
import de.nedelosk.modularmachines.api.modules.handlers.IModulePage;
import de.nedelosk.modularmachines.api.modules.state.IModuleState;
import forestry.api.core.ILocatable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketIdClient;
import forestry.core.proxy.Proxies;

public class PacketGuiUpdateModule extends PacketCoordinates implements IForestryPacketClient {

	private IStreamableGui streamableGui;
	private String pageID = null;
	private int index;

	public PacketGuiUpdateModule() {
	}

	public <T extends IStreamableGui & ILocatable> PacketGuiUpdateModule(IStreamableGui streamableGui, IModuleState state) {
		super(((IModularHandlerTileEntity)state.getModular().getHandler()).getPos());
		this.streamableGui = streamableGui;
		this.index = state.getIndex();
		if(streamableGui instanceof IModulePage){
			pageID = ((IModulePage)streamableGui).getPageID();
		}
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeInt(index);
		data.writeBoolean(pageID != null);
		if(pageID != null){
			data.writeUTF(pageID);
		}
		streamableGui.writeGuiData(data);
	}
	
	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		index = data.readInt();
		if(data.readBoolean()){
			pageID = data.readUTF();
		}
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		TileEntity tile = getTarget(Proxies.common.getRenderWorld());
		IModularHandlerTileEntity handler = (IModularHandlerTileEntity) tile.getCapability(ModularManager.MODULAR_HANDLER_CAPABILITY, null);
		IModuleState state = handler.getModular().getModule(index);
		if(pageID != null){
			IModulePage page = state.getPage(pageID);
			if (page instanceof IStreamableGui) {
				((IStreamableGui) page).readGuiData(data);
			}
		}else{
			IModule module = state.getModule();
			if (module instanceof IStreamableGui) {
				((IStreamableGui) module).readGuiData(data);
			}
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.GUI_UPDATE_MODULE;
	}
}
