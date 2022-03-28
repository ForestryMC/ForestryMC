package forestry.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.IGuiSizable;


/**
 * This element is the top parent.
 */
@OnlyIn(Dist.CLIENT)
public class WindowGui<G extends Screen & IGuiSizable> extends Window {
	protected final G gui;

	public WindowGui(int width, int height, G gui) {
		super(width, height);
		this.gui = gui;
	}

	@Override
	public WindowGui<G> getWindow() {
		return this;
	}

	/* Gui Screen */
	@Override
	public int getScreenWidth() {
		return gui.width;
	}

	@Override
	public int getScreenHeight() {
		return gui.height;
	}

	@Override
	public int getGuiLeft() {
		return gui.getGuiLeft();
	}

	@Override
	public int getGuiTop() {
		return gui.getGuiTop();
	}

	@Override
	public G getGui() {
		return gui;
	}

	@Override
	public int getGuiHeight() {
		return gui.getSizeX();
	}

	@Override
	public int getGuiWidth() {
		return gui.getSizeY();
	}

	@Override
	protected Minecraft getMinecraft() {
		if (mc == null) {
			mc = Minecraft.getInstance();
		}
		return mc;
	}

	@Override
	public Font getFontRenderer() {
		return getMinecraft().font;
	}
}
