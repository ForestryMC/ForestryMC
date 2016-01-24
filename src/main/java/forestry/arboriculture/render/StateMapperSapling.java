package forestry.arboriculture.render;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.blocks.BlockSapling;
import forestry.core.render.StateMapperForestry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StateMapperSapling extends StateMapperForestry {
	
	@Override
	public Map putStateModelLocations(Block block) {
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleTreeSpecies) {
				IBlockState state = block.getDefaultState().withProperty(BlockSapling.TREE, (IAlleleTreeSpecies)allele);
				LinkedHashMap linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
				String modID = ((IAlleleTreeSpecies) allele).getModID();
				if(modID == null){
					modID = "forestry";
				}
				String s = String.format("%s:%s",((IAlleleTreeSpecies) allele).getModID(), "germlings");
				mapStateModelLocations.put(state, new ModelResourceLocation(s, getPropertyString(linkedhashmap)));
			}
		}
		return mapStateModelLocations;
	}

}
