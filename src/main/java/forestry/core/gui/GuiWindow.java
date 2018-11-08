package forestry.core.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

import forestry.api.gui.IGuiElement;
import forestry.api.gui.events.GuiEvent;
import forestry.api.gui.events.GuiEventDestination;
import forestry.core.gui.elements.Window;

import org.lwjgl.input.Mouse;

/**
 * GuiScreen implementation of a gui that contains {@link forestry.api.gui.IGuiElement}s.
 */
public class GuiWindow extends GuiScreen implements IGuiSizable {
	protected final Window window;
	protected final int xSize;
	protected final int ySize;
	protected int guiLeft;
	protected int guiTop;

	public GuiWindow(int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
		this.window = new Window<>(xSize, ySize, this);
		addElements();
	}

	protected void addElements() {
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void updateScreen() {
		window.updateClient();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		window.setMousePosition(mouseX, mouseY);
		super.drawScreen(mouseX, mouseY, partialTicks);
		window.draw(mouseX, mouseY);
	}

	protected void drawTooltips(int mouseX, int mouseY) {
		InventoryPlayer playerInv = mc.player.inventory;

		if (playerInv.getItemStack().isEmpty()) {
			GuiUtil.drawToolTips(this, buttonList, mouseX, mouseY);
			GlStateManager.pushMatrix();
			GlStateManager.translate(guiLeft, guiTop, 0.0F);
			window.drawTooltip(mouseX, mouseY);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - xSize) / 2;
		this.guiTop = (this.height - ySize) / 2;
		window.init(guiLeft, guiTop);
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		window.setSize(width, height);
		super.setWorldAndResolution(mc, width, height);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if (keyCode == 1) {
			this.mc.displayGuiScreen(null);

			if (this.mc.currentScreen == null) {
				this.mc.setIngameFocus();
			}
		}
		IGuiElement origin = (window.getFocusedElement() == null) ? this.window : this.window.getFocusedElement();
		window.postEvent(new GuiEvent.KeyEvent(origin, typedChar, keyCode), GuiEventDestination.ALL);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		IGuiElement origin = (window.getMousedOverElement() == null) ? this.window : this.window.getMousedOverElement();
		window.postEvent(new GuiEvent.DownEvent(origin, mouseX, mouseY, mouseButton), GuiEventDestination.ALL);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		IGuiElement origin = (window.getMousedOverElement() == null) ? this.window : this.window.getMousedOverElement();
		window.postEvent(new GuiEvent.UpEvent(origin, mouseX, mouseY, state), GuiEventDestination.ALL);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int dWheel = Mouse.getDWheel();
		if (dWheel != 0) {
			window.postEvent(new GuiEvent.WheelEvent(window, dWheel), GuiEventDestination.ALL);
		}
	}

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
		return xSize;
	}

	@Override
	public int getSizeY() {
		return ySize;
	}

	@Override
	public Minecraft getMC() {
		return mc;
	}

}
