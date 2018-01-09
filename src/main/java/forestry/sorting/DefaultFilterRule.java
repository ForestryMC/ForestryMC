package forestry.sorting;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IFilterRule;
import forestry.core.render.TextureManagerForestry;

public enum DefaultFilterRule implements IFilterRule {
	CLOSED {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return false;
		}
	},
	ANYTHING {
		@Override
		public boolean isValid(ItemStack itemStack, IFilterData data) {
			return true;
		}
	},
	ITEM {
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
	private final Set<IFilterLogic> logic;

	DefaultFilterRule() {
		this.uid = "forestry.default." + name().toLowerCase(Locale.ENGLISH);
		this.logic = new HashSet<>();
	}

	public static void init() {
		for (DefaultFilterRule rule : values()) {
			AlleleManager.filterRegistry.registerFilter(rule);
		}
	}

	@Override
	public boolean isValid(ItemStack itemStack, IFilterData data) {
		for (IFilterLogic logic : logic) {
			if (logic.isValid(itemStack, data)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void addLogic(IFilterLogic logic) {
		if (logic == this) {
			throw new IllegalArgumentException();
		}
		this.logic.add(logic);
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
	public String getUID() {
		return uid;
	}

}
