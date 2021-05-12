package forestry.core.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.glfw.GLFW;

import forestry.core.gui.elements.WindowGui;

/**
 * GuiScreen implementation of a gui that contains {@link forestry.core.gui.elements.GuiElement}s.
 */
@OnlyIn(Dist.CLIENT)
public class GuiWindow extends Screen implements IGuiSizable {
	protected final WindowGui window;
	protected final int xSize;
	protected final int ySize;
	protected int guiLeft;
	protected int guiTop;

	public GuiWindow(int xSize, int ySize, ITextComponent title) {
		super(title);
		this.xSize = xSize;
		this.ySize = ySize;
		this.window = new WindowGui<>(xSize, ySize, this);
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

	@Override
	public void render(MatrixStack transform, int mouseX, int mouseY, float partialTicks) {
		window.setMousePosition(mouseX, mouseY);
		super.render(transform, mouseX, mouseY, partialTicks);
		window.draw(transform, mouseX, mouseY);
	}

	protected void drawTooltips(MatrixStack transform, int mouseY, int mouseX) {
		PlayerInventory playerInv = minecraft.player.inventory;

		if (playerInv.getCarried().isEmpty()) {
			GuiUtil.drawToolTips(transform, this, children, mouseX, mouseY);
			RenderSystem.pushMatrix();
			RenderSystem.translatef(guiLeft, guiTop, 0.0F);
			window.drawTooltip(transform, mouseX, mouseY);
			RenderSystem.popMatrix();
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
	public void mouseMoved(double mouseX, double mouseY) {
		window.onMouseMove(mouseX, mouseY);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double oldMouseX, double oldMouseY) {
		if (window.onMouseDrag(mouseX, mouseY)) {
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, mouseButton, oldMouseX, oldMouseY);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (key == GLFW.GLFW_KEY_ESCAPE) {    //TODO - keybinds?
			this.minecraft.setScreen(null);

			if (this.minecraft.screen == null) {
				this.minecraft.setWindowActive(true);
			}
		}
		if (window.onKeyPressed(key, scanCode, modifiers)) {
			return true;
		}
		return super.keyPressed(key, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int key, int scanCode, int modifiers) {
		if (window.onKeyPressed(key, scanCode, modifiers)) {
			return true;
		}
		return super.keyReleased(key, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (window.onCharTyped(codePoint, modifiers)) {
			return true;
		}
		return super.charTyped(codePoint, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (window.onMouseClicked(mouseX, mouseY, mouseButton)) {
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (window.onMouseReleased(mouseX, mouseY, mouseButton)) {
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double deltaWheel) {
		if (deltaWheel != 0 && window.onMouseScrolled(mouseX, mouseY, deltaWheel)) {
			return true;

		}
		return super.mouseScrolled(mouseX, mouseY, deltaWheel);
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
		return minecraft;
	}

}
