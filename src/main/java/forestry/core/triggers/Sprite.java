package forestry.core.triggers;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import buildcraft.api.core.render.ISprite;

public class Sprite implements ISprite {

	private final ResourceLocation rl;

	public Sprite(ResourceLocation rl) {
		this.rl = rl;
	}

	@Override
	public void bindTexture() {
		Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
	}

	@Override
	public double getInterpU(double u) {
		return u;
	}

	@Override
	public double getInterpV(double v) {
		return v;
	}

}
