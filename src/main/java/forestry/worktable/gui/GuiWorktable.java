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
package forestry.worktable.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.buttons.GuiBetterButton;
import forestry.core.gui.buttons.StandardButtonTextureSets;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.SoundUtil;
import forestry.worktable.gui.widgets.ClearWorktable;
import forestry.worktable.gui.widgets.MemorizedRecipeSlot;
import forestry.worktable.recipes.RecipeMemory;
import forestry.worktable.tiles.TileWorktable;

public class GuiWorktable extends GuiForestryTitled<ContainerWorktable> {
	private static final int SPACING = 18;

	private final TileWorktable tile;
	private boolean hasRecipeConflict = false;

	public GuiWorktable(ContainerWorktable container, PlayerInventory inv, ITextComponent name) {
		super(Constants.TEXTURE_PATH_GUI + "/worktable2.png", container, inv, container.getTile());
		this.tile = container.getTile();

		this.ySize = 218;

		RecipeMemory recipeMemory = tile.getMemory();

		int slot = 0;
		for (int y = 0; y < 3; y++) {
			int yPos = 20 + y * SPACING;
			for (int x = 0; x < 3; x++) {
				int xPos = 110 + x * SPACING;
				MemorizedRecipeSlot memorizedRecipeSlot = new MemorizedRecipeSlot(widgetManager, xPos, yPos, recipeMemory, slot++);
				widgetManager.add(memorizedRecipeSlot);
			}
		}

		widgetManager.add(new ClearWorktable(widgetManager, 66, 19));
	}

	@Override
	public void tick() {
		super.tick();

		if (hasRecipeConflict != tile.hasRecipeConflict()) {
			hasRecipeConflict = tile.hasRecipeConflict();
			if (hasRecipeConflict) {
				addButtons();
			} else {
				buttons.clear();
			}
		}
	}

	private void addButtons() {
		buttons.add(new GuiBetterButton(guiLeft + 76, guiTop + 56, StandardButtonTextureSets.LEFT_BUTTON_SMALL, b -> {
			NetworkUtil.sendToServer(new PacketGuiSelectRequest(100, 0));
			SoundUtil.playButtonClick();
		}));
		buttons.add(new GuiBetterButton(guiLeft + 85, guiTop + 56, StandardButtonTextureSets.RIGHT_BUTTON_SMALL, b -> {
			NetworkUtil.sendToServer(new PacketGuiSelectRequest(101, 0));
			SoundUtil.playButtonClick();
		}));
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addHintLedger("worktable");
	}
}
