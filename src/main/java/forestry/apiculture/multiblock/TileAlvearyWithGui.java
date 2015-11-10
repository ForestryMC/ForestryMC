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
package forestry.apiculture.multiblock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.core.ForestryAPI;
import forestry.core.access.AccessHandler;
import forestry.core.access.EnumAccess;
import forestry.core.access.IAccessHandler;
import forestry.core.access.IRestrictedAccess;
import forestry.core.network.GuiId;
import forestry.core.tiles.ITitled;

public abstract class TileAlvearyWithGui extends TileAlveary implements ITitled, IRestrictedAccess {

	private final AccessHandler accessHandler;
	private final String unlocalizedTitle;
	private final GuiId guiId;

	protected TileAlvearyWithGui(int meta, GuiId guiId) {
		this.accessHandler = new AccessHandler(this);
		this.unlocalizedTitle = "tile.for.alveary." + meta + ".name";
		this.guiId = guiId;
	}

	@Override
	public final IAccessHandler getAccessHandler() {
		return accessHandler;
	}

	@Override
	public void onSwitchAccess(EnumAccess oldAccess, EnumAccess newAccess) {
		if (oldAccess == EnumAccess.SHARED || newAccess == EnumAccess.SHARED) {
			// pipes connected to this need to update
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, blockType);
			markDirty();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		accessHandler.writeToNBT(data);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		accessHandler.readFromNBT(data);
	}

	@Override
	public final String getUnlocalizedTitle() {
		return unlocalizedTitle;
	}

	@Override
	public final void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, guiId.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}
}
