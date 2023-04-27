/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package forestry.core.recipes.nei;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.PositionedStack;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;

public abstract class RecipeHandlerBase extends TemplateRecipeHandler implements IRecipeHandlerBase {

    @Override
    public void prepare() {}

    public abstract class CachedBaseRecipe extends CachedRecipe {

        public List<PositionedFluidTank> getFluidTanks() {
            List<PositionedFluidTank> tanks = new ArrayList<>();
            PositionedFluidTank tank = this.getFluidTank();
            if (tank != null) {
                tanks.add(tank);
            }
            return tanks;
        }

        public PositionedFluidTank getFluidTank() {
            return null;
        }
    }

    public abstract String getRecipeID();

    public void changeToGuiTexture() {
        GuiDraw.changeTexture(this.getGuiTexture());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void addTransferRect(int x, int y, int width, int height) {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(x, y, width, height), this.getRecipeID()));
    }

    @Override
    public void drawForeground(int recipe) {
        super.drawForeground(recipe);
        this.drawFluidTanks(recipe);
        this.changeToGuiTexture();
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("liquid") && results[0] instanceof FluidStack
                && ((FluidStack) results[0]).getFluid() != null) {
            this.loadCraftingRecipes((FluidStack) results[0]);
        } else if (outputId.equals(this.getRecipeID())) {
            this.loadAllRecipes();
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadAllRecipes() {}

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        FluidStack fluid = NEIUtils.getFluidStack(result);
        if (fluid != null && fluid.getFluid() != null) {
            this.loadCraftingRecipes(fluid);
        }
    }

    public void loadCraftingRecipes(FluidStack result) {}

    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (inputId.equals("liquid") && ingredients[0] instanceof FluidStack
                && ((FluidStack) ingredients[0]).getFluid() != null) {
            this.loadUsageRecipes((FluidStack) ingredients[0]);
        } else {
            super.loadUsageRecipes(inputId, ingredients);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingred) {
        FluidStack fluid = NEIUtils.getFluidStack(ingred);
        if (fluid != null && fluid.getFluid() != null) {
            this.loadUsageRecipes(fluid);
        }
    }

    public void loadUsageRecipes(FluidStack ingredient) {}

    @Override
    public List<String> handleTooltip(GuiRecipe<?> guiRecipe, List<String> currenttip, int recipe) {
        super.handleTooltip(guiRecipe, currenttip, recipe);
        CachedBaseRecipe crecipe = (CachedBaseRecipe) this.arecipes.get(recipe);
        if (GuiContainerManager.shouldShowTooltip(guiRecipe)) {
            Point mouse = GuiDraw.getMousePosition();
            Point offset = guiRecipe.getRecipePosition(recipe);
            Point relMouse = new Point(
                    mouse.x - (guiRecipe.width - 176) / 2 - offset.x,
                    mouse.y - (guiRecipe.height - 166) / 2 - offset.y);

            currenttip = this.provideTooltip(guiRecipe, currenttip, crecipe, relMouse);
        }
        return currenttip;
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe<?> guiRecipe, ItemStack itemStack, List<String> currenttip,
            int recipe) {
        super.handleItemTooltip(guiRecipe, itemStack, currenttip, recipe);
        CachedBaseRecipe crecipe = (CachedBaseRecipe) this.arecipes.get(recipe);
        Point mouse = GuiDraw.getMousePosition();
        Point offset = guiRecipe.getRecipePosition(recipe);
        Point relMouse = new Point(
                mouse.x - (guiRecipe.width - 176) / 2 - offset.x,
                mouse.y - (guiRecipe.height - 166) / 2 - offset.y);

        currenttip = this.provideItemTooltip(guiRecipe, itemStack, currenttip, crecipe, relMouse);
        return currenttip;
    }

    public List<String> provideTooltip(GuiRecipe guiRecipe, List<String> currenttip, CachedBaseRecipe crecipe,
            Point relMouse) {
        if (crecipe.getFluidTanks() != null) {
            for (PositionedFluidTank tank : crecipe.getFluidTanks()) {
                if (tank.position.contains(relMouse)) {
                    tank.handleTooltip(currenttip);
                }
            }
        }
        return currenttip;
    }

    public List<String> provideItemTooltip(GuiRecipe guiRecipe, ItemStack itemStack, List<String> currenttip,
            CachedBaseRecipe crecipe, Point relMouse) {
        for (PositionedStack stack : crecipe.getIngredients()) {
            if (stack instanceof PositionedStackAdv && ((PositionedStackAdv) stack).getRect().contains(relMouse)) {
                currenttip = ((PositionedStackAdv) stack).handleTooltip(guiRecipe, currenttip);
            }
        }
        for (PositionedStack stack : crecipe.getOtherStacks()) {
            if (stack instanceof PositionedStackAdv && ((PositionedStackAdv) stack).getRect().contains(relMouse)) {
                currenttip = ((PositionedStackAdv) stack).handleTooltip(guiRecipe, currenttip);
            }
        }
        PositionedStack stack = crecipe.getResult();
        if (stack instanceof PositionedStackAdv && ((PositionedStackAdv) stack).getRect().contains(relMouse)) {
            currenttip = ((PositionedStackAdv) stack).handleTooltip(guiRecipe, currenttip);
        }
        return currenttip;
    }

    @Override
    public boolean keyTyped(GuiRecipe gui, char keyChar, int keyCode, int recipe) {
        if (keyCode == NEIClientConfig.getKeyBinding("gui.recipe")) {
            if (this.transferFluidTank(gui, recipe, false)) {
                return true;
            }
        } else if (keyCode == NEIClientConfig.getKeyBinding("gui.usage")) {
            if (this.transferFluidTank(gui, recipe, true)) {
                return true;
            }
        }
        return super.keyTyped(gui, keyChar, keyCode, recipe);
    }

    @Override
    public boolean mouseClicked(GuiRecipe gui, int button, int recipe) {
        if (button == 0) {
            if (this.transferFluidTank(gui, recipe, false)) {
                return true;
            }
        } else if (button == 1) {
            if (this.transferFluidTank(gui, recipe, true)) {
                return true;
            }
        }
        return super.mouseClicked(gui, button, recipe);
    }

    protected boolean transferFluidTank(GuiRecipe guiRecipe, int recipe, boolean usage) {
        CachedBaseRecipe crecipe = (CachedBaseRecipe) this.arecipes.get(recipe);
        Point mousepos = GuiDraw.getMousePosition();
        Point offset = guiRecipe.getRecipePosition(recipe);
        Point relMouse = new Point(
                mousepos.x - (guiRecipe.width - 176) / 2 - offset.x,
                mousepos.y - (guiRecipe.height - 166) / 2 - offset.y);

        if (crecipe.getFluidTanks() != null) {
            for (PositionedFluidTank tank : crecipe.getFluidTanks()) {
                if (tank.position.contains(relMouse)) {
                    return tank.transfer(usage);
                }
            }
        }

        return false;
    }

    public void drawFluidTanks(int recipe) {
        CachedRecipe cachedRecipe = this.arecipes.get(recipe);
        if (cachedRecipe instanceof CachedBaseRecipe) {
            CachedBaseRecipe crecipe = (CachedBaseRecipe) cachedRecipe;
            if (crecipe.getFluidTanks() != null) {
                for (PositionedFluidTank fluidTank : crecipe.getFluidTanks()) {
                    fluidTank.draw();
                }
            }
        }
    }

    @Override
    public String getOverlayIdentifier() {
        return this.getRecipeID();
    }
}
