package forestry.modules.features;

public interface IFeatureObject<F extends IModFeature<?>> {
	void init(F feature);
}
