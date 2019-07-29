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

import software.amazon.awssdk.regions.Region;

public class TrackerGetProducts implements RequestHandler<Object, HttpResponse<List<Product>>> {

	final Region region = Region.US_WEST_2;
	final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	
	@Override
	public HttpResponse<List<Product>> handleRequest(Object input, Context context) {
		
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		PaginatedScanList<Product> scanList = mapper.scan(Product.class, new DynamoDBScanExpression());
		List<Product> list = new ArrayList<Product>(scanList.size());
		Iterator<Product> iterator = scanList.iterator();
		
		while(iterator.hasNext()) {
			Product product = iterator.next();
			list.add(product);
		}
		
		HttpResponse<List<Product>> response = new HttpResponse<List<Product>>(list);
		
		return response;
	}

}
