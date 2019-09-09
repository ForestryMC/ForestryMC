package forestry.api.genetics;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
public interface IFilterRuleType extends IFilterRule {
	void addLogic(IFilterRule logic);

	/**
	 * @return True if  a other logic can be added to this type.
	 */
	boolean isContainer();

	/**
	 * @return A unique identifier for the rule.
	 */
	String getUID();

	@OnlyIn(Dist.CLIENT)
	TextureAtlasSprite getSprite();

	@OnlyIn(Dist.CLIENT)
	ResourceLocation getTextureMap();
}
