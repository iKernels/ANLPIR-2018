package it.unitn.nlpir.nodematchers.tm;

import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.strategies.MatchingStrategy;
import it.unitn.nlpir.types.QuestionFocus;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.wiki.IAnnotationsCache;
import it.unitn.nlpir.util.wiki.IFeatureCache;
import it.unitn.nlpir.util.wiki.WikipediaAnnotation;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;

/**
 * TAG-FOCUS if matched focus, TAG if matched anything else
 * 
 */
public class TMFMatcher extends TMMatcher implements NodeMatcher {
	protected final Logger logger = LoggerFactory.getLogger(TMFMatcher.class);
	
	public static final String FOCUS_TAGPATTERN= "(%s-FOCUS)";

	
	public TMFMatcher(MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, boolean bidirectional) {
		this(defaultMatchingTokenTextType, defaultRelTag, strategy, cache, featureCache, bidirectional);
	}

	public TMFMatcher(String relTag, MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, 
			boolean bidirectional) {
		super(relTag, strategy, cache, featureCache, bidirectional);
	}
	
	public TMFMatcher(String matchingTokenTextType, String relTag, MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, 
			boolean bidirectional) {
		super(matchingTokenTextType, relTag, strategy, cache, featureCache, bidirectional);
	}


	
	protected String getRelTag(JCas wikiCas, JCas tokenCas, Tree wikiLeaf, Tree docLeaf, boolean wikiSideTag, Token matchingToken, WikipediaAnnotation wikiAnnotation, String matchingType){
		String returnTag = (JCasUtil.selectCovered(tokenCas, QuestionFocus.class, matchingToken).size()>0) ? String.format(FOCUS_TAGPATTERN, this.relTag) : String.format("(%s)",this.relTag);
		return returnTag;
				
	}
	
	
	



	
	
	
}
