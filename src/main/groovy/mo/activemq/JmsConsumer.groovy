package mo.activemq

import groovy.util.logging.Slf4j
import io.vertx.ext.web.RoutingContext
import mo.constants.AppProperties
import mo.vertx.response.APIResponse
import org.apache.activemq.ActiveMQConnectionFactory

import javax.jms.Connection
import javax.jms.Destination
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.MessageConsumer
import javax.jms.Session

import static mo.vertx.response.APIResponse.handleResponse

@Slf4j
class JmsConsumer {
    private static final String AMQ_QUEUE = AppProperties.AMQ_QUEUE
    private static final Integer AMQ_CONSUMER_TIMEOUT = AppProperties.AMQ_CONSUMER_TIMEOUT
    private static final Integer OK_STATUS = AppProperties.OK
    private static final Integer INTERNAL_SERVER_ERROR_STATUS = AppProperties.INTERNAL_SERVER_ERROR

    static void consumeMessage(ActiveMQConnectionFactory connectionFactory, RoutingContext routingContext) {
        Connection consumerConnection
        APIResponse response = new APIResponse(statusCode: OK_STATUS, result: "")
        try {
            consumerConnection = connectionFactory.createConnection()
            consumerConnection.start()
            final Session session = consumerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)
            final Destination consumerDestination = session.createQueue(AMQ_QUEUE)
            final MessageConsumer consumer = session.createConsumer(consumerDestination)
            final Message message = consumer.receive(AMQ_CONSUMER_TIMEOUT)
            log.info("Message consumed: {}", message.toString())
            response.result = "Message dequeued"

            consumer.close()
            session.close()
            consumerConnection.close()
        } catch(JMSException ignored) {
            log.error("JMS message consumer error")
            ignored.printStackTrace()
            response.result = "Dequeue message failed"
            response.statusCode = INTERNAL_SERVER_ERROR_STATUS
        }
        handleResponse(routingContext, response)
    }
}
