package mo.constants

class AppProperties {
    static final Properties APP_PROPERTIES = getProperties()

    // VertX application
    static final Integer SERVER_PORT = APP_PROPERTIES.getProperty("server.port") as Integer

    // ActiveMQ
    static final String AMQ_BROKER_URL = APP_PROPERTIES.getProperty("activemq.broker.url")
    static final String AMQ_USERNAME = APP_PROPERTIES.getProperty("activemq.username")
    static final String AMQ_PASSWORD = APP_PROPERTIES.getProperty("activemq.password")
    static final String AMQ_QUEUE = APP_PROPERTIES.getProperty("activemq.queue")
    static final Integer AMQ_MAX_CONNECTION_POOL = APP_PROPERTIES.getProperty("activemq.max-connection-pools") as Integer
    static final Integer AMQ_CONSUMER_TIMEOUT = APP_PROPERTIES.getProperty("activemq.consumer-timeout") as Integer

    // MongoDB
    static final String CONNECT_URI = APP_PROPERTIES.getProperty("connect.uri")
    static final String DB_NAME = APP_PROPERTIES.getProperty("db.name")
    static final String COLLECTION_NAME = APP_PROPERTIES.getProperty("collection.name")

    // Restful API
    static final Integer OK = APP_PROPERTIES.getProperty("http-status.ok") as Integer
    static final Integer CREATED = APP_PROPERTIES.getProperty("http-status.created") as Integer
    static final Integer INTERNAL_SERVER_ERROR = APP_PROPERTIES.getProperty("http-status.internal-server-error") as Integer
    static final String API_PRODUCES = APP_PROPERTIES.getProperty("api.produces")

    static Properties getProperties() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader()
        Properties properties = new Properties()
        InputStream inputStream = loader.getResourceAsStream("app.properties")
        properties.load(inputStream)
        inputStream.close()

        return properties
    }
}
