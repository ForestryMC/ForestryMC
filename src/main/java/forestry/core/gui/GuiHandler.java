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
package forestry.core.gui;

import javax.annotation.Nullable;

import forestry.api.core.ForestryAPI;
import forestry.core.tiles.TileUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {
	public static void openGui(EntityPlayer entityplayer, IGuiHandlerEntity guiHandler) {
		openGui(entityplayer, guiHandler, (short) 0);
	}

	public static void openGui(EntityPlayer entityplayer, IGuiHandlerEntity guiHandler, short data) {
		int guiData = encodeGuiData(guiHandler, data);
		entityplayer.openGui(ForestryAPI.instance, guiData, entityplayer.world, guiHandler.getIdOfEntity(), 0, 0);
	}

	public static void openGui(EntityPlayer entityplayer, IGuiHandlerItem guiHandler) {
		openGui(entityplayer, guiHandler, (short) 0);
	}

	public static void openGui(EntityPlayer entityplayer, IGuiHandlerItem guiHandler, short data) {
		int guiData = encodeGuiData(guiHandler, data);
		entityplayer.openGui(ForestryAPI.instance, guiData, entityplayer.world, 0, 0, 0);
	}

	public static void openGui(EntityPlayer entityplayer, IGuiHandlerTile guiHandler) {
		openGui(entityplayer, guiHandler, (short) 0);
	}

	public static void openGui(EntityPlayer entityplayer, IGuiHandlerTile guiHandler, short data) {
		int guiData = encodeGuiData(guiHandler, data);
		BlockPos coordinates = guiHandler.getCoordinates();
		entityplayer.openGui(ForestryAPI.instance, guiData, entityplayer.world, coordinates.getX(), coordinates.getY(), coordinates.getZ());
	}

	private static int encodeGuiData(IGuiHandlerForestry guiHandler, short data) {
		GuiId guiId = GuiIdRegistry.getGuiIdForGuiHandler(guiHandler);
		return data << 16 | guiId.getId();
	}

	@Nullable
	private static GuiId decodeGuiID(int guiData) {
		int guiId = guiData & 0xFF;
		return GuiIdRegistry.getGuiId(guiId);
	}

	private static short decodeGuiData(int guiId) {
		return (short) (guiId >> 16);
	}

	@Override
	@Nullable
	@SideOnly(Side.CLIENT)
	public Object getClientGuiElement(int guiData, EntityPlayer player, World world, int x, int y, int z) {
		GuiId guiId = decodeGuiID(guiData);
		if (guiId == null) {
			return null;
		}
		short data = decodeGuiData(guiData);
		BlockPos pos = new BlockPos(x, y, z);

		switch (guiId.getGuiType()) {
			case Item: {
				for (EnumHand hand : EnumHand.values()) {
					ItemStack heldItem = player.getHeldItem(hand);
					if (!heldItem.isEmpty()) {
						Item item = heldItem.getItem();
						if (guiId.getGuiHandlerClass().isInstance(item)) {
							return ((IGuiHandlerItem) item).getGui(player, heldItem, data);
						}
					}
				}
				break;
			}
			case Tile: {
				TileEntity tileEntity = TileUtil.getTile(world, pos);
				if (guiId.getGuiHandlerClass().isInstance(tileEntity)) {
					return ((IGuiHandlerTile) tileEntity).getGui(player, data);
				}
				break;
			}
			case Entity: {
				Entity entity = world.getEntityByID(x);
				if (guiId.getGuiHandlerClass().isInstance(entity)) {
					return ((IGuiHandlerEntity) entity).getGui(player, data);
				}
				break;
			}
		}
		return null;
	}

	@Override
	@Nullable
	public Object getServerGuiElement(int guiData, EntityPlayer player, World world, int x, int y, int z) {
		GuiId guiId = decodeGuiID(guiData);
		if (guiId == null) {
			return null;
		}
		short data = decodeGuiData(guiData);
		BlockPos pos = new BlockPos(x, y, z);

		switch (guiId.getGuiType()) {
			case Item: {
				for (EnumHand hand : EnumHand.values()) {
					ItemStack heldItem = player.getHeldItem(hand);
					if (!heldItem.isEmpty()) {
						Item item = heldItem.getItem();
						if (guiId.getGuiHandlerClass().isInstance(item)) {
							return ((IGuiHandlerItem) item).getContainer(player, heldItem, data);
						}
					}
				}
				break;
			}
			case Tile: {
				TileEntity tileEntity = TileUtil.getTile(world, pos);
				if (guiId.getGuiHandlerClass().isInstance(tileEntity)) {
					return ((IGuiHandlerTile) tileEntity).getContainer(player, data);
				}
				break;
			}
			case Entity: {
				Entity entity = world.getEntityByID(x);
				if (guiId.getGuiHandlerClass().isInstance(entity)) {
					return ((IGuiHandlerEntity) entity).getContainer(player, data);
				}
				break;
			}
		}
		return null;
	}
}
