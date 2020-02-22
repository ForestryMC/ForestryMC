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
package forestry.core.gui.tooltips;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@OnlyIn(Dist.CLIENT)
public class ToolTip {

	private final List<ITextComponent> lines = new ArrayList<>();
	private final long delay;
	private long mouseOverStart;

	public ToolTip() {
		this.delay = 0;
	}

	public ToolTip(int delay) {
		this.delay = delay;
	}

	public void clear() {
		lines.clear();
	}

	//TODO - only for porting, remove when cleaning up
	@Deprecated
	public boolean add(String s) {
		return this.add(new StringTextComponent(s));
	}

	public boolean add(ITextComponent line) {
		return lines.add(line);
	}

	public boolean add(ITextComponent line, TextFormatting format) {
		Style style = new Style();
		style.setColor(format);
		return add(line, style);
	}

	public boolean add(ITextComponent line, Style style) {
		line.setStyle(style);
		return lines.add(line);
	}

	public boolean add(List<ITextComponent> lines) {
		boolean changed = false;
		for (ITextComponent line : lines) {
			changed |= add(line);
		}
		return changed;
	}

	public List<ITextComponent> getLines() {
		return Collections.unmodifiableList(lines);
	}

	public void onTick(boolean mouseOver) {
		if (delay == 0) {
			return;
		}
		if (mouseOver) {
			if (mouseOverStart == 0) {
				mouseOverStart = System.currentTimeMillis();
			}
		} else {
			mouseOverStart = 0;
		}
	}

	public boolean isReady() {
		return delay == 0 || mouseOverStart != 0 && System.currentTimeMillis() - mouseOverStart >= delay;
	}

	public void refresh() {
	}

}
