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
package forestry.core.gui.ledgers;

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;

import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.Translator;

public class HintLedger extends Ledger {

	private final Component hintString;
	private final Component hintTooltip;

	public HintLedger(LedgerManager manager, List<String> hints) {
		super(manager, "hint");
		int position = new Random().nextInt(hints.size());
		String hint = hints.get(position);

		hintString = Component.translatable("for.hints." + hint + ".desc");
		hintTooltip = Component.translatable("for.hints." + hint + ".tag");

		Minecraft minecraft = Minecraft.getInstance();
		Font fontRenderer = minecraft.font;
		//TODO text component
		int lineCount = fontRenderer.split(hintString, maxTextWidth).size();
		maxHeight = (lineCount + 1) * fontRenderer.lineHeight + 20;
	}

	@Override
	public void draw(PoseStack transform, int y, int x) {

		// Draw background
		drawBackground(transform, y, x);

		// Draw icon
		drawSprite(transform, TextureManagerForestry.getInstance().getDefault("misc/hint"), x + 3, y + 4);

		if (!isFullyOpened()) {
			return;
		}

		//TODO textcomponent
		drawHeader(transform, Translator.translateToLocal("for.gui.didyouknow") + '?', x + 22, y + 8);
		drawSplitText(transform, hintString.getString(), x + 12, y + 20, maxTextWidth);
	}

	@Override
	public Component getTooltip() {
		return hintTooltip;
	}
}
