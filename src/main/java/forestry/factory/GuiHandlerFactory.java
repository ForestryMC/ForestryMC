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
package forestry.factory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.factory.gadgets.MachineBottler;
import forestry.factory.gadgets.MachineCarpenter;
import forestry.factory.gadgets.MachineCentrifuge;
import forestry.factory.gadgets.MachineFabricator;
import forestry.factory.gadgets.MachineFermenter;
import forestry.factory.gadgets.MachineMoistener;
import forestry.factory.gadgets.MachineRaintank;
import forestry.factory.gadgets.MachineSqueezer;
import forestry.factory.gadgets.MachineStill;
import forestry.factory.gadgets.TileWorktable;
import forestry.factory.gui.ContainerBottler;
import forestry.factory.gui.ContainerCarpenter;
import forestry.factory.gui.ContainerCentrifuge;
import forestry.factory.gui.ContainerFabricator;
import forestry.factory.gui.ContainerFermenter;
import forestry.factory.gui.ContainerMoistener;
import forestry.factory.gui.ContainerRaintank;
import forestry.factory.gui.ContainerSqueezer;
import forestry.factory.gui.ContainerStill;
import forestry.factory.gui.ContainerWorktable;
import forestry.factory.gui.GuiBottler;
import forestry.factory.gui.GuiCarpenter;
import forestry.factory.gui.GuiCentrifuge;
import forestry.factory.gui.GuiFabricator;
import forestry.factory.gui.GuiFermenter;
import forestry.factory.gui.GuiMoistener;
import forestry.factory.gui.GuiRaintank;
import forestry.factory.gui.GuiSqueezer;
import forestry.factory.gui.GuiStill;
import forestry.factory.gui.GuiWorktable;

public class GuiHandlerFactory extends GuiHandlerBase {

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {

			case BottlerGUI:
				return new GuiBottler(player.inventory, getTile(world, x, y, z, player, MachineBottler.class));

			case CarpenterGUI:
				return new GuiCarpenter(player.inventory, getTile(world, x, y, z, player, MachineCarpenter.class));

			case CentrifugeGUI:
				return new GuiCentrifuge(player.inventory, getTile(world, x, y, z, player, MachineCentrifuge.class));

			case FabricatorGUI:
				return new GuiFabricator(player.inventory, getTile(world, x, y, z, player, MachineFabricator.class));

			case FermenterGUI:
				return new GuiFermenter(player.inventory, getTile(world, x, y, z, player, MachineFermenter.class));

			case MoistenerGUI:
				return new GuiMoistener(player.inventory, getTile(world, x, y, z, player, MachineMoistener.class));

			case RaintankGUI:
				return new GuiRaintank(player.inventory, getTile(world, x, y, z, player, MachineRaintank.class));

			case SqueezerGUI:
				return new GuiSqueezer(player.inventory, getTile(world, x, y, z, player, MachineSqueezer.class));

			case StillGUI:
				return new GuiStill(player.inventory, getTile(world, x, y, z, player, MachineStill.class));

			case WorktableGUI:
				return new GuiWorktable(player, getTile(world, x, y, z, player, TileWorktable.class));
			
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

			case BottlerGUI:
				return new ContainerBottler(player.inventory, getTile(world, x, y, z, player, MachineBottler.class));

			case CarpenterGUI:
				return new ContainerCarpenter(player.inventory, getTile(world, x, y, z, player, MachineCarpenter.class));

			case CentrifugeGUI:
				return new ContainerCentrifuge(player.inventory, getTile(world, x, y, z, player, MachineCentrifuge.class));

			case FabricatorGUI:
				return new ContainerFabricator(player.inventory, getTile(world, x, y, z, player, MachineFabricator.class));

			case FermenterGUI:
				return new ContainerFermenter(player.inventory, getTile(world, x, y, z, player, MachineFermenter.class));

			case MoistenerGUI:
				return new ContainerMoistener(player.inventory, getTile(world, x, y, z, player, MachineMoistener.class));

			case RaintankGUI:
				return new ContainerRaintank(player.inventory, getTile(world, x, y, z, player, MachineRaintank.class));

			case SqueezerGUI:
				return new ContainerSqueezer(player.inventory, getTile(world, x, y, z, player, MachineSqueezer.class));

			case StillGUI:
				return new ContainerStill(player.inventory, getTile(world, x, y, z, player, MachineStill.class));

			case WorktableGUI:
				return new ContainerWorktable(player, getTile(world, x, y, z, player, TileWorktable.class));
			
			default:
				return null;

		}
	}

}
