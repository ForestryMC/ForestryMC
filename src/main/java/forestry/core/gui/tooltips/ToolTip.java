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

import com.google.common.collect.ForwardingList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ToolTip extends ForwardingList<ToolTipLine> {

	private final List<ToolTipLine> delegate = new ArrayList<>();
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

	public boolean add(List lines) {
		boolean changed = false;
		for (Object line : lines) {
			if (line instanceof String) {
				changed |= add((String) line);
			}
		}
		return changed;
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
		if (delay == 0) {
			return true;
		}
		if (mouseOverStart == 0) {
			return false;
		}
		return System.currentTimeMillis() - mouseOverStart >= delay;
	}

	public void refresh() {
	}

}
