package forestry.core.gui.elements;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import com.mojang.blaze3d.platform.GlStateManager;

import forestry.api.genetics.IDatabasePlugin;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.events.GuiEvent;
import forestry.core.genetics.analyzer.AnalyzerTab;
import forestry.core.gui.Drawable;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.elements.layouts.VerticalLayout;
import forestry.core.utils.SoundUtil;

public class GeneticAnalyzerTabs extends VerticalLayout {
	private static final Drawable SELECTED_BACKGROUND = new Drawable(GeneticAnalyzer.TEXTURE, 0, 166, 35, 26);
	private static final Drawable UNSELECTED_BACKGROUND = new Drawable(GeneticAnalyzer.TEXTURE, 0, 192, 35, 26);
	@Nullable
	public IDatabasePlugin databasePlugin;
	public final GeneticAnalyzer analyzer;
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
		public void drawElement(int mouseX, int mouseY) {
			if (!isVisible()) {
				return;
			}
			int x = 0;
			Drawable background = selected == index ? SELECTED_BACKGROUND : UNSELECTED_BACKGROUND;
			if (selected != index) {
				x += 2;
			}
			background.draw(x, 0);
			if (!displayStack.isEmpty()) {
				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.enableRescaleNormal();
				GuiUtil.drawItemStack(Minecraft.getInstance().fontRenderer, displayStack, x + 9, 5);
				RenderHelper.disableStandardItemLighting();
			}
		}
	}

}
