package ws.cogito.microservices;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


/**
 * Event Auditing Route Builder
 */
@Component
public class EventAuditingRouteBuilder extends RouteBuilder implements RoutesBuilder {

    public void configure() {
    	
    	/**
    	 * Route errors to DLQ after one retry and one second delay
    	 */
    	errorHandler(deadLetterChannel("activemq:event.audit.dlq").
    			maximumRedeliveries(1).redeliveryDelay(1000));
    	
    	/**
    	 * Audit Storage (Mock) - Normally would use something like Elasticsearch
    	 */
    	from("activemq:event.audit").
		process(new TrackingIdProcessor()).
		wireTap("activemq:event.cep").
		to("file:target/events?fileName=event-${in.header.TrackingID}.json");
    }
}