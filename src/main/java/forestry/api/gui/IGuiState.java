package forestry.api.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGuiState {

	/**
	 * Returns the mouse position.
	 */
	int getMouseX();

	/**
	 * Returns the mouse position.
	 */
	int getMouseY();

	/**
	 * Returns the mouse position relative to the given element.
	 */
	int getRelativeMouseX(IGuiElement element);

	/**
	 * Returns the mouse position relative to the given element.
	 */
	int getRelativeMouseY(IGuiElement element);

	/**
	 * Returns the current screen width.
	 */
	int getScreenWidth();

	/**
	 * Returns the current screen height.
	 */
	int getScreenHeight();

	/**
	 * Returns the current gui height.
	 */
	int getGuiHeight();

	/**
	 * Returns the current gui width.
	 */
	int getGuiWidth();

	int getGuiLeft();

	int getGuiTop();

	TextureManager getTextureManager();

	FontRenderer getFontRenderer();
}
