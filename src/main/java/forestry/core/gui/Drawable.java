package forestry.core.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Drawable {
	/* Final Attributes */
	//Position on the Texture
	public final int u;
	public final int v;
	//Rectangle Size
	public final int uWidth;
	public final int vHeight;
	//Texture
	public final ResourceLocation textureLocation;
	//Texture Size
	private final int textureWidth;
	private final int textureHeight;

	public Drawable(ResourceLocation textureLocation, int u, int v, int uWidth, int vHeight) {
		this(textureLocation, u, v, uWidth, vHeight, 256, 256);
	}

	public Drawable(ResourceLocation textureLocation, int u, int v, int uWidth, int vHeight, int textureWidth, int textureHeight) {
		this.u = u;
		this.v = v;
		this.uWidth = uWidth;
		this.vHeight = vHeight;
		this.textureLocation = textureLocation;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	public void draw(int xOffset, int yOffset) {
		draw(xOffset, yOffset, uWidth, vHeight);
	}

	public void draw(int xOffset, int yOffset, int width, int height) {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(textureLocation);

		// Enable correct lighting.
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		Gui.drawScaledCustomSizeModalRect(xOffset, yOffset, u, v, uWidth, vHeight, width, height, textureWidth, textureHeight);
	}
}
