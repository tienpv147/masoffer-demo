package mo

import mo.vertx.MainVerticle
import groovy.util.logging.Slf4j
import io.vertx.core.Vertx

@Slf4j
class App {
    static void main(String[] args) {
        Vertx vertx = Vertx.vertx()
        vertx.deployVerticle(new MainVerticle(), { asyncRes ->
            if (asyncRes.succeeded()) {
                log.info("Deployment successful!")
            } else {
                log.error("Deployment failed: ", asyncRes.cause())
            }
        })
    }
}
