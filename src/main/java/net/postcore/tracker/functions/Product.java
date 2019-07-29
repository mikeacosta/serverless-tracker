package net.postcore.tracker.functions;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Products")
public class Product {
	
	int id;
	String name;
	Double price;
	int quantity;
	
	public Product() { }
	
	public Product(int id, String name, Double price, int quantity) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
	}

	@DynamoDBHashKey(attributeName = "id")  
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@DynamoDBAttribute(attributeName = "name") 
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@DynamoDBAttribute(attributeName = "price") 
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	@DynamoDBAttribute(attributeName = "quantity") 
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}		

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", price=" + price + ", quantity=" + quantity + "]";
	}

}
