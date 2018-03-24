package forestry.database.gui.widgets;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import forestry.api.genetics.EnumDatabaseTab;
import forestry.core.gui.Drawable;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;

public class WidgetDatabaseTabs extends Widget {
	private static final ResourceLocation CREATIVE_TEXTURE = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	private static final EnumDatabaseTab[] TABS = EnumDatabaseTab.values();

	private static final Drawable GUI_TAB = new Drawable(CREATIVE_TEXTURE, 0, 64, 28, 28);
	private static final Drawable GUI_TAB_ACTIVE_LEFT = new Drawable(CREATIVE_TEXTURE, 0, 96, 28, 32);
	private static final Drawable GUI_TAB_ACTIVE_CENTER = new Drawable(CREATIVE_TEXTURE, 28, 96, 28, 32);
	private static final Drawable GUI_TAB_ACTIVE_RIGHT = new Drawable(CREATIVE_TEXTURE, 140, 96, 28, 32);

	private final WidgetDatabaseScreen parent;
	//The space between the tabs
	public int spacing = 2;
	//The selected tab
	public int selectedTab;

	public WidgetDatabaseTabs(WidgetManager manager, int xPos, int yPos, WidgetDatabaseScreen parent) {
		super(manager, xPos, yPos);
		this.width = 28;
		this.height = 28;
		this.parent = parent;
		this.parent.setTabs(this);
		this.selectedTab = 0;
	}

	@Override
	public void draw(int startX, int startY) {
		//Drawn in the screen widget
	}

	public void drawTabs(int startX, int startY) {
		for (int tabIndex = 0; tabIndex < TABS.length; tabIndex++) {
			if (tabIndex == selectedTab) {
				continue;
			}
			EnumDatabaseTab tab = TABS[tabIndex];
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int xPos = this.xPos + tabIndex * (GUI_TAB.width + spacing);
			GUI_TAB.draw(xPos, yPos - 4);
			GuiUtil.drawItemStack(manager.gui, parent.getItemStack(tab), startX + xPos + 6, startY + yPos + 2);
		}
	}

	public void drawSelectedTab(int startX, int startY) {
		//Check if a tab is selected
		if (selectedTab < 0) {
			return;
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		EnumDatabaseTab tab = TABS[selectedTab];
		int textureIndex = selectedTab == 0 ? 0 : selectedTab == TABS.length - 1 ? 2 : 1;
		Drawable tabTexture;
		if (textureIndex == 0) {
			tabTexture = GUI_TAB_ACTIVE_LEFT;
		} else if (textureIndex == 2) {
			tabTexture = GUI_TAB_ACTIVE_RIGHT;
		} else {
			tabTexture = GUI_TAB_ACTIVE_CENTER;
		}
		int xPos = this.xPos + selectedTab * (GUI_TAB.width + spacing);
		tabTexture.draw(xPos, yPos - 4);
		GuiUtil.drawItemStack(manager.gui, parent.getItemStack(tab), startX + xPos + 6, startY + yPos + 2);
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		GuiForestry gui = manager.gui;
		//set the mouse position relative to the gui
		mouseX -= gui.getGuiLeft();
		mouseY -= gui.getGuiTop();
		int mouseOverTab = getMouseOverTab(mouseX, mouseY);
		if (mouseOverTab < 0) {
			return;
		}
		if (selectedTab != mouseOverTab) {
			selectedTab = parent.onTabChange(TABS[mouseOverTab]);
		}
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		int mouseOverTab = getMouseOverTab(mouseX, mouseY);
		return mouseOverTab >= 0;
	}

	@Nullable
	public int getMouseOverTab(int mouseX, int mouseY) {
		//set the mouse position relative to the widget
		mouseX -= xPos;
		mouseY -= yPos;
		if (mouseY < 0 || mouseY > height) {
			return -1;
		}
		int x = 0;
		for (int tabIndex = 0; tabIndex < TABS.length; tabIndex++) {
			if (mouseX < x || mouseX > x + GUI_TAB.width) {
				x += spacing + GUI_TAB.width;
				continue;
			}
			return tabIndex;
		}
		return -1;
	}

	@Nullable
	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		int mouseOverTab = getMouseOverTab(mouseX, mouseY);
		if (mouseOverTab < 0) {
			return null;
		}
		EnumDatabaseTab tab = TABS[mouseOverTab];
		ToolTip toolTip = new ToolTip();
		toolTip.add(parent.getTooltip(tab));
		return toolTip;
	}
}
