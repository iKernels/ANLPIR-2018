package it.unitn.nlpir.nodematchers.tm;

import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.strategies.MatchingStrategy;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.DocumentId;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.uima.TokenTextGetter;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.TreeUtil;
import it.unitn.nlpir.util.wiki.IAnnotationsCache;
import it.unitn.nlpir.util.wiki.IFeatureCache;
import it.unitn.nlpir.util.wiki.WikipediaAnnotation;

import java.util.ArrayList;
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
public class WikiRelMatcher implements NodeMatcher {
	protected final Logger logger = LoggerFactory.getLogger(WikiRelMatcher.class);
	
	public static final String defaultMatchingTokenTextType = TokenTextGetterFactory.LEMMA;
	public static final String defaultRelTag = "REL";
	
	protected TokenTextGetter tokenTextGetter;
	protected String relTag;
	protected MatchingStrategy strategy;
	protected IAnnotationsCache annotationsCache;
	
	
	
	
	public WikiRelMatcher(MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, boolean bidirectional) {
		this(defaultMatchingTokenTextType, defaultRelTag, strategy, cache, featureCache, bidirectional);
	}


	public WikiRelMatcher(String relTag, MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, 
			boolean bidirectional) {
		this(defaultMatchingTokenTextType, relTag, strategy, cache, featureCache, bidirectional);
	}
	
	public WikiRelMatcher(String matchingTokenTextType, String relTag, MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, 
			boolean bidirectional) {
		this.tokenTextGetter = TokenTextGetterFactory
				.getTokenTextGetter(matchingTokenTextType);
		this.strategy = strategy;
		this.relTag = relTag;
		this.annotationsCache = cache;
		
	}

	protected String getRelTag(JCas wikiCas, JCas tokenCas, Tree wikiLeaf, Tree docLeaf, boolean wikiSideTag, Token matchingToken, WikipediaAnnotation wikiAnnotation, String matchingType){
		return String.format(this.relTag);
	}
	
	
	@Override
	public List<MatchedNode> getMatches(JCas questionCas, JCas documentCas, Tree questionTree, Tree documentTree) {
		
		String qid = JCasUtil.selectSingle(questionCas, DocumentId.class).getId().replace("question-", "");
		String aid = JCasUtil.selectSingle(documentCas, DocumentId.class).getId().replace("document-", "");
		List<MatchedNode> matches = new ArrayList<>();
		if (!this.annotationsCache.containsAnnotation(qid, aid))
			return matches;
		
		
		Map<Integer, Tree> questionTreeMap = TreeUtil.getTokenIdToTreeNodeMap(questionTree);
		Map<Integer, Tree> answerTreeMap = TreeUtil.getTokenIdToTreeNodeMap(documentTree);
		
		
		

		for (WikipediaAnnotation questionLink : annotationsCache.getQuestionAnnotation(qid, aid)){
			for (WikipediaAnnotation answerLink : annotationsCache.getAnswerAnnotation(qid, aid)) {
				if (!questionLink.getLink().equals(answerLink.getLink()))
					continue;
				String matchedQLemmas = matchWikiTokens(questionCas, questionTree, matches, questionTreeMap, questionLink);
				String matchedALemmas = matchWikiTokens(documentCas, documentTree, matches, answerTreeMap, answerLink);
				if (!matchedQLemmas.equals(matchedALemmas))
					logger.info(String.format("Matched '%s' in question and '%s' in answer both linked to '%s'", matchedQLemmas, matchedALemmas,questionLink.getLink()));
				
			}
		}
		
		return matches;
	}


	protected String matchWikiTokens(JCas cas, Tree tree, List<MatchedNode> matches,
			Map<Integer, Tree> treeMap, WikipediaAnnotation link) {
		StringBuffer sb = new StringBuffer();
		for (Token token : JCasUtil.selectCovered(cas, Token.class, link.getStart(), link.getEnd())) {
			sb.append(token.getLemma());
			sb.append(" ");
			strategy.doMatching(tree, treeMap.get(token.getId()), matches, getRelTag(null, null, null, null, false, null, null, null));
		}
		return sb.toString().trim().toLowerCase();
	}

}
