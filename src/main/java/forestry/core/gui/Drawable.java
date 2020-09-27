package forestry.core.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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

    public Drawable(
            ResourceLocation textureLocation,
            int u,
            int v,
            int uWidth,
            int vHeight,
            int textureWidth,
            int textureHeight
    ) {
        this.u = u;
        this.v = v;
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        this.textureLocation = textureLocation;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public void draw(MatrixStack transform, int yOffset, int xOffset) {
        draw(transform, yOffset, uWidth, vHeight, xOffset);
    }

    public void draw(MatrixStack transform, int yOffset, int width, int height, int xOffset) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bindTexture(textureLocation);

        // Enable correct lighting.
        RenderSystem.enableAlphaTest();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        AbstractGui.blit(
                transform,
                xOffset,
                yOffset,
                width,
                height,
                u,
                v,
                uWidth,
                vHeight,
                textureWidth,
                textureHeight
        );
        RenderSystem.disableAlphaTest();
        //AbstractGui.blit(xOffset, yOffset, u, v, uWidth, vHeight, width, height, textureWidth, textureHeight);
    }
}
