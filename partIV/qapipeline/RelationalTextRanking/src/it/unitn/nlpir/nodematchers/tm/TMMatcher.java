package it.unitn.nlpir.nodematchers.tm;

import it.unitn.nlpir.nodematchers.IMatcherWithTreeModifyingNodesMarker;
import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.markers.LTMHierNonDupMarker;
import it.unitn.nlpir.nodematchers.strategies.MatchingStrategy;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.projectors.nodematchmarkers.ITreeModifyingNodeMarker;

import it.unitn.nlpir.types.DocumentId;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.uima.TokenTextGetter;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.TreeUtil;
import it.unitn.nlpir.util.UIMAUtil;
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
public class TMMatcher implements NodeMatcher, IMatcherWithTreeModifyingNodesMarker {
	protected final Logger logger = LoggerFactory.getLogger(TMMatcher.class);
	
	public static final String defaultMatchingTokenTextType = TokenTextGetterFactory.LEMMA;
	public static final String defaultRelTag = "TM";
	
	protected TokenTextGetter tokenTextGetter;
	protected String relTag;
	protected MatchingStrategy strategy;
	protected IAnnotationsCache annotationsCache;
	protected IFeatureCache featureCache;
	protected boolean bidirectional;
	protected ITreeModifyingNodeMarker marker;
	
	public TMMatcher(MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, boolean bidirectional) {
		this(defaultMatchingTokenTextType, defaultRelTag, strategy, cache, featureCache, bidirectional);
	}


	public TMMatcher(String relTag, MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, 
			boolean bidirectional) {
		this(defaultMatchingTokenTextType, relTag, strategy, cache, featureCache, bidirectional);
	}
	
	public TMMatcher(String matchingTokenTextType, String relTag, MatchingStrategy strategy, IAnnotationsCache cache, IFeatureCache featureCache, 
			boolean bidirectional) {
		this.tokenTextGetter = TokenTextGetterFactory
				.getTokenTextGetter(matchingTokenTextType);
		this.strategy = strategy;
		this.relTag = relTag;
		this.annotationsCache = cache;
		this.featureCache = featureCache;
		this.bidirectional = bidirectional;
	}

	protected String getRelTag(JCas wikiCas, JCas tokenCas, Tree wikiLeaf, Tree docLeaf, boolean wikiSideTag, Token matchingToken, WikipediaAnnotation wikiAnnotation, String matchingType){
		return String.format("(%s)",this.relTag);
	}
	
	protected boolean skipToken(Token t){
		if (t.getIsFiltered())
			return true;

		if (t.getCoveredText().matches("[\\s\\-\\ ]*url[\\s\\-\\ ]*"))
			return true;
		
		return false;
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
		
		
		List<Token> qTokens = UIMAUtil.getAnnotationsAsList(questionCas, Token.class);

		
		doTMMatch(documentCas,questionCas,questionTree, documentTree, annotationsCache.getAnswerAnnotation(qid, aid), 
				matches, questionTreeMap, answerTreeMap, qTokens, this.relTag);
		if (bidirectional) {
			List<Token> aTokens = UIMAUtil.getAnnotationsAsList(documentCas, Token.class);
			doTMMatch(questionCas, documentCas, documentTree, questionTree,  annotationsCache.getQuestionAnnotation(qid, aid), 
					matches, answerTreeMap, questionTreeMap, aTokens, this.relTag);
		}
		
		return matches;
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
						String currentRelTag = getRelTag(wikiCas, tokenCas, wikiTree, tokenTree, false, token, annotation, feature);
						if (currentRelTag!=null)
							strategy.doMatching(tokenTree, tokenTreeMap.get(token.getId()), matches, getRelTag(wikiCas, tokenCas, wikiTree, tokenTree, false, token, annotation, feature));
						for (Token wikiToken : JCasUtil.selectCovered(wikiCas, Token.class, annotation.getStart(), annotation.getEnd())) {
							if (wikiToken==null)
								continue;
							currentRelTag =  getRelTag(wikiCas, tokenCas, wikiTree, tokenTree, true, token, annotation, feature);
							if (currentRelTag!=null)
								strategy.doMatching(wikiTree, wikiTreeMap.get(wikiToken.getId()), matches, getRelTag(wikiCas, tokenCas, wikiTree, tokenTree, true, token, annotation, feature));
						}
					}
				}
		}
	}



	protected boolean matchObserved(String feature, Token token) {
		return token.getLemma().toLowerCase().equals(feature);
	}



	@Override
	public ITreeModifyingNodeMarker getNodeMarker() {
		return new LTMHierNonDupMarker();
	}



	@Override
	public void setNodeMarker(ITreeModifyingNodeMarker marker) {
				
	}
	
	
}
