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

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;

import forestry.core.network.GuiId;
import forestry.core.network.IGuiHandlerEntity;
import forestry.core.network.IGuiHandlerItem;
import forestry.core.network.IGuiHandlerTile;

public class GuiHandler implements IGuiHandler {

	public static int encodeGuiData(GuiId guiId, int data) {
		return data << 8 | guiId.ordinal();
	}

	public static int decodeGuiID(int guiId) {
		return guiId & 0xFF;
	}

	public static int decodeGuiData(int guiId) {
		return guiId >> 8;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int data = decodeGuiData(id);

		if (cleanId >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[cleanId]) {
			case ItemGui: {
				ItemStack heldItem = player.getCurrentEquippedItem();
				if (heldItem != null) {
					Item item = heldItem.getItem();
					if (item instanceof IGuiHandlerItem) {
						return ((IGuiHandlerItem) item).getGui(player, heldItem, data);
					}
				}
				break;
			}
			case TileGui: {
				TileEntity tileEntity = world.getTileEntity(x, y, z);
				if (tileEntity instanceof IGuiHandlerTile) {
					return ((IGuiHandlerTile) tileEntity).getGui(player, data);
				}
				break;
			}
			case EntityGui: {
				Entity entity = world.getEntityByID(x);
				if (entity instanceof IGuiHandlerEntity) {
					return ((IGuiHandlerEntity) entity).getGui(player, data);
				}
			}
		}
		return null;
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int cleanId = decodeGuiID(id);
		int data = decodeGuiData(id);

		if (cleanId >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[cleanId]) {
			case ItemGui: {
				ItemStack heldItem = player.getCurrentEquippedItem();
				if (heldItem != null) {
					Item item = heldItem.getItem();
					if (item instanceof IGuiHandlerItem) {
						return ((IGuiHandlerItem) item).getContainer(player, heldItem, data);
					}
				}
				break;
			}
			case TileGui: {
				TileEntity tileEntity = world.getTileEntity(x, y, z);
				if (tileEntity instanceof IGuiHandlerTile) {
					return ((IGuiHandlerTile) tileEntity).getContainer(player, data);
				}
				break;
			}
			case EntityGui: {
				Entity entity = world.getEntityByID(x);
				if (entity instanceof IGuiHandlerEntity) {
					return ((IGuiHandlerEntity) entity).getContainer(player, data);
				}
			}
		}
		return null;
	}
}
