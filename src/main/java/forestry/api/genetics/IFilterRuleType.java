package forestry.api.genetics;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

	@SideOnly(Side.CLIENT)
	TextureAtlasSprite getSprite();

	@SideOnly(Side.CLIENT)
	ResourceLocation getTextureMap();
}
