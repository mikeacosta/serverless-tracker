package net.postcore.tracker.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;

import net.postcore.tracker.models.LambdaResponse;
import net.postcore.tracker.models.Product;

public class TrackerGetProducts implements RequestHandler<Object, LambdaResponse<List<Product>>> {

	final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
			.withRegion("us-west-2")
	        .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
	        .build();
	
	@Override
	public LambdaResponse<List<Product>> handleRequest(Object input, Context context) {
		
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		PaginatedScanList<Product> scanList = mapper.scan(Product.class, new DynamoDBScanExpression());
		List<Product> list = new ArrayList<Product>(scanList.size());
		Iterator<Product> iterator = scanList.iterator();
		
		while(iterator.hasNext()) {
			Product product = iterator.next();
			list.add(product);
		}
		
		LambdaResponse<List<Product>> response = new LambdaResponse<List<Product>>(list);
		
		return response;
	}

}
