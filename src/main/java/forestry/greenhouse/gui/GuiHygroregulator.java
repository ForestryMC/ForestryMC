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
package forestry.greenhouse.gui;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.config.Constants;
import forestry.core.gui.ContainerSocketedHelper;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.IContainerSocketed;
import forestry.core.gui.widgets.SocketWidget;
import forestry.core.gui.widgets.TankWidget;
import forestry.greenhouse.gui.widgets.WidgetCamouflageSlot;
import forestry.greenhouse.tiles.TileHygroregulator;

public class GuiHygroregulator extends GuiForestryTitled<ContainerHygroregulator> implements IContainerSocketed {
	private final TileHygroregulator tile;
	private final ContainerSocketedHelper<TileHygroregulator> helper;

	public GuiHygroregulator(InventoryPlayer inventory, TileHygroregulator tile) {
		super(Constants.TEXTURE_PATH_GUI + "/greenhouse_hygroregulator.png", new ContainerHygroregulator(inventory, tile), tile);
		this.tile = tile;

		widgetManager.add(new TankWidget(this.widgetManager, 104, 17, 0));
		//Add the camouflage slot
		widgetManager.add(new WidgetCamouflageSlot(widgetManager, guiLeft + 152, guiTop + 38, tile));

		widgetManager.add(new SocketWidget(widgetManager, guiLeft + 9, guiTop + 38, tile, 0));
		this.helper = new ContainerSocketedHelper<>(tile);
	}

	/**
	 * {@link net.minecraft.block.Block#isFullBlock(IBlockState)}
	 *
	 * @param slot
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void handleChipsetClick(int slot) {
		helper.handleChipsetClick(slot);
	}

	@Override
	public void handleChipsetClickServer(int slot, EntityPlayerMP player, ItemStack itemstack) {
		helper.handleChipsetClickServer(slot, player, itemstack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleSolderingIronClick(int slot) {
		helper.handleSolderingIronClick(slot);
	}

	@Override
	public void handleSolderingIronClickServer(int slot, EntityPlayerMP player, ItemStack itemstack) {
		helper.handleSolderingIronClickServer(slot, player, itemstack);
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
	}
}
