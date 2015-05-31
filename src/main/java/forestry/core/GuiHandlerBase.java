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
package forestry.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.gadgets.TileForestry;
import forestry.core.gadgets.TileNaturalistChest;
import forestry.core.gui.ContainerNaturalistInventory;
import forestry.core.gui.GuiNaturalistInventory;
import forestry.core.network.PacketTileGuiOpened;
import forestry.core.proxy.Proxies;

public abstract class GuiHandlerBase implements IGuiHandler {

	public <T extends TileForestry> T getTileForestry(World world, int x, int y, int z, Class<T> tileClass) {
		T tileForestry = null;
		try {
			tileForestry = tileClass.cast(world.getTileEntity(x, y, z));
		} catch (ClassCastException ex) {
			Proxies.log.warning("Failed to cast a tile entity to a TileForestry at " + x + '/' + y + '/' + z);
		}

		if (tileForestry != null && !world.isRemote) {
			Proxies.net.sendNetworkPacket(new PacketTileGuiOpened(tileForestry));
		}

		return tileForestry;
	}

	public GuiNaturalistInventory getNaturalistChestGui(String rootUID, EntityPlayer player, World world, int x, int y, int z, int page) {
		TileNaturalistChest tile = getTileForestry(world, x, y, z, TileNaturalistChest.class);
		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(rootUID);
		return new GuiNaturalistInventory(speciesRoot, player, new ContainerNaturalistInventory(player.inventory, tile, page, 25), tile, page, 5);
	}

	public ContainerNaturalistInventory getNaturalistChestContainer(String rootUID, EntityPlayer player, World world, int x, int y, int z, int page) {
		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(rootUID);
		speciesRoot.getBreedingTracker(world, player.getGameProfile()).synchToPlayer(player);
		return new ContainerNaturalistInventory(player.inventory, getTileForestry(world, x, y, z, TileNaturalistChest.class), page, 25);
	}

	public static int encodeGuiData(int guiId, int data) {
		return data << 8 | guiId;
	}

	public static int decodeGuiID(int guiId) {
		return guiId & 0xFF;
	}

	public static int decodeGuiData(int guiId) {
		return guiId >> 8;
	}
}
