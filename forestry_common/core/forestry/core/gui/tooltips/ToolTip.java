/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui.tooltips;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ForwardingList;

import forestry.core.utils.Localization;

/**
 *
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
            tips.add(line.text);
        }
        return tips;
    }

    public static ToolTip buildToolTip(String tipTag, String... vars) {
        if (!Localization.instance.hasMapping(tipTag))
            return null;
		ToolTip toolTip = new ToolTip(750);
		String text = Localization.instance.get(tipTag);
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
