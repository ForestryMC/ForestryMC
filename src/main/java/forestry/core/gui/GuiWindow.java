package forestry.core.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.platform.GlStateManager;

import forestry.api.gui.IGuiElement;
import forestry.api.gui.events.GuiEvent;
import forestry.api.gui.events.GuiEventDestination;
import forestry.core.gui.elements.Window;

import org.lwjgl.glfw.GLFW;

/**
 * GuiScreen implementation of a gui that contains {@link forestry.api.gui.IGuiElement}s.
 */
public class GuiWindow extends Screen implements IGuiSizable {
	protected final Window window;
	protected final int xSize;
	protected final int ySize;
	protected int guiLeft;
	protected int guiTop;

	public GuiWindow(int xSize, int ySize, ITextComponent title) {
		super(title);
		this.xSize = xSize;
		this.ySize = ySize;
		this.window = new Window<>(xSize, ySize, this);
		addElements();
	}

	protected void addElements() {
	}

	//TODO right method?
	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void tick() {
		window.updateClient();
	}

	//TODO - right method?
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		window.setMousePosition(mouseX, mouseY);
		super.render(mouseX, mouseY, partialTicks);
		window.draw(mouseX, mouseY);
	}

	protected void drawTooltips(int mouseX, int mouseY) {
		PlayerInventory playerInv = minecraft.player.inventory;

		if (playerInv.getItemStack().isEmpty()) {
			GuiUtil.drawToolTips(this, buttons, mouseX, mouseY);
			GlStateManager.pushMatrix();
			GlStateManager.translatef(guiLeft, guiTop, 0.0F);
			window.drawTooltip(mouseX, mouseY);
			GlStateManager.popMatrix();
		}
	}

	//TODO check right method
	@Override
	public void init() {
		super.init();
		this.guiLeft = (this.width - xSize) / 2;
		this.guiTop = (this.height - ySize) / 2;
		window.init(guiLeft, guiTop);
	}

	@Override
	public void init(Minecraft mc, int width, int height) {
		window.setSize(width, height);
		super.init(mc, width, height);
	}

	@Override
	//	protected void keyTyped(char typedChar, int keyCode) {
	//TODO these are the params I belive
	public boolean keyPressed(int keyCode, int scanCode, int mods) {    //TODO resolve this method
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {    //TODO - keybinds?
			this.minecraft.displayGuiScreen(null);

			if (this.minecraft.currentScreen == null) {
				this.minecraft.setGameFocused(true);
			}
		}
		IGuiElement origin = (window.getFocusedElement() == null) ? this.window : this.window.getFocusedElement();
		window.postEvent(new GuiEvent.KeyEvent(origin, keyCode, scanCode), GuiEventDestination.ALL);
		return true; //TODO return type
	}

	//TODO onMouseClicked
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		IGuiElement origin = (window.getMousedOverElement() == null) ? this.window : this.window.getMousedOverElement();
		window.postEvent(new GuiEvent.DownEvent(origin, mouseX, mouseY, mouseButton), GuiEventDestination.ALL);
		return true; //TODO return type
	}

	//TODO onMouseRelease
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		IGuiElement origin = (window.getMousedOverElement() == null) ? this.window : this.window.getMousedOverElement();
		window.postEvent(new GuiEvent.UpEvent(origin, mouseX, mouseY, state), GuiEventDestination.ALL);
		//TODO return type
		return true;
	}

	@Override
	public boolean mouseScrolled(double x, double y, double w) {
		super.mouseScrolled(x, y, w);
		if (w != 0) {
			window.postEvent(new GuiEvent.WheelEvent(window, w), GuiEventDestination.ALL);

		}
		return true;
	}

	//TODO above is how to do dwheel? maybe?
	//	@Override
	//	public void handleMouseInput() {
	//		super.handleMouseInput();
	//		int dWheel = Mouse.getDWheel();
	//		if (dWheel != 0) {
	//			window.postEvent(new GuiEvent.WheelEvent(window, dWheel), GuiEventDestination.ALL);
	//		}
	//	}

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
		return minecraft;
	}

}
