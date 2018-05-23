package it.unitn.nlpir.util.wiki;

import java.util.List;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * MongoDB-driven cache for features
 * @author Kateryna
 *
 */
public class Roth2013BasedFeatureListCache extends AbstractMongoDBFeatureCache implements IFeatureCache {
	

	protected static final Logger logger = LoggerFactory.getLogger(Roth2013BasedFeatureListCache.class);
	
	
	public Roth2013BasedFeatureListCache(String dbName, String host, int port, String collectionName){
		super(dbName,host,port,collectionName);
		
		initCache(CACHE_SIZE);
	}
	
	
	
	protected void initCache(int size){

		logger.info("Initializing the feature cache");
		for (Document doc : table.find()){
			
			
			List<Document> qAnnotations = doc.get(MongoDBAnnotationConstants.Q_ANNOTATION, List.class);
			populateClassesFromAnnotations(qAnnotations);
			
			List<Document> aAnnotations = doc.get(MongoDBAnnotationConstants.A_ANNOTATION, List.class);
			populateClassesFromAnnotations(aAnnotations);
		}
		
		
	}

	

	protected void populateClassesFromAnnotations(List<Document> annotations) {
		for (Document d : annotations) {
			List<String> attributes = d.get(MongoDBAnnotationConstants.ENTITY_ATTRIBUTES_S, List.class);
			String wikipage = d.getString(MongoDBAnnotationConstants.ENTITY_WIKI_TITLE_S);
			featureCache.put(wikipage, attributes);
		}
	}
	
	



	
}
