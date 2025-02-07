package jsonParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;

public class HPParser {
	public static void main(String[] args) throws IOException {
		JsonFactory f = new MappingJsonFactory();
	    JsonParser jp = f.createParser(new File("log-edited.json"));
	    Map<String,Spawn> spawnObject = new HashMap<String, Spawn>();
	    Map<String,String> obstacles = new HashMap<String, String>();
	    Map<String,String> enemies = new HashMap<String, String>();

	    
	    JsonFactory factory = new JsonFactory();
        JsonGenerator generator = factory.createGenerator(new FileWriter(new File("hpSummary2.json")));
        
	    JsonToken current;

	    current = jp.nextToken();
	    if (current != JsonToken.START_OBJECT) {
	      System.out.println("Error: root should be object: quiting.");
	      return;
	    }
	    
	    while (jp.nextToken() != JsonToken.END_OBJECT) {
	    	String fieldName = jp.getCurrentName();
	        // move from field name to field value
	        current = jp.nextToken();
	        if (fieldName.equals("logs")) {
	        	if (current == JsonToken.START_OBJECT) {
	        		while (jp.nextToken() != JsonToken.END_OBJECT) {
	        			String fieldName2 = jp.getCurrentName();
	        			current = jp.nextToken();
	        			if(fieldName2.equals("logs")){
	        				if (current == JsonToken.START_ARRAY) {
	        					// For each of the records in the array
	        			        int i = 1; 
	        			        int missedBonus = 0;
	        			        int missedEnemy = 0;
	        			        int catchedBonus = 0;
	        			        int dodgeObstacle = 0;
	        			        int collidedObstacle = 0;
	        			        int collidedEnemy = 0;
	        			        int killEnemy = 0;
	        			        int hitEnemy = 0;
	        			        int hurtEnemy = 0;
	        			        int totalEnemies = 0;
	        			        int totalBonuses = 0;
	        			        int totalObstacles = 0;
	        			        
	        			        //Start Writing Object
//	        			        generator.writeStartObject();
//	        			        generator.writeFieldName("patientID");
//	        			        generator.writeString("xxxxx");
//	        			        generator.writeFieldName("sessionID");
//	        			        generator.writeString("xxxxx");
//	        			        generator.writeFieldName("event");
	        			        generator.writeStartArray();
	        					while (jp.nextToken() != JsonToken.END_ARRAY) {	        					  
	        			        	  JsonNode node = jp.readValueAsTree();
	        			              // And now we have random access to everything in the object
	        			              //System.out.println("type " + node.get("type").asText());
	        			        	  if(node.get("type").asText().equals("Event")){
	        			        		  //System.out.println("There is an event");
	        			        		  JsonNode data = node.get("data");
	        			        		  
	        			        		  //If it is a spawn, add it to Array
	        			        		  if(data.get("type").asText().equals("Spawn")){
	        			        			  JsonNode arg = data.get("arguments");
	        			        			  JsonNode pos = data.get("position");

        			        				  Spawn spawn = new Spawn();
        			        				  spawn.spawnType = arg.get(0).asText();
        			        				  spawn.apparitionTime = node.get("timestamp").asText();
        			        				  spawn.apparitionY = pos.get(1).asText();
        			        				  spawnObject.put(data.get("id").asText(), spawn);
        			        				  if(spawn.spawnType.equals("Enemy")){
        			        					  totalEnemies++;
        			        				  }else if(spawn.spawnType.equals("Bonus")){
        			        					  totalBonuses++;
        			        				  }else if(spawn.spawnType.equals("Obstacle")){
        			        					  totalObstacles++;
        			        				  }
	        			        			          			        			  
	        			        		  }
	        			        		  
	        			        		  if(data.get("type").asText().equals("Catch")){
	        			        			  JsonNode pos = data.get("position");
	        			        			  //System.out.println(i + " Catching Bonuses at time:" + node.get("timestamp")+ "; with id: "+ data.get("id")+"; at x: "+ pos.get(0)+"; at y: "+ pos.get(1));
	        			        			  catchedBonus++;
	        			        			  generator.writeStartObject();
	        			        			  generator.writeFieldName("spawnID");
	        		        			      generator.writeString(data.get("id").asText());
	        		        			      generator.writeFieldName("spawnType");
	        		        			      generator.writeString("Bonus");
	        		        			      generator.writeFieldName("eventType");
	        		        			      generator.writeString("Catch");
	        		        			      generator.writeFieldName("eventCat");
	        		        			      generator.writeString("Positive");
	        		        			      generator.writeFieldName("x");
	        		        			      generator.writeString(pos.get(0).asText());
	        		        			      
	        		        			      Spawn sp = spawnObject.get(data.get("id").asText());
	        		        			      BigDecimal startY = new BigDecimal(sp.apparitionY);
	        		        			      BigDecimal endY = new BigDecimal(pos.get(1).asText());
	        		        			      BigDecimal startTime = new BigDecimal(sp.apparitionTime);
	        		        			      BigDecimal endTime = new BigDecimal(node.get("timestamp").asText());
	        		        			      BigDecimal timeToReact = (endTime.subtract(startTime));
	        		        			      //BigDecimal screenV = ((endY.subtract(startY)).divide(endTime.subtract(startTime), 5, RoundingMode.HALF_UP));
	        		        			      generator.writeFieldName("timeToReact");
	        		        			      generator.writeString(timeToReact.toString());
	        		        			      generator.writeFieldName("time");
	        		        			      generator.writeString(node.get("timestamp").asText());
	        			        			  generator.writeEndObject();
	        			        			  //i++;
	        			        		  }
	        			        		  if(data.get("type").asText().equals("Miss")){
	        			        			  JsonNode pos = data.get("position");
	        			        			  Spawn sp = spawnObject.get(data.get("id").asText());
	        			        			  //System.out.println(i + " Missing "+sp.spawnType+" at time:" + node.get("timestamp")+ "; with id: "+ data.get("id")+"; at x: "+ pos.get(0)+"; at y: "+ pos.get(1));
	        			        			  generator.writeStartObject();
	        			        			  generator.writeFieldName("spawnID");
	        		        			      generator.writeString(data.get("id").asText());
	        		        			      generator.writeFieldName("spawnType");     	
	        		        			      
//	        		        			      BigDecimal startY = new BigDecimal(sp.apparitionY);
//	        		        			      BigDecimal endY = new BigDecimal(pos.get(1).asText());
//	        		        			      BigDecimal startTime = new BigDecimal(sp.apparitionTime);
//	        		        			      BigDecimal endTime = new BigDecimal(node.get("timestamp").asText());
//	        		        			      BigDecimal timeToReact = (endTime.subtract(startTime));
//	        		        			      BigDecimal screenV = ((endY.subtract(startY)).divide(endTime.subtract(startTime), 5, RoundingMode.HALF_UP));
//	        		        			      System.out.println("Speed: "+screenV.toString()+","+startY.toString()+","+endY.toString()+","+startTime.toString()+","+endTime.toString());

	        			        			  if(sp.spawnType.equals("Enemy")){
	        			        				  missedEnemy++;
	        			        				  generator.writeString("Enemy");
	        			        				  generator.writeFieldName("eventType");
	        			        				  generator.writeString("Miss");
		        		        			      generator.writeFieldName("eventCat");
		        		        			      generator.writeString("Neutral");
		        			        			  generator.writeFieldName("x");
		        		        			      generator.writeString(pos.get(0).asText());
		        		        			      generator.writeFieldName("y");
		        		        			      generator.writeString(pos.get(2).asText());
	        			        			  }else{
	        			        				  missedBonus++;
	        			        				  generator.writeString("Bonus");
	        			        				  generator.writeFieldName("eventType");
	        			        				  generator.writeString("Miss");
		        		        			      generator.writeFieldName("eventCat");
		        		        			      generator.writeString("Neutral");
		        			        			  generator.writeFieldName("x");
		        		        			      generator.writeString(pos.get(0).asText());

	        			        			  }
	        			        			  generator.writeFieldName("timeToReact");
	        		        			      generator.writeString("99");
	        		        			      generator.writeFieldName("time");
	        		        			      generator.writeString(node.get("timestamp").asText());
	        			        			  generator.writeEndObject();
	        			        			  //i++;
	        			        		  }
	        			        		  if(data.get("type").asText().equals("Dodge")){
	        			        			  JsonNode pos = data.get("position");
	        			        			  //System.out.println(i + " Dodge Obstacle at time:" + node.get("timestamp")+ "; with id: "+ data.get("id")+"; at x: "+ pos.get(0)+"; at y: "+ pos.get(1));
	        			        			  dodgeObstacle++;
	        			        			  generator.writeStartObject();
	        			        			  generator.writeFieldName("spawnID");
	        		        			      generator.writeString(data.get("id").asText());
	        		        			      generator.writeFieldName("spawnType");
	        		        			      generator.writeString("Obstacle");
	        		        			      generator.writeFieldName("eventType");
	        		        			      generator.writeString("Dodge");
	        		        			      generator.writeFieldName("eventCat");
	        		        			      generator.writeString("Neutral");
	        		        			      generator.writeFieldName("x");
	        		        			      generator.writeString(pos.get(0).asText());
	        		        			      
	        		        			      Spawn sp = spawnObject.get(data.get("id").asText());
//	        		        			      BigDecimal startY = new BigDecimal(sp.apparitionY);
//	        		        			      BigDecimal endY = new BigDecimal(pos.get(1).asText());
//	        		        			      BigDecimal startTime = new BigDecimal(sp.apparitionTime);
//	        		        			      BigDecimal endTime = new BigDecimal(node.get("timestamp").asText());
//	        		        			      BigDecimal timeToReact = (endTime.subtract(startTime));
	        		        			      //BigDecimal screenV = ((endY.subtract(startY)).divide(endTime.subtract(startTime), 5, RoundingMode.HALF_UP));
	        		        			      generator.writeFieldName("timeToReact");
	        		        			      generator.writeString("99");
	        		        			      generator.writeFieldName("time");
	        		        			      generator.writeString(node.get("timestamp").asText());
	        			        			  generator.writeEndObject();
	        			        			  //i++;
	        			        		  }
//	        			        		  if(data.get("type").asText().equals("Collision")){
//	        			        			  JsonNode pos = data.get("position");
//	        			        			  //System.out.println(i + " Collided with Obstacle at time:" + node.get("timestamp")+ "; with id: "+ data.get("id")+"; at x: "+ pos.get(0)+"; at y: "+ pos.get(1));
//	        			        			  Spawn sp = bonuses.get(data.get("id").asText());
//	        			        			  if(sp.spawnType.equals("Enemy")){
//	        			        				  collidedEnemy++;
//	        			        			  }else{
//	        			        				  collidedObstacle++;
//	        			        			  }
//	        			        			  
//	        			        			  //i++;
//	        			        		  }
	        			        		  if(data.get("type").asText().equals("Kill")){
	        			        			  JsonNode pos = data.get("position");
//	        			        			  System.out.println(i + " Kill Enemy at time:" + node.get("timestamp")+ "; with id: "+ data.get("id")+"; at x: "+ pos.get(0)+"; at y: "+ pos.get(1));
	        			        			  killEnemy++;
	        			        			  
	        			        			  generator.writeStartObject();
	        			        			  generator.writeFieldName("spawnID");
	        		        			      generator.writeString(data.get("id").asText());
	        		        			      generator.writeFieldName("spawnType");
	        		        			      generator.writeString("Enemy");
	        		        			      generator.writeFieldName("eventType");
	        		        			      generator.writeString("Kill");
	        		        			      generator.writeFieldName("eventCat");
	        		        			      generator.writeString("Positive");
	        		        			      generator.writeFieldName("x");
	        		        			      generator.writeString(pos.get(0).asText());
	        		        			      
	        		        			      Spawn sp = spawnObject.get(data.get("id").asText());
//	        		        			      BigDecimal startY = new BigDecimal(sp.apparitionY);
//	        		        			      BigDecimal endY = new BigDecimal(pos.get(1).asText());
	        		        			      BigDecimal startTime = new BigDecimal(sp.apparitionTime);
	        		        			      BigDecimal endTime = new BigDecimal(node.get("timestamp").asText());
	        		        			      BigDecimal timeToReact = (endTime.subtract(startTime));
	        		        			      //BigDecimal screenV = ((endY.subtract(startY)).divide(endTime.subtract(startTime), 5, RoundingMode.HALF_UP));
	        		        			      generator.writeFieldName("timeToReact");
	        		        			      generator.writeString(timeToReact.toString());
	        		        			      generator.writeFieldName("time");
	        		        			      generator.writeString(node.get("timestamp").asText());
	        			        			  generator.writeEndObject();
//	        			        			  i++;
	        			        		  }
	        			        		  if(data.get("type").asText().equals("Hit")){
	        			        			  JsonNode pos = data.get("position");
//	        			        			  System.out.println(i + " Hit Enemy at time:" + node.get("timestamp")+ "; with id: "+ data.get("id")+"; at x: "+ pos.get(0)+"; at y: "+ pos.get(1));
	        			        			  hitEnemy++;
	        			        			  
	        			        			  generator.writeStartObject();
	        			        			  generator.writeFieldName("spawnID");
	        		        			      generator.writeString(data.get("id").asText());
	        		        			      generator.writeFieldName("spawnType");
	        		        			      generator.writeString("Enemy");
	        		        			      generator.writeFieldName("eventType");
	        		        			      generator.writeString("Hit");
	        		        			      generator.writeFieldName("eventCat");
	        		        			      generator.writeString("Positive");
	        		        			      generator.writeFieldName("x");
	        		        			      generator.writeString(pos.get(0).asText());
	        		        			      
	        		        			      Spawn sp = spawnObject.get(data.get("id").asText());
//	        		        			      BigDecimal startY = new BigDecimal(sp.apparitionY);
//	        		        			      BigDecimal endY = new BigDecimal(pos.get(1).asText());
	        		        			      BigDecimal startTime = new BigDecimal(sp.apparitionTime);
	        		        			      BigDecimal endTime = new BigDecimal(node.get("timestamp").asText());
	        		        			      BigDecimal timeToReact = (endTime.subtract(startTime));
	        		        			      //BigDecimal screenV = ((endY.subtract(startY)).divide(endTime.subtract(startTime), 5, RoundingMode.HALF_UP));
	        		        			      generator.writeFieldName("timeToReact");
	        		        			      generator.writeString(timeToReact.toString());
	        		        			      generator.writeFieldName("time");
	        		        			      generator.writeString(node.get("timestamp").asText());
	        			        			  generator.writeEndObject();

//	        			        			  i++;
	        			        		  }
//	        			        		  if(data.get("type").asText().equals("Hurt")){
//	        			        			  JsonNode pos = data.get("position");
//	        			        			  System.out.println(i + " Hurt Enemy at time:" + node.get("timestamp")+ "; with id: "+ data.get("id")+"; at x: "+ pos.get(0)+"; at y: "+ pos.get(1));
//	        			        			  hurtEnemy++;
//	        			        			  i++;
//	        			        		  }
        			        			  
	        			        	  }          			        	  
	        			        }
//	        					System.out.println("Total Catched Bonus: "+ catchedBonus);
//      			        	  	System.out.println("Total Missed Bonus: "+ missedBonus);
//      			        	  	System.out.println("Total Missed Enemy: "+ missedEnemy);
//      			        	  	System.out.println("Total Dodge Obstacle: "+ dodgeObstacle);
//      			        	  	System.out.println("Total Collision Enemy: "+ collidedEnemy);
//      			        	  	System.out.println("Total Collision Obstacle: "+ collidedObstacle);
//      			        	  	System.out.println("Total Killed Enemy: "+ killEnemy);
//    			        	  	System.out.println("Total Hit Enemy: "+ hitEnemy);
//    			        	  	System.out.println("Total Hurt Enemy: "+ hurtEnemy);
//	        					System.out.println("Total Enemies: " + totalEnemies);
//	        					System.out.println("Total Bonus: " + totalBonuses);
//	        					System.out.println("Total Obstacles: " + totalObstacles);
	        					generator.writeEndArray();
//	        					generator.writeFieldName("totalPositiveBonus");
//	        			        generator.writeString(String.valueOf(catchedBonus));
//	        			        generator.writeFieldName("totalNeutralBonus");
//	        			        generator.writeString(String.valueOf(missedBonus));
//	        			        generator.writeFieldName("totalNeutralEnemy");
//	        			        generator.writeString(String.valueOf(missedEnemy));
//	        					generator.writeEndObject();
	        			        
	        			        generator.close();
	        				}
	        				break;
	        			}
	        			else{
	        				 //System.out.println("Unprocessed property: " + fieldName2);
	        			}
	        		}
	        	}
	        	break;
	        }
	        else {
	            //System.out.println("Unprocessed property: " + fieldName);
	            jp.skipChildren();
	        }
	    }
	}
}
