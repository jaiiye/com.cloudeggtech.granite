package com.cloudeggtech.granite.cluster.dba;

import com.mongodb.client.MongoDatabase;

public interface IDbInitializer {
	void initialize(MongoDatabase database);
}
