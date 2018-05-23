package it.unitn.nlpir.features.builder;

import it.unitn.nlpir.features.FeatureExtractor;
import it.unitn.nlpir.features.QAPair;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import svmlighttk.SVMVector;

public class FeaturesBuilder {
	static final Logger logger = LoggerFactory.getLogger(FeaturesBuilder.class);

	private List<FeatureExtractor> featureExtractorList;
	private List<String> featureNames;
	private boolean outputInfoMessage;
	public FeaturesBuilder() {
		this.featureExtractorList = new ArrayList<>();
		this.featureNames = new ArrayList<>();
		this.outputInfoMessage = true;
	}
	
	public FeaturesBuilder add(FeatureExtractor f) {
		add(f, f.getFeatureName() + "-" + featureExtractorList.size());
		return this;
	}
	
	public FeaturesBuilder extend(FeaturesBuilder fb) {
		for (FeatureExtractor f: fb.getFeatureExtractorList())
			add(f, f.getFeatureName() + "-" + featureExtractorList.size());
		return this;
	}
	
	public List<FeatureExtractor> getFeatureExtractorList() {
		return featureExtractorList;
	}

	public List<String> getFeatureNames() {
		return featureNames;
	}


	public FeaturesBuilder add(FeatureExtractor f, String name) {
		this.featureExtractorList.add(f);
		this.featureNames.add(name);
		return this;
	}
	
	public SVMVector getFeatures(QAPair qa) {
		long globalStart = System.currentTimeMillis();
		for (FeatureExtractor f : this.featureExtractorList) {
			
			int sizeBefore = qa.getFeatureVector().getFeatures().size();
			long start = System.currentTimeMillis();
			f.extractFeatures(qa);
			int sizeAfter = qa.getFeatureVector().getFeatures().size();
			
			long elapsed = System.currentTimeMillis() - start;
			if (this.outputInfoMessage)
				logger.info(String.format("Feature name: %s; Range: %d:%d;", f.getFeatureName(), sizeBefore+1, sizeAfter));
			logger.debug(String.format("Feature name: %s; Range: %d:%d; EXTRACTION TIME: %d", f.getFeatureName(), sizeBefore+1, sizeAfter,elapsed));
			
		}
		this.outputInfoMessage = false;
		long globalElapsed = System.currentTimeMillis() - globalStart;
		logger.debug(String.format("OVERALL EXTRACTION TIME: %d", globalElapsed));
		
		return qa.getFeatureVector();
	}
}
