package forestry.api.apiculture;

import forestry.api.genetics.IEffectData;

@SuppressWarnings("unused")
public interface IAlleleBeeAcceleratableEffect {

    IEffectData doEffectAccelerated(IBeeGenome genome, IEffectData storedData, IBeeHousing housing, float did);
}
