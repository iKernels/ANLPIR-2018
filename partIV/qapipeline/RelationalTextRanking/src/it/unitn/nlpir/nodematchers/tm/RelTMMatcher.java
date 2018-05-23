package it.unitn.nlpir.nodematchers.tm;

import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.markers.TMFlatNonDupMarker;
import it.unitn.nlpir.nodematchers.strategies.MatchingStrategy;
import it.unitn.nlpir.projectors.nodematchmarkers.ITreeModifyingNodeMarker;
import it.unitn.nlpir.util.wiki.IAnnotationsCache;
import it.unitn.nlpir.util.wiki.IFeatureCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class performs a hard match on the tokens of the type specified by the
 * {@matchingTokenTextType} passed in the constructor.
 * By default matching is done on the lemmas.
 * 
 */
public class RelTMMatcher extends TMMatcher implements NodeMatcher {
	protected final Logger logger = LoggerFactory.getLogger(RelTMMatcher.class);
	
	
	
	public RelTMMatcher(MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, boolean bidirectional) {
		this(defaultMatchingTokenTextType, defaultRelTag, strategy, cache, featureCache, bidirectional);
	}

	public RelTMMatcher(String relTag, MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, 
			boolean bidirectional) {
		super(relTag, strategy, cache, featureCache, bidirectional);
	}
	
	public RelTMMatcher(String matchingTokenTextType, String relTag, MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, 
			boolean bidirectional) {
		super(matchingTokenTextType, relTag, strategy, cache, featureCache, bidirectional);
	}

	
	
	
	
	


	@Override
	public ITreeModifyingNodeMarker getNodeMarker() {
		return new TMFlatNonDupMarker();
	}



	
	
	
}
