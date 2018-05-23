package it.unitn.nlpir.nodematchers.tm;

import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.markers.TMFlatNonDupMarker;
import it.unitn.nlpir.nodematchers.strategies.MatchingStrategy;
import it.unitn.nlpir.nodematchers.strategies.SecondParentMatchingStrategy;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.projectors.nodematchmarkers.ITreeModifyingNodeMarker;
import it.unitn.nlpir.types.QuestionClass;
import it.unitn.nlpir.types.QuestionFocus;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.wiki.IAnnotationsCache;
import it.unitn.nlpir.util.wiki.IFeatureCache;
import it.unitn.nlpir.util.wiki.WikipediaAnnotation;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;

/**
 * This class performs a hard match on the tokens of the type specified by the
 * {@matchingTokenTextType} passed in the constructor.
 * By default matching is done on the lemmas.
 * 
 */
public class RelTMFocusMatcher extends TMMatcher implements NodeMatcher {
	protected final Logger logger = LoggerFactory.getLogger(RelTMFocusMatcher.class);
	
	
	protected MatchingStrategy focusMatchingStrategy;
	
	public RelTMFocusMatcher(MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, boolean bidirectional) {
		this(defaultMatchingTokenTextType, defaultRelTag, strategy, cache, featureCache, bidirectional);
	}

	public RelTMFocusMatcher(String relTag, MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, 
			boolean bidirectional) {
		super(relTag, strategy, cache, featureCache, bidirectional);
		this.focusMatchingStrategy = new SecondParentMatchingStrategy();
	}
	
	public RelTMFocusMatcher(String matchingTokenTextType, String relTag, MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, 
			boolean bidirectional) {
		super(matchingTokenTextType, relTag, strategy, cache, featureCache, bidirectional);
		this.focusMatchingStrategy = new SecondParentMatchingStrategy();
	}

	protected String getRelTag(JCas wikiCas, JCas tokenCas, Tree wikiLeaf, Tree docLeaf, boolean wikiSideTag, Token matchingToken, WikipediaAnnotation wikiAnnotation, String matchingType){

		Collection<QuestionFocus> questionFocus = JCasUtil.selectCovered(tokenCas, QuestionFocus.class, matchingToken);
		if (questionFocus.size()==0)
			return this.relTag;
			
		String questionClass = JCasUtil.selectSingle(tokenCas, QuestionClass.class).getQuestionClass();
		return String.format("%s-FOCUS-%s", this.relTag, questionClass);
			
		
	}
	
	
	protected void doTMMatch(JCas wikiCas, JCas tokenCas, Tree tokenTree, Tree wikiTree, List<WikipediaAnnotation> wikiAnnotations,
			List<MatchedNode> matches, Map<Integer, Tree> tokenTreeMap, Map<Integer, Tree> wikiTreeMap,
			List<Token> tokens, String relTag) {
		for (WikipediaAnnotation annotation : wikiAnnotations) {
			List<String> types = featureCache.get(annotation.getLink());
			if (types==null)
				continue;
			for (String feature : types)
				for (Token token : tokens) {
					if (matchObserved(feature, token)) {
						logger.info(String.format("'%s' linked to '%s' MATCHES '%s'", wikiCas.getDocumentText().substring(annotation.getStart(), annotation.getEnd()), annotation.getLink(), token.getCoveredText()));
						String thisRelTag= getRelTag(wikiCas, tokenCas, wikiTree, tokenTree, false, token, annotation, feature);
						MatchingStrategy curStrategy =  (thisRelTag.contains("FOCUS")) ? focusMatchingStrategy :strategy;

						curStrategy.doMatching(tokenTree, tokenTreeMap.get(token.getId()), matches, thisRelTag);
						for (Token wikiToken : JCasUtil.selectCovered(wikiCas, Token.class, annotation.getStart(), annotation.getEnd())) {
							if (wikiToken==null)
								continue;
							curStrategy.doMatching(wikiTree, wikiTreeMap.get(wikiToken.getId()), matches, thisRelTag);
						}
					}
				}
		}
	}
	


	@Override
	public ITreeModifyingNodeMarker getNodeMarker() {
		return new TMFlatNonDupMarker();
	}



	
	
	
}
