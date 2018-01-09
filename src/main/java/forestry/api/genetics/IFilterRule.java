package forestry.api.genetics;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @since 5.8
 */
public interface IFilterRule extends IFilterLogic {
	void addLogic(IFilterLogic logic);

	/**
	 * @return A unique identifier for the rule.
	 */
	String getUID();

	@SideOnly(Side.CLIENT)
	TextureAtlasSprite getSprite();

	@SideOnly(Side.CLIENT)
	ResourceLocation getTextureMap();
}
