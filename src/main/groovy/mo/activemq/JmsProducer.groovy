package mo.activemq

import groovy.util.logging.Slf4j
import io.vertx.ext.web.RoutingContext
import mo.constants.AppProperties
import mo.vertx.response.APIResponse
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.jms.pool.PooledConnectionFactory

import javax.jms.Connection
import javax.jms.Destination
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.MessageProducer
import javax.jms.Session

import static mo.vertx.response.APIResponse.handleResponse

@Slf4j
class JmsProducer {
    private static final String AMQ_QUEUE = AppProperties.AMQ_QUEUE
    private static final Integer OK_STATUS = AppProperties.OK
    private static final Integer INTERNAL_SERVER_ERROR_STATUS = AppProperties.INTERNAL_SERVER_ERROR

    static void produceMessage(ActiveMQConnectionFactory connectionFactory, RoutingContext routingContext) {
        final PooledConnectionFactory pooledConnectionFactory = ActiveMQConnector.createPooledConnectionFactory(connectionFactory)
        Connection producerConnection
        APIResponse response = new APIResponse(statusCode: OK_STATUS, result: "")
        try {
            producerConnection = pooledConnectionFactory.createConnection()
            producerConnection.start()
            final Session session = producerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)
            final Destination queue = session.createQueue(AMQ_QUEUE)
            final MessageProducer producer = session.createProducer(queue)
            Message message = session.createTextMessage(routingContext.getBodyAsJson().toString())
            producer.send(message)
            log.info("Sent message to queue ${AMQ_QUEUE}: {}", message.toString())
            response.result = "Sent message to queue ${AMQ_QUEUE} successfully"

            producer.close()
            session.close()
            producerConnection.close()
        } catch (JMSException ignored) {
            log.error("JMS message producer error")
            ignored.printStackTrace()
            response.result = "Sending message failed"
            response.statusCode = INTERNAL_SERVER_ERROR_STATUS
        }
        handleResponse(routingContext, response)
    }
}
