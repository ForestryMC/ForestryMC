package forestry.lepidopterology;

import java.util.Locale;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IFilterRule;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.core.render.TextureManagerForestry;

public enum LepidopterologyFilterRule implements IFilterRule {
	FLUTTER{
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent();
		}
	},
	BUTTERFLY{
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent() && data.getType() == EnumFlutterType.BUTTERFLY;
		}
	},
	SERUM{
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent() && data.getType() == EnumFlutterType.SERUM;
		}
	},
	CATERPILLAR{
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent() && data.getType() == EnumFlutterType.CATERPILLAR;
		}
	},
	COCOON{
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent() && data.getType() == EnumFlutterType.COCOON;
		}
	};

	private final String uid;

	LepidopterologyFilterRule() {
		this.uid = "forestry.lepidopterology." + name().toLowerCase(Locale.ENGLISH);
	}

	public static void init() {
		for (LepidopterologyFilterRule rule : values()) {
			AlleleManager.filterRegistry.registerFilter(rule);
		}
	}

	@Override
	public void addLogic(IFilterLogic logic) {
		throw new IllegalStateException();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public TextureAtlasSprite getSprite() {
		return TextureManagerForestry.getInstance().getDefault("analyzer/" + name().toLowerCase(Locale.ENGLISH));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTextureMap() {
		return TextureManagerForestry.LOCATION_FORESTRY_TEXTURE;
	}

	@Override
	public String getRootUID() {
		return ButterflyManager.butterflyRoot.getUID();
	}

	@Override
	public String getUID() {
		return uid;
	}
}
