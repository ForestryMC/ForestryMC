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
package forestry.apiculture.gui;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.core.config.Constants;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.GuiAnalyzerProvider;
import forestry.core.gui.slots.SlotWatched;
import forestry.core.render.EnumTankLevel;

public class GuiBeeHousing<C extends ContainerForestry & IContainerBeeHousing> extends GuiAnalyzerProvider<C> {
	private final IGuiBeeHousingDelegate delegate;

	public enum Icon {
		APIARY("/apiary.png"),
		BEE_HOUSE("/alveary.png");

		private final String path;

		Icon(String path) {
			this.path = path;
		}
	}

	public GuiBeeHousing(IGuiBeeHousingDelegate delegate, C container, Icon icon) {
		super(Constants.TEXTURE_PATH_GUI + icon.path, container, delegate, 25, 7, 2, 0);
		this.delegate = delegate;
		this.ySize = 190;

		for (int i = 0; i < 2; i++) {
			Slot queenSlot = container.getForestrySlot(1 + i);
			if (queenSlot instanceof SlotWatched) {
				SlotWatched watched = (SlotWatched) queenSlot;
				watched.setChangeWatcher(this);
			}
		}
		analyzer.init();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		bindTexture(textureFile);
		drawHealthMeter(guiLeft + 20, guiTop + 37, delegate.getHealthScaled(46), EnumTankLevel.rateTankLevel(delegate.getHealthScaled(100)));
	}

	@Override
	protected void drawSelectedSlot(int selectedSlot) {
		Slot slot = container.getForestrySlot(1 + selectedSlot);
		if (slot != null) {
			SELECTED_COMB_SLOT.draw(guiLeft + slot.xPos - 3, guiTop + slot.yPos - 3);
		}
	}

	private void drawHealthMeter(int x, int y, int height, EnumTankLevel rated) {
		int i = 176 + rated.getLevelScaled(16);
		int k = 0;

		this.drawTexturedModalRect(x, y + 46 - height, i, k + 46 - height, 4, height);
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(delegate);
		addClimateLedger(delegate);
		addHintLedger(delegate.getHintKey());
		addOwnerLedger(delegate);
	}

	@Override
	public ItemStack getSpecimen(int index) {
		Slot slot = container.getForestrySlot(getSelectedSlot(index));
		return slot.getStack();
	}

	@Override
	protected boolean hasErrors() {
		return delegate.getErrorLogic().hasErrors();
	}

	@Override
	public boolean onUpdateSelected() {
		return false;
	}

	@Override
	public void onSelection(int index, boolean changed) {

	}
}
