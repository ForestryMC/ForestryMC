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
package forestry.energy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.core.network.PacketSocketUpdate;
import forestry.core.proxy.Proxies;
import forestry.energy.gui.ContainerEngineBronze;
import forestry.energy.gui.ContainerEngineCopper;
import forestry.energy.gui.ContainerEngineTin;
import forestry.energy.gui.ContainerGenerator;
import forestry.energy.gui.GuiEngineBronze;
import forestry.energy.gui.GuiEngineCopper;
import forestry.energy.gui.GuiEngineTin;
import forestry.energy.gui.GuiGenerator;
import forestry.energy.tiles.TileEngineBiogas;
import forestry.energy.tiles.TileEngineElectric;
import forestry.energy.tiles.TileEnginePeat;
import forestry.energy.tiles.TileGenerator;

public class GuiHandlerEnergy extends GuiHandlerBase {

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {

			case EngineBiogasGUI:
				return new GuiEngineBronze(player.inventory, getTile(world, x, y, z, player, TileEngineBiogas.class));

			case EnginePeatGUI:
				return new GuiEngineCopper(player.inventory, getTile(world, x, y, z, player, TileEnginePeat.class));

			case EngineElectricGUI:
				return new GuiEngineTin(player.inventory, getTile(world, x, y, z, player, TileEngineElectric.class));

			case GeneratorGUI:
				return new GuiGenerator(player.inventory, getTile(world, x, y, z, player, TileGenerator.class));

			default:
				return null;
		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {

			case EngineBiogasGUI:
				return new ContainerEngineBronze(player.inventory, getTile(world, x, y, z, player, TileEngineBiogas.class));

			case EnginePeatGUI:
				return new ContainerEngineCopper(player.inventory, getTile(world, x, y, z, player, TileEnginePeat.class));

			case EngineElectricGUI:
				TileEngineElectric tile = getTile(world, x, y, z, player, TileEngineElectric.class);
				Proxies.net.sendToPlayer(new PacketSocketUpdate(tile), player);
				return new ContainerEngineTin(player.inventory, tile);

			case GeneratorGUI:
				return new ContainerGenerator(player.inventory, getTile(world, x, y, z, player, TileGenerator.class));

			default:
				return null;

		}
	}

}
