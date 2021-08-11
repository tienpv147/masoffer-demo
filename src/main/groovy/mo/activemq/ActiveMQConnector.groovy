package mo.activemq

import groovy.util.logging.Slf4j
import mo.constants.AppProperties
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.jms.pool.PooledConnectionFactory

@Slf4j
class ActiveMQConnector {
    private static final String AMQ_BROKER_URL = AppProperties.AMQ_BROKER_URL
    private static final String AMQ_USERNAME = AppProperties.AMQ_USERNAME
    private static final String AMQ_PASSWORD = AppProperties.AMQ_PASSWORD
    private static final Integer AMQ_MAX_CONNECTION_POOL = AppProperties.AMQ_MAX_CONNECTION_POOL

    static ActiveMQConnectionFactory createAMQConnectionFactory() {
        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                userName: AMQ_USERNAME,
                password: AMQ_PASSWORD,
                brokerURL: AMQ_BROKER_URL
        )
        return connectionFactory
    }

    static PooledConnectionFactory createPooledConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory(
                connectionFactory: connectionFactory,
                maxConnections: AMQ_MAX_CONNECTION_POOL
        )
        return pooledConnectionFactory
    }
}
