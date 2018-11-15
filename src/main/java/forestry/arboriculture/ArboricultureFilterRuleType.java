package forestry.arboriculture;

import java.util.Locale;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterRule;
import forestry.api.genetics.IFilterRuleType;
import forestry.core.render.TextureManagerForestry;

public enum ArboricultureFilterRuleType implements IFilterRuleType {
	TREE {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent();
		}
	},
	SAPLING {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent() && data.getType() == EnumGermlingType.SAPLING;
		}
	},
	POLLEN {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return data.isPresent() && data.getType() == EnumGermlingType.POLLEN;
		}
	};

	private final String uid;

	ArboricultureFilterRuleType() {
		this.uid = "forestry.arboriculture." + name().toLowerCase(Locale.ENGLISH);
	}

	public static void init() {
		for (ArboricultureFilterRuleType rule : values()) {
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
		return TreeManager.treeRoot.getUID();
	}

	@Override
	public String getUID() {
		return uid;
	}
}
