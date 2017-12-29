package com.cloudeggtech.granite.cluster.auth;

import org.bson.Document;

import com.cloudeggtech.granite.cluster.dba.IDbInitializer;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

public class DbInitializer implements IDbInitializer {

	@Override
	public void initialize(MongoDatabase database) {
		if (collectionExistsInDb(database, "users"))
			return;
		
		database.createCollection("users");
		MongoCollection<Document> users = database.getCollection("users");
		users.createIndex(Indexes.ascending("name"));
	}

	private boolean collectionExistsInDb(MongoDatabase database, String collectionName) {
		MongoCursor<String> cursor = database.listCollectionNames().iterator();
		while (cursor.hasNext()) {
			if (collectionName.equals(cursor.next()))
				return true;
		}
		
		return false;
	}
	
}
