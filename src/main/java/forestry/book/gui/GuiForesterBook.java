package forestry.book.gui;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.book.IForesterBook;
import forestry.book.gui.buttons.GuiButtonBack;
import forestry.book.gui.buttons.GuiButtonPage;
import forestry.core.config.Constants;
import forestry.core.gui.GuiWindow;
import forestry.core.gui.IGuiSizable;

@SideOnly(Side.CLIENT)
public abstract class GuiForesterBook extends GuiWindow implements IGuiSizable {
	public static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/almanac/almanac.png");
	static final int LEFT_PAGE_START_X = 16;
	static final int RIGHT_PAGE_START_X = 132;
	static final int PAGE_START_Y = 12;
	static final int LEFT_PAGE_START_Y = 25;
	static final int RIGHT_PAGE_START_Y = PAGE_START_Y;
	public static final int PAGE_WIDTH = 108;
	public static final int PAGE_HEIGHT = 155;

	private static final int X_SIZE = 256;
	private static final int Y_SIZE = 181;
	@Nullable
	private static GuiForesterBook guiScreen;
	protected final IForesterBook book;

	protected GuiForesterBook(IForesterBook book) {
		super(X_SIZE, Y_SIZE);
		this.book = book;
		setGuiScreen(this);
	}

	public IForesterBook getBook() {
		return book;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.clear();
		if (hasButtons()) {
			GuiButtonPage leftButton = addButton(new GuiButtonPage(0, guiLeft + 24, guiTop + Y_SIZE - 20, true));
			GuiButtonPage rightButton = addButton(new GuiButtonPage(1, guiLeft + X_SIZE - 44, guiTop + Y_SIZE - 20, false));
			GuiButtonBack backButton = addButton(new GuiButtonBack(2, guiLeft + X_SIZE / 2 - 12, guiTop + Y_SIZE - 20));
			initButtons(leftButton, rightButton, backButton);
		}
	}

	protected boolean hasButtons() {
		return true;
	}

	protected void initButtons(GuiButtonPage leftButton, GuiButtonPage rightButton, GuiButtonBack backButton) {
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

		drawTooltips(mouseX, mouseY);
	}

	@Override
	protected void drawTooltips(int mouseX, int mouseY) {
		super.drawTooltips(mouseX, mouseY);
		InventoryPlayer playerInv = mc.player.inventory;

		if (playerInv.getItemStack().isEmpty()) {
			List<String> tooltip = getTooltip(mouseX, mouseY);
			if (!tooltip.isEmpty()) {
				ScaledResolution scaledresolution = new ScaledResolution(mc);
				GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), -1, fontRenderer);
			}
		}
	}

	protected void drawText() {
	}

	protected List<String> getTooltip(int mouseX, int mouseY) {
		return Collections.emptyList();
	}

	protected abstract String getTitle();

	@Nullable
	public static GuiForesterBook getGuiScreen() {
		return guiScreen;
	}

	public static void setGuiScreen(@Nullable GuiForesterBook guiScreen) {
		GuiForesterBook.guiScreen = guiScreen;
	}
}
