package net.postcore.tracker.functions;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;
import com.google.gson.Gson;
import net.postcore.tracker.models.HttpRequest;
import net.postcore.tracker.models.HttpResponse;
import net.postcore.tracker.models.Product;

public class TrackerProductOps implements RequestHandler<HttpRequest, HttpResponse<Product>> {

	final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
			.withRegion("us-west-2")
	        .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
	        .build();
			
    @Override
    public HttpResponse<Product> handleRequest(HttpRequest input, Context context) {
        context.getLogger().log("Input: " + input.toString());
		
        Product product;
        
        try {
        	String productId = input.getPathParameters().get("id");

        	if (input.getRequestContext().getHttpMethod().equals("PUT")) {
        		product = updateProduct(productId, input, context);
        	}
        	else {
        		product = getProduct(productId, context);       		
        	}
        } catch (Exception e) {
        	context.getLogger().log("Error: " + e.getMessage());
        	context.getLogger().log("Stack trace: " + e.getStackTrace());
        	return new HttpResponse<Product>("500", e.getMessage());
        }
        
        return new HttpResponse<Product>(product);
    }
    
    private Product getProduct(String productId, Context context) {
        Product product = null;
        
		Map<String, AttributeValue> attrValues = new HashMap<String, AttributeValue>();
		attrValues.put(":val1", new AttributeValue().withN(productId));
		DynamoDBQueryExpression<Product> queryExpression = new DynamoDBQueryExpression<Product>()
				.withKeyConditionExpression("id = :val1")
				.withExpressionAttributeValues(attrValues);
		
		DynamoDBMapper mapper = new DynamoDBMapper(client);
        PaginatedQueryList<Product> list = mapper.query(Product.class, queryExpression);
       
        if (list.size() > 0)
            product = list.get(0);
        
        return product;    	
    }
    
    private Product updateProduct(String productId, HttpRequest input, Context context) {
		Product product = new Gson().fromJson(input.getBody(), Product.class);
		
		HashMap<String, AttributeValue> map = new HashMap<>();
	    map.put("id", new AttributeValue().withN(productId));	
	    map.put("name", new AttributeValue(product.getName()));
	    map.put("price", new AttributeValue().withN(Double.toString(product.getPrice())));
	    map.put("quantity", new AttributeValue().withN(Integer.toString(product.getQuantity()))); 		    
		
	    client.putItem(new PutItemRequest()
	    	    .withTableName("Products")
	    	    .withItem(map));
	    
		return getProduct(productId, context);	
    }

}
