package org.common.email.service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Iterator;

@Component
public class ScheduleTasks {
    MongoClient mongo = MongoClients.create("mongodb://localhost:27017");

    // Accessing the database
    MongoDatabase database = mongo.getDatabase("inventory");

    @Autowired
    private EmailService emailService;

    private Document getWarehouseObject(int warehouseId) {
        BasicDBObject fields = new BasicDBObject();
        fields.put("warehouseId", warehouseId);
        System.out.println(warehouseId);
        MongoCollection<Document> warehouseCollection = database.getCollection("warehouse");
        FindIterable<Document> warehouseIter = warehouseCollection.find(fields);

        Iterator warehouseIt = warehouseIter.iterator();
        while (warehouseIt.hasNext()) {
            return (Document)(warehouseIt.next());
        }
        return null;
    }

    private String getInvoiceBody(int numOfItems, String accountType){
        int pricePerSensor = 10;
        int premiumAmount = 15;
        int totalMonthBill = 0;
        if (accountType == "R"){
             totalMonthBill = numOfItems * pricePerSensor;
        } else {
            totalMonthBill = numOfItems * premiumAmount;
        }

        return "Your Total amount due for this month is: "+totalMonthBill;
    }

    @Scheduled(cron = "0 * * * * ?")
    public void scheduleTaskWithCronExpression() throws MessagingException {

        // Retrieving a collection
        MongoCollection<Document> collection = database.getCollection("user");
        System.out.println("Collection sampleCollection selected successfully");

        // Getting the iterable object
        FindIterable<Document> iterDoc = collection.find();
        int i = 1;

        // Getting the iterator
        Iterator it = iterDoc.iterator();

        while (it.hasNext()) {
            Document doc = (Document) it.next();
            System.out.println(doc.get("email"));
            Document wareHouseDoc = getWarehouseObject((Integer)doc.get("wareHouseId"));
            System.out.println(wareHouseDoc.get("items"));
            ArrayList<Document> items = (ArrayList<Document>) wareHouseDoc.get("items");
            System.out.println(getInvoiceBody(items.size(), doc.get("accountType").toString()));
            emailService.sendMail(doc.get("email").toString(), "Monthly Invoice", getInvoiceBody(items.size(), doc.get("accountType").toString()));
            i++;
        }
    }
}
