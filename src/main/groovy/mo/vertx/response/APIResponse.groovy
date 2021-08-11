package mo.vertx.response

import io.vertx.ext.web.RoutingContext

class APIResponse {
    int statusCode
    String result

    static void handleResponse(RoutingContext routingContext, APIResponse response) {
        routingContext.response()
                .putHeader("Content-Type", "application/json")
                .setStatusCode(response.statusCode)
                .end(response.result)
    }
}
