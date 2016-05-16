package forestry.api.genetics;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IGuiAlyzer {
	
	@SideOnly(Side.CLIENT)
	int getColorCoding(boolean dominant);

	@SideOnly(Side.CLIENT)
	void drawLine(String text, int x, IIndividual individual, IChromosomeType chromosome, boolean inactive);

	@SideOnly(Side.CLIENT)
	void drawSplitLine(String text, int x, int maxWidth, IIndividual individual, IChromosomeType chromosome, boolean inactive);

	@SideOnly(Side.CLIENT)
	void drawRow(String text0, String text1, String text2, IIndividual individual, IChromosomeType chromosome);

	@SideOnly(Side.CLIENT)
	void drawChromosomeRow(String chromosomeName, IIndividual individual, IChromosomeType chromosome);

	@SideOnly(Side.CLIENT)
	void drawSpeciesRow(String text0, IIndividual individual, IChromosomeType chromosome, String customPrimaryName, String customSecondaryName);

	@SideOnly(Side.CLIENT)
	void drawAnalyticsOverview();

	@SideOnly(Side.CLIENT)
	void drawAnalyticsPageClassification(IIndividual individual);

	@SideOnly(Side.CLIENT)
	void drawAnalyticsPageMutations(IIndividual individual);

	@SideOnly(Side.CLIENT)
	void drawMutationInfo(IMutation combination, IAllele species, int x);

	@SideOnly(Side.CLIENT)
	void drawToleranceInfo(IAlleleTolerance toleranceAllele, int x);

	@SideOnly(Side.CLIENT)
	void drawFertilityInfo(int fertility, int x, int textColor, int texOffset);
	
}
