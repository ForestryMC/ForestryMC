/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.IGuiHandler;
import forestry.core.network.GuiId;
import forestry.pipes.gui.ContainerPropolisPipe;
import forestry.pipes.gui.GuiPropolisPipe;
import forestry.plugins.PluginApiculture;

public class GuiHandlerPipes implements IGuiHandler {

	private Pipe getPipe(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile == null)
			return null;

		if (!(tile instanceof TileGenericPipe))
			return null;

		return ((TileGenericPipe) tile).pipe;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		switch (GuiId.values()[id]) {
		case PropolisPipeGUI:
			return new GuiPropolisPipe(player, (PipeItemsPropolis)getPipe(world, x, y, z));
		default:
			return null;

		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		switch (GuiId.values()[id]) {
		case PropolisPipeGUI:
			PluginApiculture.beeInterface.getBreedingTracker(world, player.getGameProfile()).synchToPlayer(player);
			return new ContainerPropolisPipe(player.inventory, (PipeItemsPropolis)getPipe(world, x, y, z));
		default:
			return null;

		}
	}

}
