package mo.mongodb

import groovy.util.logging.Slf4j
import mo.constants.AppProperties

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient

@Slf4j
class MongoDBConnector {
    private static final String CONNECT_URI = AppProperties.CONNECT_URI
    private static final String DB_NAME = AppProperties.DB_NAME

    static MongoClient getMongoClient(Vertx vertx) {
        JsonObject config = new JsonObject()
                .put("connection_string", CONNECT_URI)
                .put("db_name", DB_NAME)

        MongoClient mongoClient = MongoClient.createShared(vertx, config)

        mongoClient.getCollections { asyncRes ->
            if (asyncRes.succeeded()) {
                log.info("=== Connect to database dbtest successfully ===")
            } else {
                log.error(asyncRes.cause().toString())
            }
        }
        return mongoClient
    }
}
