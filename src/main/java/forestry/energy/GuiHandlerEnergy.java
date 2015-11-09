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

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.core.network.PacketSocketUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileUtil;
import forestry.energy.gui.ContainerEngineBiogas;
import forestry.energy.gui.ContainerEngineElectric;
import forestry.energy.gui.ContainerEnginePeat;
import forestry.energy.gui.ContainerGenerator;
import forestry.energy.gui.GuiEngineBiogas;
import forestry.energy.gui.GuiEngineElectric;
import forestry.energy.gui.GuiEnginePeat;
import forestry.energy.gui.GuiGenerator;
import forestry.energy.tiles.TileEngineBiogas;
import forestry.energy.tiles.TileEngineElectric;
import forestry.energy.tiles.TileEnginePeat;
import forestry.energy.tiles.TileGenerator;

public class GuiHandlerEnergy extends GuiHandlerBase {

	@SideOnly(Side.CLIENT)
	@Override
	public Gui getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {

			case EngineBiogasGUI:
				return new GuiEngineBiogas(player.inventory, TileUtil.getTile(world, x, y, z, TileEngineBiogas.class));

			case EnginePeatGUI:
				return new GuiEnginePeat(player.inventory, TileUtil.getTile(world, x, y, z, TileEnginePeat.class));

			case EngineElectricGUI:
				return new GuiEngineElectric(player.inventory, TileUtil.getTile(world, x, y, z, TileEngineElectric.class));

			case GeneratorGUI:
				return new GuiGenerator(player.inventory, TileUtil.getTile(world, x, y, z, TileGenerator.class));

			default:
				return null;
		}
	}

	@Override
	public Container getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {

			case EngineBiogasGUI:
				return new ContainerEngineBiogas(player.inventory, TileUtil.getTile(world, x, y, z, TileEngineBiogas.class));

			case EnginePeatGUI:
				return new ContainerEnginePeat(player.inventory, TileUtil.getTile(world, x, y, z, TileEnginePeat.class));

			case EngineElectricGUI:
				TileEngineElectric tile = TileUtil.getTile(world, x, y, z, TileEngineElectric.class);
				Proxies.net.sendToPlayer(new PacketSocketUpdate(tile), player);
				return new ContainerEngineElectric(player.inventory, tile);

			case GeneratorGUI:
				return new ContainerGenerator(player.inventory, TileUtil.getTile(world, x, y, z, TileGenerator.class));

			default:
				return null;

		}
	}

}
