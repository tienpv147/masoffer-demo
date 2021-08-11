package mo.mongodb

import groovy.util.logging.Slf4j
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.RoutingContext
import mo.constants.AppProperties
import mo.vertx.response.APIResponse

import static mo.vertx.response.APIResponse.handleResponse

@Slf4j
class MongoCRUD {
    private static final String COLLECTION_NAME = AppProperties.COLLECTION_NAME
    private static final Integer OK_STATUS = AppProperties.OK
    private static final Integer INTERNAL_SERVER_ERROR_STATUS = AppProperties.INTERNAL_SERVER_ERROR

    static void insertDocument(MongoClient mongoClient, RoutingContext routingContext) {
        APIResponse response = new APIResponse(statusCode: OK_STATUS, result: "")
        mongoClient.insert(COLLECTION_NAME, routingContext.getBodyAsJson(), { req ->
            if (req.succeeded()) {
                response.result = "Inserted document with id: " + routingContext.getBodyAsJson().getInteger("_id")
            } else {
                response.statusCode = INTERNAL_SERVER_ERROR_STATUS
                response.result = "Insert document failed"
            }
            handleResponse(routingContext, response)
        })
    }

    static void findDocument(MongoClient mongoClient, RoutingContext routingContext) {
        APIResponse response = new APIResponse(statusCode: OK_STATUS, result: "")
        mongoClient.findBatch(COLLECTION_NAME, routingContext.getBodyAsJson())
                .exceptionHandler({ ex ->
                    ex.printStackTrace()
                    response.result = "System error"
                    response.statusCode = INTERNAL_SERVER_ERROR_STATUS
                    handleResponse(routingContext, response)
                })
                .handler({ doc ->
                    response.result = doc.encodePrettily()
                })
                .endHandler({
                    if (response.result.isEmpty()) {
                        response.result = "Cannot found any documents"
                    }
                    handleResponse(routingContext, response)
                })
    }

    static void updateDocument(MongoClient mongoClient, RoutingContext routingContext) {
        APIResponse response = new APIResponse(statusCode: OK_STATUS, result: "")
        JsonObject requestBody = routingContext.getBodyAsJson()
        JsonObject query = requestBody.getJsonObject("query_doc")
        JsonObject updateFields = requestBody.getJsonObject("update_fields")
        JsonObject update = new JsonObject().put("\$set", updateFields)
        mongoClient.updateCollection(COLLECTION_NAME, query, update, { req ->
            if (req.succeeded()) {
                response.result = "Updated document"
            } else {
                response.statusCode = INTERNAL_SERVER_ERROR_STATUS
                response.result = "Update document failed"
            }
            handleResponse(routingContext, response)
        })
    }

    static void deleteDocument(MongoClient mongoClient, RoutingContext routingContext) {
        APIResponse response = new APIResponse(statusCode: OK_STATUS, result: "")
        mongoClient.findOneAndDelete(COLLECTION_NAME, routingContext.getBodyAsJson(), { req ->
            if (req.succeeded()) {
                response.result = "Deleted document with id " + routingContext.getBodyAsJson().getInteger("id")
            } else {
                response.statusCode = INTERNAL_SERVER_ERROR_STATUS
                response.result = "Delete document failed, please check your update operators"
            }
            handleResponse(routingContext, response)
        })
    }
}
