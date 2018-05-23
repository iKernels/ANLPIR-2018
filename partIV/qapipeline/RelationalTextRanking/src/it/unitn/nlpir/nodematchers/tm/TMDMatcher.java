package it.unitn.nlpir.nodematchers.tm;

import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.strategies.MatchingStrategy;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.wiki.IAnnotationsCache;
import it.unitn.nlpir.util.wiki.IFeatureCache;
import it.unitn.nlpir.util.wiki.WikipediaAnnotation;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.stanford.nlp.trees.Tree;

/**
 * This class performs a hard match on the tokens of the type specified by the
 * {@matchingTokenTextType} passed in the constructor.
 * By default matching is done on the lemmas.
 * 
 */
public class TMDMatcher extends TMMatcher implements NodeMatcher {
	protected final Logger logger = LoggerFactory.getLogger(TMDMatcher.class);
	
	public static final String CHILD_REL_TAG_PATTERN= "(%s-C)";
	public static final String PARENT_REL_TAG_PATTERN= "(%s-P)";
	
	public TMDMatcher(MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, boolean bidirectional) {
		this(defaultMatchingTokenTextType, defaultRelTag, strategy, cache, featureCache, bidirectional);
	}

	public TMDMatcher(String relTag, MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, 
			boolean bidirectional) {
		super(relTag, strategy, cache, featureCache, bidirectional);
	}
	
	public TMDMatcher(String matchingTokenTextType, String relTag, MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, 
			boolean bidirectional) {
		super(matchingTokenTextType, relTag, strategy, cache, featureCache, bidirectional);
	}


	
	protected String getRelTag(JCas wikiCas, JCas tokenCas, Tree wikiLeaf, Tree docLeaf, boolean wikiSideTag, Token matchingToken, WikipediaAnnotation wikiAnnotation, String matchingType){
		return wikiSideTag ? String.format(CHILD_REL_TAG_PATTERN, this.relTag) : String.format(PARENT_REL_TAG_PATTERN, this.relTag);
	}
	
	
	



	
	
	
}
