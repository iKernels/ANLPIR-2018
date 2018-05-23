package it.unitn.nlpir.util.wiki;

import java.io.IOException;
import java.util.List;
import org.apache.commons.collections.map.LRUMap;
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
public abstract class AbstractMongoDBFeatureCache implements IFeatureCache {
	protected MongoClient mongo;
	protected  MongoCollection<Document> table;
	
	protected static final int CACHE_SIZE = 400000;
	protected LRUMap featureCache;
	protected static final Logger logger = LoggerFactory.getLogger(AbstractMongoDBFeatureCache.class);
	
	
	
	public static final String DEFAULT_HOST="127.0.0.1";
	public AbstractMongoDBFeatureCache(String dbName, String host, int port, String collectionName){
		
		setConnectionAndTable(dbName, host, port, collectionName);
		featureCache = new LRUMap(CACHE_SIZE);
		
		
	}

	@Override
	public void add(String link, List<String> feats) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public List<String> get(String link) {
		if (featureCache.containsKey(link))
			return (List<String>) featureCache.get(link);
		else
			return null;
	}



	@Override
	public int getNewFeaturePoolSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected void setConnectionAndTable(String dbName, String host, int port, String collectionName) {
		try {
			mongo = new MongoClient(host, port);
		}
		catch (Exception e) {
			mongo = new MongoClient(DEFAULT_HOST, port);
		}
		
		MongoDatabase db = mongo.getDatabase(dbName);
		table = db.getCollection(collectionName);
	}
	
	
	

	
	
	
	

	
	public void close() throws IOException {
		mongo.close();
	}





	
	public void flush() throws IOException {
		// TODO Auto-generated method stub
		
	}
	
}
