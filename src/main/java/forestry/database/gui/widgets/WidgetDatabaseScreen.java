package forestry.database.gui.widgets;

import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import forestry.api.genetics.EnumDatabaseTab;
import forestry.api.genetics.IDatabaseTab;
import forestry.core.config.Constants;
import forestry.core.gui.elements.GuiElementHelper;
import forestry.core.gui.elements.GuiElementScrollable;
import forestry.core.gui.widgets.WidgetElementProvider;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.gui.widgets.WidgetScrollBar;
import forestry.core.utils.SoundUtil;
import forestry.core.utils.Translator;
import forestry.database.DatabaseScreenLogic;

public class WidgetDatabaseScreen extends WidgetElementProvider {
	public static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/database_mutation_screen.png");

	private WidgetDatabaseTabs tabs;
	public WidgetScrollBar scrollBar;
	public GuiElementHelper layoutHelper;
	public DatabaseScreenLogic logic;
	public DatabaseScreenLogic.ScreenState state;

	public WidgetDatabaseScreen(WidgetManager manager, int xPos, int yPos, WidgetScrollBar scrollBar) {
		super(manager, xPos, yPos, 118, 177, new GuiElementScrollable(0, 0, 102, 157));
		this.scrollable.setXOffset(8);
		this.scrollable.setYOffset(12);
		this.scrollBar = scrollBar;
		this.logic = new DatabaseScreenLogic();
		this.state = DatabaseScreenLogic.ScreenState.NO_INDIVIDUAL;
	}

	public void setTabs(WidgetDatabaseTabs tabs){
		this.tabs = tabs;
	}

	public int onTabChange(EnumDatabaseTab tab){
		state = logic.onTabChange(logic.tabs[tab.ordinal()]);
		update();
		if(state == DatabaseScreenLogic.ScreenState.SUCCESS){
			SoundUtil.playButtonClick();
			return logic.selectedTab.getTab().ordinal();
		}
		return -1;
	}

	public void onItemChange(ItemStack itemStack){
		state = logic.onItemChange(itemStack);
		update();
		if(state == DatabaseScreenLogic.ScreenState.SUCCESS){
			tabs.selectedTab = logic.selectedTab.getTab().ordinal();
		}else {
			tabs.selectedTab = -1;
		}
	}

	public ItemStack getItemStack(EnumDatabaseTab tab){
		if(state == DatabaseScreenLogic.ScreenState.SUCCESS){
			return logic.databasePlugin.getTabDatabaseIconItem(tab);
		}
		return new ItemStack(Items.PAPER);
	}

	public String getTooltip(EnumDatabaseTab tab){
		if(state == DatabaseScreenLogic.ScreenState.SUCCESS){
			String tooltip = logic.tabs[tab.ordinal()].getTooltip(logic.individual);
			if(tooltip != null) {
				return tooltip;
			}
		}
		return Translator.translateToLocal("for.gui.database.tab." + tab.toString().toLowerCase(Locale.ENGLISH) + ".name");
	}

	private void update(){
		//reset list and layout helper
		scrollable.clear();
		scrollBar.setVisible(false);
		scrollable.updateVisibleElements(0);
		layoutHelper = new GuiElementHelper(scrollable);
		if(state == DatabaseScreenLogic.ScreenState.SUCCESS){
			IDatabaseTab selectedTab = logic.selectedTab;
			selectedTab.createElements(layoutHelper, logic.individual, logic.itemStack);
			int invisibleElements = scrollable.getInvisibleElementCount();
			if (invisibleElements > 0) {
				scrollBar.setParameters(this, 0, invisibleElements, 1);
				scrollBar.setVisible(true);
				//scrollBar.setValue(0);
			}else{
				scrollBar.setValue(0);
			}
			scrollable.updateVisibleElements(scrollBar.getValue());
		}else{
			GuiElementHelper layoutHelper = new GuiElementHelper(scrollable);
			FontRenderer fontRenderer = manager.gui.getFontRenderer();
			String key ="for.gui.portablealyzer.help";
			if(state == DatabaseScreenLogic.ScreenState.NO_PLUGIN){
				key = "for.gui.database.support";
			}
			List<String> lines = fontRenderer.listFormattedStringToWidth(Translator.translateToLocal(key), width - 10);
			for(String text : lines) {
				layoutHelper.addText(2, text, -1);
			}
			scrollable.updateVisibleElements(0);
		}
	}

	@Override
	public void draw(int startX, int startY) {
		tabs.drawTabs(startX, startY);
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(TEXTURE);
		manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, 0, 0, width, height);

		//Draw the selected tab after the background so it is drawn over it
		tabs.drawSelectedTab(startX, startY);
		super.draw(startX, startY);
	}

	public int size(){
		return scrollable.getSize();
	}
}
