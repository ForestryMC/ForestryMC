package forestry.sorting;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterRule;
import forestry.api.genetics.IFilterRuleType;
import forestry.core.render.TextureManagerForestry;

public enum DefaultFilterRuleType implements IFilterRuleType {
	CLOSED(false) {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return false;
		}
	},
	ANYTHING(false) {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return true;
		}
	},
	ITEM(false) {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return !data.isPresent();
		}
	},
	PURE_BREED,
	NOCTURNAL,
	PURE_NOCTURNAL,
	FLYER,
	PURE_FLYER,
	CAVE,
	PURE_CAVE,
	/*FIREPROOF,
	PURE_FIREPROOF*/;

	private final String uid;
	private final Set<IFilterRule> logic;
	private final boolean isContainer;

	DefaultFilterRuleType() {
		this(true);
	}

	DefaultFilterRuleType(boolean isContainer) {
		this.uid = "forestry.default." + name().toLowerCase(Locale.ENGLISH);
		this.logic = new HashSet<>();
		this.isContainer = isContainer;
	}

	public static void init() {
		for (DefaultFilterRuleType rule : values()) {
			AlleleManager.filterRegistry.registerFilter(rule);
		}
	}

	@Override
	public boolean isValid(ItemStack itemStack, IFilterData data) {
		for (IFilterRule logic : logic) {
			if (logic.isValid(itemStack, data)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void addLogic(IFilterRule logic) {
		if (logic == this) {
			throw new IllegalArgumentException();
		}
		this.logic.add(logic);
	}

	@Override
	public boolean isContainer() {
		return isContainer;
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
	public String getUID() {
		return uid;
	}

}
