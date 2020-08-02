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
package forestry.core.fluids;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import forestry.api.core.tooltips.ToolTip;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FilteredTank extends StandardTank {

    private final Set<ResourceLocation> filters = new HashSet<>(); // FluidNames

    public FilteredTank(int capacity) {
        super(capacity);
        setValidator(this::fluidMatchesFilter);
    }

    public FilteredTank(int capacity, boolean canFill, boolean canDrain) {
        super(capacity, canFill, canDrain);
    }

    public FilteredTank setFilters(Fluid... filters) {
        return setFilters(Arrays.asList(filters));
    }

    public FilteredTank setFilters(Collection<Fluid> filters) {
        this.filters.clear();
        for (Fluid fluid : filters) {
            this.filters.add(fluid.getRegistryName());
        }
        return this;
    }

    private boolean fluidMatchesFilter(FluidStack resource) {
        return resource.getFluid() != Fluids.EMPTY && filters.contains(resource.getFluid().getRegistryName());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void refreshTooltip() {
        if (hasFluid()) {
            super.refreshTooltip();
            return;
        }

        ToolTip toolTip = getToolTip();
        toolTip.clear();
        if (Screen.hasShiftDown() || filters.size() < 5) {
            for (ResourceLocation filterName : filters) {
                Fluid fluidFilter = ForgeRegistries.FLUIDS.getValue(filterName);
                FluidAttributes attributes = fluidFilter.getAttributes();
                Rarity rarity = attributes.getRarity();
                if (rarity == null) {
                    rarity = Rarity.COMMON;
                }
                FluidStack filterFluidStack = new FluidStack(fluidFilter, 1);
                toolTip.add(filterFluidStack.getDisplayName(), rarity.color);
            }
        } else {
            //TODO can this be simplified
            ITextComponent tmiComponent = new StringTextComponent("<")
                    .append(new TranslationTextComponent("for.gui.tooltip.tmi"))
                    .append(new StringTextComponent(">"));
            toolTip.add(tmiComponent, TextFormatting.ITALIC);
        }
        toolTip.add(new TranslationTextComponent("for.gui.tooltip.liquid.amount", getFluidAmount(), getCapacity()));
    }

}
