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

import net.minecraft.entity.player.EntityPlayer;

import forestry.api.core.EnumCamouflageType;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.ledgers.ClimateLedger;
import forestry.core.gui.ledgers.Ledger;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.render.TextureManager;
import forestry.core.utils.Translator;
import forestry.greenhouse.gui.widgets.WidgetCamouflageSlot;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.tiles.TileGreenhouse;

public class GuiGreenhouse extends GuiForestryTitled<ContainerGreenhouse, TileGreenhouse> {

	public GuiGreenhouse(EntityPlayer player, TileGreenhouse tile) {
		super(Constants.TEXTURE_PATH_GUI + "/greenhouse.png", new ContainerGreenhouse(player.inventory, tile), tile);

		//Add the water tank
		widgetManager.add(new TankWidget(widgetManager, 152, 16, 0).setOverlayOrigin(176, 0));
		
		//Add the multiblock camouflage slots
		widgetManager.add(new WidgetCamouflageSlot(widgetManager, 8, 16, inventory.getMultiblockLogic().getController(), EnumCamouflageType.DEFAULT));
		widgetManager.add(new WidgetCamouflageSlot(widgetManager, 8, 37, inventory.getMultiblockLogic().getController(), EnumCamouflageType.GLASS));
		//widgetManager.add(new WidgetCamouflageSlot(widgetManager, 8, 58, inventory.getMultiblockLogic().getController(), EnumCamouflageType.DOOR));
		
		//Add the tile camouflage slots
		widgetManager.add(new WidgetCamouflageSlot(widgetManager, 35, 37, inventory, tile.getCamouflageType()));
	}

	@Override
	protected void addLedgers() {
		IGreenhouseControllerInternal greenhouseController = inventory.getMultiblockLogic().getController();
		
		ledgerManager.add(new ClimateLedger(ledgerManager, greenhouseController));
		ledgerManager.add(new EnergyLedger());
		super.addLedgers();
	}
	
	protected class EnergyLedger extends Ledger {

		public EnergyLedger() {
			super(ledgerManager, "power");
			maxHeight = 48;
		}

		@Override
		public void draw(int x, int y) {

			// Draw background
			drawBackground(x, y);

			// Draw icon
			drawSprite(TextureManager.getInstance().getDefault("misc/energy"), x + 3, y + 4);

			if (!isFullyOpened()) {
				return;
			}

			drawHeader(Translator.translateToLocal("for.gui.energy"), x + 22, y + 8);

			drawSubheader(Translator.translateToLocal("for.gui.stored") + ':', x + 22, y + 20);
			drawText(inventory.getMultiblockLogic().getController().getEnergyManager().getEnergyStored(null) + " RF", x + 22, y + 32);
		}

		@Override
		public String getTooltip() {
			return Translator.translateToLocal("for.gui.energy") + ": " + inventory.getMultiblockLogic().getController().getEnergyManager().getEnergyStored(null) + " RF/t";
		}
	}
}
