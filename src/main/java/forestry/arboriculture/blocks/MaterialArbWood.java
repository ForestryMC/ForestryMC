package forestry.arboriculture.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialArbWood extends Material {

	public static final Material ARB_WOOD = new MaterialArbWood();
	
	private MaterialArbWood() {
		super(MapColor.WOOD);
		setBurning();
	}

}
