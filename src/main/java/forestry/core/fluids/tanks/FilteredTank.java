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
package forestry.core.fluids.tanks;

import forestry.core.gui.tooltips.ToolTipLine;
import net.minecraft.item.EnumRarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FilteredTank extends StandardTank {

    private final Fluid filter;

    public FilteredTank(int capacity, Fluid filter) {
        this(capacity, filter, null);
    }

    public FilteredTank(int capacity, Fluid filter, TileEntity tile) {
        super(capacity, tile);
        this.filter = filter;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (liquidMatchesFilter(resource))
            return super.fill(resource, doFill);
        return 0;
    }

    public Fluid getFilter() {
        return filter;
    }

    public boolean liquidMatchesFilter(FluidStack resource) {
        if (resource == null || filter == null)
            return false;
        return resource.getFluid() == filter;
    }

    @Override
    protected void refreshTooltip() {
        toolTip.clear();
        int amount = 0;
        if (filter != null) {
            EnumRarity rarity = filter.getRarity();
            if (rarity == null)
                rarity = EnumRarity.common;
            ToolTipLine name = new ToolTipLine(filter.getLocalizedName(), rarity.rarityColor);
            name.setSpacing(2);
            toolTip.add(name);
            if (getFluid() != null)
                amount = getFluid().amount;
        }
        toolTip.add(new ToolTipLine(String.format("%,d", amount) + " / " + String.format("%,d", getCapacity())));
    }

}
