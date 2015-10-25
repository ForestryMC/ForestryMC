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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.gadgets.TileNaturalistChest;
import forestry.core.gui.ContainerNaturalistInventory;
import forestry.core.gui.GuiNaturalistInventory;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketGuiUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Utils;

public abstract class GuiHandlerBase implements IGuiHandler {

	protected static <T extends TileEntity> T getTile(World world, BlockPos pos, EntityPlayer player,
			Class<T> tileClass) {
		T tileForestry = Utils.getTile(world, pos, tileClass);

		if (tileForestry instanceof IStreamableGui && !world.isRemote) {
			PacketGuiUpdate packet = new PacketGuiUpdate((IStreamableGui) tileForestry);
			Proxies.net.sendToPlayer(packet, player);
		}

		return tileForestry;
	}

	protected GuiNaturalistInventory getNaturalistChestGui(String rootUID, EntityPlayer player, World world,
			BlockPos pos, int page) {
		TileNaturalistChest tile = getTile(world, pos, player, TileNaturalistChest.class);
		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(rootUID);
		return new GuiNaturalistInventory(speciesRoot, player,
				new ContainerNaturalistInventory(player.inventory, tile, page), tile, page, 5);
	}

	protected ContainerNaturalistInventory getNaturalistChestContainer(String rootUID, EntityPlayer player, World world,
			BlockPos pos, int page) {
		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(rootUID);
		speciesRoot.getBreedingTracker(world, player.getGameProfile()).synchToPlayer(player);
		return new ContainerNaturalistInventory(player.inventory,
				getTile(world, pos, player, TileNaturalistChest.class), page);
	}

	public static int encodeGuiData(int guiId, int data) {
		return data << 8 | guiId;
	}

	protected static int decodeGuiID(int guiId) {
		return guiId & 0xFF;
	}

	protected static int decodeGuiData(int guiId) {
		return guiId >> 8;
	}
}
