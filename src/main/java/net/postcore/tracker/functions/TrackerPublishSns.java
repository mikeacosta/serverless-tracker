package net.postcore.tracker.functions;

import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;

public class TrackerPublishSns implements RequestHandler<DynamodbEvent, Integer> {

    @Override
    public Integer handleRequest(DynamodbEvent event, Context context) {
        context.getLogger().log("Received event: " + event);
        
        for (DynamodbStreamRecord record : event.getRecords()) {
            context.getLogger().log(record.getEventID());
            context.getLogger().log(record.getEventName());
            context.getLogger().log(record.getDynamodb().toString());
            
            if (!record.getEventName().equalsIgnoreCase("modify"))
            	continue;
            
            Map<String, AttributeValue> map = record.getDynamodb().getNewImage();
            
            if (!map.containsKey("quantity"))
            	continue;
            
            String id = record.getDynamodb().getKeys().get("id").toString();
            String quantity = record.getDynamodb().getKeys().get("quantity").toString();
            
            context.getLogger().log("Product: " + id + " quantity changed to: " +  quantity);
        }
        
        return event.getRecords().size();
    }
}