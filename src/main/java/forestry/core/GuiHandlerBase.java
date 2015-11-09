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

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.genetics.ISpeciesRoot;
import forestry.core.gui.ContainerNaturalistInventory;
import forestry.core.gui.GuiNaturalistInventory;
import forestry.core.network.GuiId;
import forestry.core.tiles.TileNaturalistChest;
import forestry.core.tiles.TileUtil;

public abstract class GuiHandlerBase implements IGuiHandler {

	@Override
	public abstract Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z);

	@SideOnly(Side.CLIENT)
	@Override
	public abstract Gui getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z);

	protected GuiNaturalistInventory getNaturalistChestGui(ISpeciesRoot speciesRoot, EntityPlayer player, World world, int x, int y, int z, int page) {
		TileNaturalistChest tile = TileUtil.getTile(world, x, y, z, TileNaturalistChest.class);
		return new GuiNaturalistInventory(speciesRoot, player, new ContainerNaturalistInventory(player.inventory, tile, page), tile, page, 5);
	}

	protected ContainerNaturalistInventory getNaturalistChestContainer(ISpeciesRoot speciesRoot, EntityPlayer player, World world, int x, int y, int z, int page) {
		speciesRoot.getBreedingTracker(world, player.getGameProfile()).synchToPlayer(player);
		return new ContainerNaturalistInventory(player.inventory, TileUtil.getTile(world, x, y, z, TileNaturalistChest.class), page);
	}

	public static int encodeGuiData(GuiId guiId, int data) {
		return data << 8 | guiId.ordinal();
	}

	protected static int decodeGuiID(int guiId) {
		return guiId & 0xFF;
	}

	protected static int decodeGuiData(int guiId) {
		return guiId >> 8;
	}
}
