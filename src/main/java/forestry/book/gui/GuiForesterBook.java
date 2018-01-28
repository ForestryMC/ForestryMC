package forestry.book.gui;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.client.config.GuiUtils;

import forestry.api.book.IBookEntry;
import forestry.api.book.IForesterBook;
import forestry.book.gui.buttons.GuiButtonBack;
import forestry.book.gui.buttons.GuiButtonPage;
import forestry.core.config.Constants;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.IGuiSizable;

public abstract class GuiForesterBook extends GuiScreen implements IGuiSizable {
	public static final Comparator<IBookEntry> ENTRY_COMPARATOR = (f, s)->f.getName().compareToIgnoreCase(s.getName());
	public static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/atlas.png");
	static final int LEFT_PAGE_START_X = 16;
	static final int RIGHT_PAGE_START_X = 132;
	static final int PAGE_START_Y = 12;
	static final int LEFT_PAGE_START_Y = 25;
	static final int RIGHT_PAGE_START_Y = PAGE_START_Y;

	private static final int X_SIZE = 256;
	private static final int Y_SIZE = 181;
	@Nullable
	public static GuiForesterBook guiScreen;
	protected final IForesterBook book;
	//TODO: Add element manager
	//protected final ElementManager<GuiForesterBook> elementManager;

	protected int guiLeft;
	protected int guiTop;

	protected GuiForesterBook(IForesterBook book) {
		this.book = book;
		GuiForesterBook.guiScreen = this;

	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - X_SIZE) / 2;
		this.guiTop = (this.height - Y_SIZE) / 2;
		this.buttonList.clear();
		if(hasButtons()){
			GuiButtonPage leftButton = addButton(new GuiButtonPage(0, guiLeft + 24, guiTop + Y_SIZE - 20, true));
			GuiButtonPage rightButton = addButton(new GuiButtonPage(1, guiLeft + X_SIZE - 44, guiTop + Y_SIZE - 20, false));
			GuiButtonBack backButton = addButton(new GuiButtonBack(2, guiLeft + X_SIZE / 2 - 12, guiTop + Y_SIZE - 20));
			initButtons(leftButton, rightButton, backButton);
		}
	}

	protected boolean hasButtons(){
		return true;
	}

	protected void initButtons(GuiButtonPage leftButton, GuiButtonPage rightButton, GuiButtonBack backButton){
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		TextureManager manager = mc.renderEngine;

		manager.bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, X_SIZE, Y_SIZE);

		super.drawScreen(mouseX, mouseY, partialTicks);

		boolean unicode = fontRenderer.getUnicodeFlag();
		fontRenderer.setUnicodeFlag(true);
		drawCenteredString(fontRenderer, TextFormatting.UNDERLINE + getTitle(), guiLeft + LEFT_PAGE_START_X + 52, guiTop + PAGE_START_Y, 0xD3D3D3);

		drawText();

		fontRenderer.setUnicodeFlag(unicode);

		drawPages(mouseX, mouseY);

		InventoryPlayer playerInv = mc.player.inventory;

		if (playerInv.getItemStack().isEmpty()) {
			GuiUtil.drawToolTips(this, buttonList, mouseX, mouseY);
			List<String> tooltip = getTooltip(mouseX, mouseY);
			if(!tooltip.isEmpty()) {
				ScaledResolution scaledresolution = new ScaledResolution(mc);
				GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), -1, fontRenderer);
			}
		}
	}

	protected void drawText(){

	}

	protected void drawPages(int mouseX, int mouseY){
	}

	protected List<String> getTooltip(int mouseX, int mouseY){
		return Collections.emptyList();
	}

	protected abstract String getTitle();

	@Override
	public int getGuiLeft() {
		return guiLeft;
	}

	@Override
	public int getGuiTop() {
		return guiTop;
	}

	@Override
	public int getSizeX() {
		return X_SIZE;
	}

	@Override
	public int getSizeY() {
		return Y_SIZE;
	}

	@Override
	public Minecraft getMC() {
		return mc;
	}
}
