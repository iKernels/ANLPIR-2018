package it.unitn.nlpir.util.wiki;


import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.wiki.annotator.SimpleAnnotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


/**
 * MongoDB-driven cache for features
 * @author Kateryna
 *
 */
public class MongoDBWikiAnnotationsCache implements IAnnotationsCache {
	protected MongoClient mongo;
	protected  MongoCollection<Document> table;
	protected static final Logger logger = LoggerFactory.getLogger(MongoDBWikiAnnotationsCache.class);

	protected Map<Pair<String,String>, Pair<List<WikipediaAnnotation>, List<WikipediaAnnotation>>> ramAnnotationsCache;
	public static final String DEFAULT_HOST="127.0.0.1";
	
	
	
	
	public MongoDBWikiAnnotationsCache(String databaseName, String host, int port, String collectionName){
		try {
			mongo = new MongoClient(host, port);
		}
		catch (Exception e) {
			mongo = new MongoClient(DEFAULT_HOST, port);
		}
		MongoDatabase db = mongo.getDatabase(databaseName);
		table = db.getCollection(collectionName);
		ramAnnotationsCache = new HashMap<Pair<String,String>, Pair<List<WikipediaAnnotation>, List<WikipediaAnnotation>>>();
		try {
			initialize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected List<WikipediaAnnotation>  getWikipediaAnnotations(List<Document> annotations) {
		List<WikipediaAnnotation> wikiAnnotations = new ArrayList<WikipediaAnnotation>();
		for (Document d : annotations) {
			WikipediaAnnotation a = new SimpleAnnotation(d.getString(MongoDBAnnotationConstants.ENTITY_WIKI_TITLE_S), 
					d.getInteger(MongoDBAnnotationConstants.ENTITY_START_S, 0), d.getInteger(MongoDBAnnotationConstants.ENTITY_END_S, 0), 
					d.getDouble(MongoDBAnnotationConstants.ENTITY_RANKER_SCORE_S));
			wikiAnnotations.add(a);
		}
		return wikiAnnotations;
	}
	
	@Override
	public void initialize() throws Exception {
		
		
		for (Document doc : table.find()){
			String aid = doc.get(MongoDBAnnotationConstants.AID).toString();
			String qid = doc.get(MongoDBAnnotationConstants.QID).toString(); 
			Pair<String,String> id = new Pair<String,String>(qid,aid);
			List<Document> qAnnotations = doc.get(MongoDBAnnotationConstants.Q_ANNOTATION, List.class);
			List<Document> aAnnotations = doc.get(MongoDBAnnotationConstants.A_ANNOTATION, List.class);
			Pair<List<WikipediaAnnotation>, List<WikipediaAnnotation>> annotations = new Pair<List<WikipediaAnnotation>, List<WikipediaAnnotation>>(getWikipediaAnnotations(qAnnotations),
					getWikipediaAnnotations(aAnnotations));
			ramAnnotationsCache.put(id, annotations);
		}
		
	
		
	}
	
	@Override
	public boolean containsAnnotation(String qid, String aid) {
		return ramAnnotationsCache.containsKey(new Pair<String,String>(qid,aid));
		
	}
	
	@Override
	public void addQuestionAnswerAnnotation(String qid, String aid,
			List<WikipediaAnnotation> questionAnnotation,
			List<WikipediaAnnotation> answerAnnotation) {
	
	}
	
	

	
	@Override
	public List<WikipediaAnnotation> getQuestionAnnotation(String qid,
			String aid) {
		return ramAnnotationsCache.get(new Pair<String,String>(qid,aid)).getA();
		
	}
	@Override
	public List<WikipediaAnnotation> getAnswerAnnotation(String qid, String aid) {
		return ramAnnotationsCache.get(new Pair<String,String>(qid,aid)).getB();	
	}

	@Override
	public void close() {
		mongo.close();
	}

	@Override
	public void flush() throws IOException {
	
	}
	
}
