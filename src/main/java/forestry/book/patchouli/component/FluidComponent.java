package forestry.book.patchouli.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class FluidComponent implements ICustomComponent {
    public IVariable fluid;
    public IVariable amount;
    public IVariable max;
    public IVariable width;
    public IVariable height;

    private transient int x, y, w, h, level, maxLevel;
    private transient FluidStack fluidStack;

    @Override
    public void build(int componentX, int componentY, int pageNum) {
        this.x = componentX;
        this.y = componentY;
    }

    @Override
    public void render(MatrixStack ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        ms.pushPose();
        Fluid _fluid = fluidStack.getFluid();
        FluidAttributes fluidAttributes = _fluid.getAttributes();
        ResourceLocation fluidStill = fluidAttributes.getStillTexture(fluidStack);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(fluidStill);
        ResourceLocation spriteLocation = sprite.getName();
        Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(spriteLocation.getNamespace(), "textures/" + spriteLocation.getPath() + ".png"));
        setGLColorFromInt(fluidAttributes.getColor(fluidStack));

        // MatrixStack transform, int x, int y, float u, float v, int width, int height, int ?, int ?
        AbstractGui.blit(ms, x, (int) (y + h - Math.floor(h * ((float)level/maxLevel))), sprite.getU0(), sprite.getV0(), w, h*level/maxLevel, 8, 8);

        if (context.isAreaHovered(mouseX, mouseY, x, y, w, h)) {
            List<ITextComponent> toolTips = new ArrayList<>();
            toolTips.add(new TranslationTextComponent(fluidAttributes.getTranslationKey(fluidStack)));
            TranslationTextComponent liquidAmount = new TranslationTextComponent("for.gui.tooltip.liquid.amount", level, maxLevel);
            toolTips.add(liquidAmount);

            context.setHoverTooltipComponents(toolTips);
        }

        ms.popPose();
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
        ResourceLocation id = new ResourceLocation(lookup.apply(fluid).asString());
        int mb = lookup.apply(amount).asNumber().intValue();
        System.out.println("");
        try {
            this.fluidStack = new FluidStack(ForgeRegistries.FLUIDS.getValue(id), mb);
        } catch (Exception e) {
            this.fluidStack = FluidStack.EMPTY;
        }

        this.w = lookup.apply(width).asNumber().intValue();
        this.h = lookup.apply(height).asNumber().intValue();
        this.level = lookup.apply(amount).asNumber().intValue();
        this.maxLevel = lookup.apply(max).asNumber().intValue();
    }

    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        RenderSystem.color4f(red, green, blue, 1.0F);
    }
}
