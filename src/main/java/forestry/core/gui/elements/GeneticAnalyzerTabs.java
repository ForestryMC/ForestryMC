package forestry.core.gui.elements;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;

import forestry.api.genetics.gatgets.IDatabasePlugin;
import forestry.api.genetics.gatgets.IDatabaseTab;
import forestry.core.genetics.analyzer.AnalyzerTab;
import forestry.core.gui.Drawable;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.elements.layouts.ContainerElement;
import forestry.core.gui.elements.layouts.FlexLayout;
import forestry.core.utils.SoundUtil;

public class GeneticAnalyzerTabs extends ContainerElement {
	private static final Drawable SELECTED_BACKGROUND = new Drawable(GeneticAnalyzer.TEXTURE, 0, 166, 35, 26);
	private static final Drawable UNSELECTED_BACKGROUND = new Drawable(GeneticAnalyzer.TEXTURE, 0, 192, 35, 26);
	@Nullable
	public IDatabasePlugin<?> databasePlugin;
	public final GeneticAnalyzer analyzer;
	private int selected = 0;

	public GeneticAnalyzerTabs(GeneticAnalyzer analyzer) {
		setSize(35, UNKNOWN_HEIGHT);
		setLayout(FlexLayout.vertical(2));
		this.analyzer = analyzer;
		IDatabaseTab<?>[] tabs = getTabs();
		for (int i = 0; i < 4; i++) {
			IDatabaseTab<?> tab = tabs.length > i ? tabs[i] : null;
			add(new Tab(i, tab));
		}
	}

	private void select(int index) {
		this.selected = index;
	}

	public IDatabaseTab<?> getSelected() {
		GuiElement element = elements.get(selected);
		if (!(element instanceof Tab) || !element.isVisible()) {
			return AnalyzerTab.ANALYZE;
		}
		return ((Tab) element).tab;
	}

	public void setPlugin(@Nullable IDatabasePlugin<?> plugin) {
		if (databasePlugin != plugin) {
			this.selected = 0;
			this.databasePlugin = plugin;
			IDatabaseTab<?>[] tabs = getTabs();
			for (int i = 0; i < elements.size(); i++) {
				GuiElement element = elements.get(i);
				if (element instanceof Tab) {
					IDatabaseTab<?> tab = tabs.length > i ? tabs[i] : null;
					Tab tabElement = (Tab) element;
					tabElement.setTab(tab);
				}
			}
		}
	}

	private IDatabaseTab<?>[] getTabs() {
		if (databasePlugin == null) {
			return new IDatabaseTab[]{AnalyzerTab.ANALYZE};
		}
		return databasePlugin.getTabs();
	}

	private class Tab extends GuiElement {
		private final int index;
		@Nullable
		public IDatabaseTab<?> tab;
		private ItemStack displayStack = ItemStack.EMPTY;

		public Tab(int index, @Nullable IDatabaseTab<?> tab) {
			setSize(35, 26);
			setTab(tab);
			this.index = index;
		}

		@Override
		public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
			if (!isVisible()) {
				return false;
			}
			select(index);
			SoundUtil.playButtonClick();
			analyzer.update();
			return true;
		}

		@Override
		public boolean canMouseOver() {
			return true;
		}

		@Override
		public boolean isVisible() {
			return tab != null;
		}

		public void setTab(@Nullable IDatabaseTab<?> tab) {
			this.tab = tab;
			if (tab != null) {
				this.displayStack = tab.getIconStack();
			} else {
				this.displayStack = ItemStack.EMPTY;
			}
		}

		@Override
		public void drawElement(PoseStack transform, int mouseX, int mouseY) {
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
				// GlStateManager._enableRescaleNormal();
				GuiUtil.drawItemStack(Minecraft.getInstance().font, displayStack, x + 9, 5);
				// Lighting.turnOff();
			}
		}
	}

}
