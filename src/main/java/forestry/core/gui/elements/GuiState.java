package forestry.core.gui.elements;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureManager;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiState;
import forestry.core.gui.IGuiSizable;

@SideOnly(Side.CLIENT)
public class GuiState<G extends GuiScreen & IGuiSizable> implements IGuiState {
	private final G gui;
	private int mouseX = -1;
	private int mouseY = -1;
	private int mouseButton = -1;
	@Nullable
	private Minecraft mc = null;

	public GuiState(G gui) {
		this.gui = gui;
	}

	public int getMouseX() {
		return mouseX;
	}

	public void setMouseX(int mouseX) {
		this.mouseX = mouseX;
	}

	@Override
	public int getRelativeMouseX(IGuiElement element) {
		return mouseX - element.getAbsoluteX();
	}

	public int getMouseY() {
		return mouseY;
	}

	public void setMouseY(int mouseY) {
		this.mouseY = mouseY;
	}

	@Override
	public int getRelativeMouseY(IGuiElement element) {
		return mouseX - element.getAbsoluteY();
	}

	public int getScreenWidth() {
		return gui.width;
	}

	public int getScreenHeight() {
		return gui.height;
	}

	public int getGuiLeft() {
		return gui.getGuiLeft();
	}

	public int getGuiTop() {
		return gui.getGuiTop();
	}

	@Override
	public int getGuiHeight() {
		return gui.getSizeX();
	}

	@Override
	public int getGuiWidth() {
		return gui.getSizeY();
	}

	public Minecraft getMinecraft() {
		if (mc == null) {
			mc = Minecraft.getMinecraft();
		}
		return mc;
	}

	@Override
	public TextureManager getTextureManager() {
		return getMinecraft().getTextureManager();
	}

	@Override
	public FontRenderer getFontRenderer() {
		return getMinecraft().fontRenderer;
	}
}
