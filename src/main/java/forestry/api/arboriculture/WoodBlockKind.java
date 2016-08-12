package forestry.api.arboriculture;

import java.util.Locale;

import com.google.common.collect.ImmutableMap;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelProcessingHelper;

public enum WoodBlockKind {
	LOG, PLANKS, SLAB, FENCE, FENCE_GATE, STAIRS, DOOR(false);

	public final boolean retextureItem;
	
	private WoodBlockKind() {
		this(true);
	}
	
	private WoodBlockKind(boolean retextureItem) {
		this.retextureItem = retextureItem;
	}
	
	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.ENGLISH);
	}
	
	public IModel retextureModel(IModel model, IWoodType type){
		ImmutableMap.Builder<String, String> textures = new ImmutableMap.Builder();
		switch (this) {
		case SLAB:
		case STAIRS:
			String textureLocation = type.getPlankTexture();
			textures.put("particle", textureLocation);
			textures.put("side", textureLocation);
			textures.put("top", textureLocation);
			textures.put("bottom", textureLocation);
			textures.put("all", textureLocation);
			break;
		case PLANKS:
			textures.put("particle", type.getPlankTexture());
			textures.put("all", type.getPlankTexture());
			break;
		case FENCE_GATE:
		case FENCE:
			textures.put("particle", type.getPlankTexture());
			textures.put("side", type.getPlankTexture());
			textures.put("texture", type.getPlankTexture());
			break;
		case DOOR:
			textures.put("particle", type.getDoorLowerTexture());
			textures.put("bottom", type.getDoorLowerTexture());
			textures.put("top", type.getDoorUpperTexture());
			break;
		case LOG:
			textures.put("particle", type.getBarkTexture());
			textures.put("side", type.getBarkTexture());
			textures.put("all", type.getBarkTexture());
			textures.put("end", type.getHeartTexture());
			break;
		default:
			break;
		}
		return ModelProcessingHelper.retexture(model, textures.build());
	}
}
