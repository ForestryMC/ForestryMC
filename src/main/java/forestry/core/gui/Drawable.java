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
	public final int u;
	public final int v;
	public final int width;
	public final int height;

	public final ResourceLocation textureLocation;
	public int textureWidth = 256;
	public int textureHeight = 256;

	public Drawable(ResourceLocation textureLocation, int u, int v, int width, int height) {
		this.textureLocation = textureLocation;
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
	}

	public Drawable(ResourceLocation textureLocation, int u, int v, int width, int height, int textureWidth, int textureHeight) {
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
		this.textureLocation = textureLocation;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	public Drawable setTextureSize(int textureWidth, int textureHeight) {
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		return this;
	}

	public void draw(int xOffset, int yOffset) {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(textureLocation);

		// Enable correct lighting.
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		Gui.drawModalRectWithCustomSizedTexture(xOffset, yOffset, u, v, width, height, textureWidth, textureHeight);
	}
}
