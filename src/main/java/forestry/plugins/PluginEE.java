/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.plugins;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IMutation;
import forestry.apiculture.genetics.AlleleBeeSpecies;
import forestry.apiculture.genetics.BeeTemplates;
import forestry.apiculture.genetics.IJubilanceProvider;
import forestry.apiculture.genetics.JubilanceReqRes;
import forestry.apiculture.genetics.MutationEMC;
import forestry.core.config.Defaults;
import forestry.core.genetics.Allele;
import forestry.core.proxy.Proxies;
import java.lang.reflect.Field;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BiomeDictionary;

@Plugin(pluginID = "EqEx2", name = "Equivalent Exchange 2", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.ee2.description")
public class PluginEE extends ForestryPlugin {

	public IMutation darkenedA;
	public IMutation reddenedA;
	public IMutation omegaA;

	public ItemStack eeBlock1;
	public ItemStack eeBlock2;

	public Class<?> eeRelay2Class;
	public Class<?> eeRelay3Class;
	public Field eeRelay2EMCField;
	public Field eeRelay3EMCField;

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded("mod_EE");
	}

	@Override
	public String getFailMessage() {
		return "Equivalent Exchange not found";
	}

	@Override
	public void postInit() {

		try {

			eeBlock1 = new ItemStack((Block) Class.forName("ee.EEBlock").getField("eeStone").get(null), 1, 8);
			eeBlock2 = new ItemStack((Block) Class.forName("ee.EEBlock").getField("eeStone").get(null), 1, 9);
			eeRelay2Class = Class.forName("ee.TileRelay2");
			eeRelay2EMCField = eeRelay2Class.getField("scaledEnergy");
			eeRelay3Class = Class.forName("ee.TileRelay3");
			eeRelay3EMCField = eeRelay3Class.getField("scaledEnergy");

		} catch (Exception ex) {
			Proxies.log.fine("Necessary block/classes from EE were not found");
			return;
		}

		IJubilanceProvider jubilanceProviderDarkened = new JubilanceReqRes(eeBlock1);
		IJubilanceProvider jubilanceProviderReddened = new JubilanceReqRes(eeBlock2);

		((AlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele("forestry.speciesDarkened")).jubilanceProvider = jubilanceProviderDarkened;
		((AlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele("forestry.speciesReddened")).jubilanceProvider = jubilanceProviderReddened;
		((AlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele("forestry.speciesOmega")).jubilanceProvider = jubilanceProviderReddened;

		darkenedA = new MutationEMC(Allele.speciesModest, Allele.speciesWintry, BeeTemplates.getDarkenedTemplate(), 100, eeBlock1, eeRelay2Class,
				eeRelay2EMCField, 139264);
		reddenedA = new MutationEMC(Allele.speciesDarkened, Allele.speciesTropical, BeeTemplates.getReddenedTemplate(), 100, eeBlock2, eeRelay3Class,
				eeRelay3EMCField, 466944);
		omegaA = new MutationEMC(Allele.speciesDarkened, Allele.speciesReddened, BeeTemplates.getOmegaTemplate(), 100, eeBlock2, eeRelay3Class,
				eeRelay3EMCField, 933888).restrictBiomeType(BiomeDictionary.Type.NETHER).restrictBiomeType(BiomeDictionary.Type.SANDY);

	}
}
