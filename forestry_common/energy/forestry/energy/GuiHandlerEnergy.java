/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.energy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketSocketUpdate;
import forestry.core.proxy.Proxies;
import forestry.energy.gadgets.EngineBronze;
import forestry.energy.gadgets.EngineCopper;
import forestry.energy.gadgets.EngineTin;
import forestry.energy.gadgets.MachineGenerator;
import forestry.energy.gui.ContainerEngineBronze;
import forestry.energy.gui.ContainerEngineCopper;
import forestry.energy.gui.ContainerEngineTin;
import forestry.energy.gui.ContainerGenerator;
import forestry.energy.gui.GuiEngineBronze;
import forestry.energy.gui.GuiEngineCopper;
import forestry.energy.gui.GuiEngineTin;
import forestry.energy.gui.GuiGenerator;

public class GuiHandlerEnergy extends GuiHandlerBase {

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		switch (GuiId.values()[id]) {

		case EngineBronzeGUI:
			return new GuiEngineBronze(player.inventory, (EngineBronze) getTileForestry(world, x, y, z));

		case EngineCopperGUI:
			return new GuiEngineCopper(player.inventory, (EngineCopper) getTileForestry(world, x, y, z));

		case EngineTinGUI:
			return new GuiEngineTin(player.inventory, (EngineTin) getTileForestry(world, x, y, z));

		case GeneratorGUI:
			return new GuiGenerator(player.inventory, (MachineGenerator) getTileForestry(world, x, y, z));

		default:
			return null;
		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		switch (GuiId.values()[id]) {

		case EngineBronzeGUI:
			return new ContainerEngineBronze(player.inventory, (EngineBronze) getTileForestry(world, x, y, z));

		case EngineCopperGUI:
			return new ContainerEngineCopper(player.inventory, (EngineCopper) getTileForestry(world, x, y, z));

		case EngineTinGUI:
			EngineTin tile = (EngineTin) getTileForestry(world, x, y, z);
			Proxies.net.sendToPlayer(new PacketSocketUpdate(PacketIds.SOCKET_UPDATE, x, y, z, tile), player);
			return new ContainerEngineTin(player.inventory, tile);

		case GeneratorGUI:
			return new ContainerGenerator(player.inventory, (MachineGenerator) getTileForestry(world, x, y, z));

		default:
			return null;

		}
	}

}
