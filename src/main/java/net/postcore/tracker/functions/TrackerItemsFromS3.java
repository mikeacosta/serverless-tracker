package net.postcore.tracker.functions;

import com.amazonaws.services.lambda.runtime.Context;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.gson.Gson;

import java.util.HashMap;

public class TrackerItemsFromS3 implements RequestHandler<S3Event, String> {
    
    private final Region region = Region.US_WEST_2;
    private final S3Client s3Client = S3Client.builder().region(region).build();
    private final DynamoDbClient dbClient = DynamoDbClient.builder().region(region).build();

	@Override
    public String handleRequest(S3Event event, Context context) {
        context.getLogger().log("Received event: " + event.toJson());
        
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        
        ResponseInputStream<?> objectData = s3Client.getObject(GetObjectRequest.builder()
        		.bucket(bucket)
        		.key(key)
        		.build());
        
        InputStreamReader isr = new InputStreamReader(objectData);
        BufferedReader br = new BufferedReader(isr); 

        Product [] products = null;

		Gson gson = new Gson();
		products = gson.fromJson(br, Product[ ].class);
		
		for (Product product: products) {
			AddProduct(product, context);
			
		}
		
		return "Products from " + key + " added to Products table";
        
    }
    
    private void AddProduct(Product product, Context context) {
    	
    	try {
			HashMap<String, AttributeValue> map = new HashMap<>();
            
		    map.put("id", AttributeValue.builder()
		    	      .n(Integer.toString(product.id)).build());		    
		    map.put("name", AttributeValue.builder()
		    	      .s(product.name).build()); 		    
		    map.put("price", AttributeValue.builder()
		    	      .n(Double.toString(product.price)).build());	
		    map.put("quantity", AttributeValue.builder()
		    	      .n(Integer.toString(product.quantity)).build());		    
						
		    PutItemRequest request = PutItemRequest.builder()
		    		.tableName("Products").item(map).build();
		    
		    dbClient.putItem(request);   
		    
    	} catch (Exception e) {
    		
	          e.printStackTrace();
	          context.getLogger().log(String.format(
	              "Error adding " + product.id + " " + product.name + " to Products table"));
	          throw e;    		
    	}
    }
}