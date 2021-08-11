package mo.vertx

import groovy.util.logging.Slf4j
import io.vertx.core.AbstractVerticle
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import mo.activemq.ActiveMQConnector
import mo.activemq.JmsConsumer
import mo.activemq.JmsProducer
import mo.constants.AppProperties
import mo.mongodb.MongoCRUD
import mo.mongodb.MongoDBConnector
import org.apache.activemq.ActiveMQConnectionFactory

@Slf4j
class MainVerticle extends AbstractVerticle {
    private static final Integer SERVER_PORT = AppProperties.SERVER_PORT
    private static final String API_PRODUCES = AppProperties.API_PRODUCES

    @Override
    void start() {
        // Connect to MongoDB
        MongoClient mongoClient = MongoDBConnector.getMongoClient(vertx)

        // Connect to ActiveMQ
        ActiveMQConnectionFactory activeMQConnectionFactory = ActiveMQConnector.createAMQConnectionFactory()

        // Set up Route
        Router router = Router.router(vertx)
        router.route().handler(BodyHandler.create()).produces(API_PRODUCES)
        router.post("/document/create").handler({ routingContext -> MongoCRUD.insertDocument(mongoClient, routingContext) })
        router.get("/document/find").handler({ routingContext -> MongoCRUD.findDocument(mongoClient, routingContext) })
        router.put("/document/update").handler({ routingContext -> MongoCRUD.updateDocument(mongoClient, routingContext) })
        router.delete("/document/delete").handler({ routingContext -> MongoCRUD.deleteDocument(mongoClient, routingContext) })
        router.post("/message/enqueue").handler({ routingContext -> JmsProducer.produceMessage(activeMQConnectionFactory, routingContext) })
        router.post("/message/dequeue").handler({ routingContext -> JmsConsumer.consumeMessage(activeMQConnectionFactory, routingContext) })

        vertx.createHttpServer().requestHandler(router)
                .listen(SERVER_PORT, { result ->
                    if (result.succeeded()) {
                        log.info("HTTP server running on port ${SERVER_PORT}")
                    } else {
                        log.error("Could not start a HTTP server: ", result.cause())
                    }
                })
    }

    @Override
    void stop() {
        log.info("Shutting down application")
    }
}
