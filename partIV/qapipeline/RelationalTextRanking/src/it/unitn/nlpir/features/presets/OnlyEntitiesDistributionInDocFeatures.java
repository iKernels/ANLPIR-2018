package it.unitn.nlpir.features.presets;

import it.unitn.nlpir.features.EntitiesDistributionInDocument;
import it.unitn.nlpir.features.builder.FeaturesBuilder;

public class OnlyEntitiesDistributionInDocFeatures implements IVectorFeatureExtractor{

	@Override
	public FeaturesBuilder getFeaturesBuilder() {
		return new FeaturesBuilder()
		
		.add(new EntitiesDistributionInDocument());
	}
}
