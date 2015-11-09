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

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.core.tiles.TileUtil;
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
import forestry.factory.tiles.TileBottler;
import forestry.factory.tiles.TileCarpenter;
import forestry.factory.tiles.TileCentrifuge;
import forestry.factory.tiles.TileFabricator;
import forestry.factory.tiles.TileFermenter;
import forestry.factory.tiles.TileMoistener;
import forestry.factory.tiles.TileRaintank;
import forestry.factory.tiles.TileSqueezer;
import forestry.factory.tiles.TileStill;
import forestry.factory.tiles.TileWorktable;

public class GuiHandlerFactory extends GuiHandlerBase {

	@SideOnly(Side.CLIENT)
	@Override
	public Gui getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {

			case BottlerGUI:
				return new GuiBottler(player.inventory, TileUtil.getTile(world, x, y, z, TileBottler.class));

			case CarpenterGUI:
				return new GuiCarpenter(player.inventory, TileUtil.getTile(world, x, y, z, TileCarpenter.class));

			case CentrifugeGUI:
				return new GuiCentrifuge(player.inventory, TileUtil.getTile(world, x, y, z, TileCentrifuge.class));

			case FabricatorGUI:
				return new GuiFabricator(player.inventory, TileUtil.getTile(world, x, y, z, TileFabricator.class));

			case FermenterGUI:
				return new GuiFermenter(player.inventory, TileUtil.getTile(world, x, y, z, TileFermenter.class));

			case MoistenerGUI:
				return new GuiMoistener(player.inventory, TileUtil.getTile(world, x, y, z, TileMoistener.class));

			case RaintankGUI:
				return new GuiRaintank(player.inventory, TileUtil.getTile(world, x, y, z, TileRaintank.class));

			case SqueezerGUI:
				return new GuiSqueezer(player.inventory, TileUtil.getTile(world, x, y, z, TileSqueezer.class));

			case StillGUI:
				return new GuiStill(player.inventory, TileUtil.getTile(world, x, y, z, TileStill.class));

			case WorktableGUI:
				return new GuiWorktable(player, TileUtil.getTile(world, x, y, z, TileWorktable.class));
			
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

			case BottlerGUI:
				return new ContainerBottler(player.inventory, TileUtil.getTile(world, x, y, z, TileBottler.class));

			case CarpenterGUI:
				return new ContainerCarpenter(player.inventory, TileUtil.getTile(world, x, y, z, TileCarpenter.class));

			case CentrifugeGUI:
				return new ContainerCentrifuge(player.inventory, TileUtil.getTile(world, x, y, z, TileCentrifuge.class));

			case FabricatorGUI:
				return new ContainerFabricator(player.inventory, TileUtil.getTile(world, x, y, z, TileFabricator.class));

			case FermenterGUI:
				return new ContainerFermenter(player.inventory, TileUtil.getTile(world, x, y, z, TileFermenter.class));

			case MoistenerGUI:
				return new ContainerMoistener(player.inventory, TileUtil.getTile(world, x, y, z, TileMoistener.class));

			case RaintankGUI:
				return new ContainerRaintank(player.inventory, TileUtil.getTile(world, x, y, z, TileRaintank.class));

			case SqueezerGUI:
				return new ContainerSqueezer(player.inventory, TileUtil.getTile(world, x, y, z, TileSqueezer.class));

			case StillGUI:
				return new ContainerStill(player.inventory, TileUtil.getTile(world, x, y, z, TileStill.class));

			case WorktableGUI:
				return new ContainerWorktable(player, TileUtil.getTile(world, x, y, z, TileWorktable.class));
			
			default:
				return null;

		}
	}

}
