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
import java.util.List;

import com.google.common.collect.ForwardingList;

import forestry.core.utils.StringUtil;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ToolTip extends ForwardingList<ToolTipLine> {

	private final List<ToolTipLine> delegate = new ArrayList<ToolTipLine>();
	private final long delay;
	private long mouseOverStart;

	public ToolTip() {
		this.delay = 0;
	}

	public ToolTip(int delay) {
		this.delay = delay;
	}

	@Override
	protected final List<ToolTipLine> delegate() {
		return delegate;
	}

	public boolean add(String line) {
		return add(new ToolTipLine(line));
	}

	public void onTick(boolean mouseOver) {
		if (delay == 0)
			return;
		if (mouseOver) {
			if (mouseOverStart == 0)
				mouseOverStart = System.currentTimeMillis();
		} else
			mouseOverStart = 0;
	}

	public boolean isReady() {
		if (delay == 0)
			return true;
		if (mouseOverStart == 0)
			return false;
		return System.currentTimeMillis() - mouseOverStart >= delay;
	}

	public void refresh() {
	}

	public List<String> convertToStrings() {
		List<String> tips = new ArrayList<String>(size());
		for (ToolTipLine line : this) {
			tips.add(line.getText());
		}
		return tips;
	}

	public static ToolTip buildToolTip(String tipTag, String... vars) {
		ToolTip toolTip = new ToolTip(750);
		String text = StringUtil.localize(tipTag);
		for (String var : vars) {
			String[] pair = var.split("=");
			text = text.replace(pair[0], pair[1]);
		}
		String[] tips = text.split("\n");
		for (String tip : tips) {
			tip = tip.trim();
			toolTip.add(new ToolTipLine(tip));
		}
		return toolTip;
	}

}
