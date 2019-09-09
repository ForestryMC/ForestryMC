package forestry.lepidopterology;

import java.util.Locale;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterRule;
import forestry.api.genetics.IFilterRuleType;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.core.render.TextureManagerForestry;

public enum LepidopterologyFilterRuleType implements IFilterRuleType {
	FLUTTER {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent();
		}
	},
	BUTTERFLY {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent() && data.getType() == EnumFlutterType.BUTTERFLY;
		}
	},
	SERUM {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent() && data.getType() == EnumFlutterType.SERUM;
		}
	},
	CATERPILLAR {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent() && data.getType() == EnumFlutterType.CATERPILLAR;
		}
	},
	COCOON {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent() && data.getType() == EnumFlutterType.COCOON;
		}
	};

	private final String uid;

	LepidopterologyFilterRuleType() {
		this.uid = "forestry.lepidopterology." + name().toLowerCase(Locale.ENGLISH);
	}

	public static void init() {
		for (LepidopterologyFilterRuleType rule : values()) {
			AlleleManager.filterRegistry.registerFilter(rule);
		}
	}

	@Override
	public void addLogic(IFilterRule logic) {
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public TextureAtlasSprite getSprite() {
		return TextureManagerForestry.getInstance().getDefault("analyzer/" + name().toLowerCase(Locale.ENGLISH));
	}

	@OnlyIn(Dist.CLIENT)
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
