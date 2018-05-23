package it.unitn.nlpir.features.presets;

import it.unitn.nlpir.features.FeatureSets;
import it.unitn.nlpir.features.builder.FeaturesBuilder;

public class JaccardNERFeatures implements IVectorFeatureExtractor{

	@Override
	public FeaturesBuilder getFeaturesBuilder() {
		return new FeaturesBuilder()
		
		.extend(FeatureSets.buildDKProNEJaccardNESimiliarityFeatures());
	}
}
