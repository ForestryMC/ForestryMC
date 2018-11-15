package forestry.apiculture;

import java.util.Locale;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterRule;
import forestry.api.genetics.IFilterRuleType;
import forestry.core.render.TextureManagerForestry;

public enum ApicultureFilterRuleType implements IFilterRuleType {
	BEE {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent();
		}
	},
	DRONE {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent() && data.getType() == EnumBeeType.DRONE;
		}
	},
	PRINCESS {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent() && data.getType() == EnumBeeType.PRINCESS;
		}
	},
	QUEEN {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent() && data.getType() == EnumBeeType.QUEEN;
		}
	};

	private final String uid;

	ApicultureFilterRuleType() {
		this.uid = "forestry.apiculture." + name().toLowerCase(Locale.ENGLISH);
	}

	public static void init() {
		for (ApicultureFilterRuleType rule : values()) {
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
		return BeeManager.beeRoot.getUID();
	}

	@Override
	public String getUID() {
		return uid;
	}
}
