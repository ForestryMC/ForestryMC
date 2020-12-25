package forestry.core.gui.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import forestry.api.genetics.gatgets.IDatabasePlugin;
import forestry.api.genetics.gatgets.IDatabaseTab;
import forestry.core.genetics.analyzer.AnalyzerTab;
import forestry.core.gui.Drawable;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.elements.layouts.VerticalLayout;
import forestry.core.gui.elements.lib.IGuiElement;
import forestry.core.gui.elements.lib.events.GuiEvent;
import forestry.core.utils.SoundUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class GeneticAnalyzerTabs extends VerticalLayout {
    private static final Drawable SELECTED_BACKGROUND = new Drawable(GeneticAnalyzer.TEXTURE, 0, 166, 35, 26);
    private static final Drawable UNSELECTED_BACKGROUND = new Drawable(GeneticAnalyzer.TEXTURE, 0, 192, 35, 26);
    public final GeneticAnalyzer analyzer;
    @Nullable
    public IDatabasePlugin databasePlugin;
    private int selected = 0;

    public GeneticAnalyzerTabs(int xPos, int yPos, GeneticAnalyzer analyzer) {
        super(xPos, yPos, 35);
        setDistance(2);
        this.analyzer = analyzer;
        IDatabaseTab[] tabs = getTabs();
        for (int i = 0; i < 4; i++) {
            IDatabaseTab tab = tabs.length > i ? tabs[i] : null;
            Tab element = new Tab(0, 0, i);
            element.setTab(tab);
            add(element);
        }
    }

    private void select(int index) {
        this.selected = index;
    }

    public IDatabaseTab getSelected() {
        IGuiElement element = elements.get(selected);
        if (!(element instanceof Tab) || !element.isVisible()) {
            return AnalyzerTab.ANALYZE;
        }
        return ((Tab) element).tab;
    }

    public void setPlugin(@Nullable IDatabasePlugin plugin) {
        if (databasePlugin != plugin) {
            this.selected = 0;
            this.databasePlugin = plugin;
            IDatabaseTab[] tabs = getTabs();
            for (int i = 0; i < elements.size(); i++) {
                IGuiElement element = elements.get(i);
                if (element instanceof Tab) {
                    IDatabaseTab tab = tabs.length > i ? tabs[i] : null;
                    Tab tabElement = (Tab) element;
                    tabElement.setTab(tab);
                }
            }
        }
    }

    private IDatabaseTab[] getTabs() {
        if (databasePlugin == null) {
            return new IDatabaseTab[]{AnalyzerTab.ANALYZE};
        }
        return databasePlugin.getTabs();
    }

    private class Tab extends GuiElement {
        private final int index;
        @Nullable
        public IDatabaseTab tab;
        private ItemStack displayStack = ItemStack.EMPTY;

        public Tab(int xPos, int yPos, int index) {
            super(xPos, yPos, 35, 26);
            this.index = index;
            addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
                if (isVisible()) {
                    select(index);
                    SoundUtil.playButtonClick();
                    analyzer.update();
                }
            });
        }

        @Override
        public boolean canMouseOver() {
            return true;
        }

        public boolean isVisible() {
            return tab != null;
        }

        public void setTab(@Nullable IDatabaseTab tab) {
            this.tab = tab;
            if (tab != null) {
                this.displayStack = tab.getIconStack();
            } else {
                this.displayStack = ItemStack.EMPTY;
            }
        }

        @Override
        public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
            if (!isVisible()) {
                return;
            }
            int x = 0;
            Drawable background = selected == index ? SELECTED_BACKGROUND : UNSELECTED_BACKGROUND;
            if (selected != index) {
                x += 2;
            }
            background.draw(transform, 0, x);
            if (!displayStack.isEmpty()) {
                //RenderHelper.enableGUIStandardItemLighting(); TODO Gui Light
                GlStateManager.enableRescaleNormal();
                GuiUtil.drawItemStack(Minecraft.getInstance().fontRenderer, displayStack, x + 9, 5);
                RenderHelper.disableStandardItemLighting();
            }
        }
    }

}
