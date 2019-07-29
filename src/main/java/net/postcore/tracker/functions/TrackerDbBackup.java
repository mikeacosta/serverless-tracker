package net.postcore.tracker.functions;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.CreateBackupRequest;

public class TrackerDbBackup implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);

        Region region = Region.US_WEST_2;
        DynamoDbClient client = DynamoDbClient.builder().region(region).build();
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String backupName = "Products_bu_" + timeStamp;
        CreateBackupRequest request = CreateBackupRequest.builder()
        		.tableName("Products")
        		.backupName(backupName)
        		.build();
        client.createBackup(request);
        
        return "Products backup '" + backupName + "' created.";
    }

}
