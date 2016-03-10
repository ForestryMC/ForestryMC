package forestry.arboriculture.blocks;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumWoodType;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.blocks.property.PropertyWoodType;

import com.sun.istack.internal.Nullable;

@SideOnly(Side.CLIENT)
class WoodTypeStateMapper extends StateMapperBase {

	@Nonnull
	private final IWoodTyped woodTyped;
	@Nullable
	private final PropertyWoodType propertyWoodType;

	public WoodTypeStateMapper(@Nonnull IWoodTyped woodTyped, @Nullable PropertyWoodType propertyWoodType) {
		this.woodTyped = woodTyped;
		this.propertyWoodType = propertyWoodType;
	}

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		final Map<IProperty, Comparable> properties;
		if (propertyWoodType != null) {
			properties = Maps.newLinkedHashMap(state.getProperties());
			properties.remove(propertyWoodType);
		} else {
			properties = state.getProperties();
		}

		Block block = state.getBlock();
		int meta = block.getMetaFromState(state);
		EnumWoodType woodType = woodTyped.getWoodType(meta);
		String resourceDomain = Block.blockRegistry.getNameForObject(block).getResourceDomain();
		String blockKind = woodTyped.getBlockKind().replace('.', '/');
		String resourceLocation = "arboriculture/" + blockKind + '/' + woodType;
		String propertyString = this.getPropertyString(properties);
		return new ModelResourceLocation(resourceDomain + ':' + resourceLocation, propertyString);
	}

}
