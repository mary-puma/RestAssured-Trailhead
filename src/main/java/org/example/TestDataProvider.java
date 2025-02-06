package org.example;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TestDataProvider {

    SOQLQuery soqlQuery = new SOQLQuery();
    public  JSONObject generateOpportunityJsonPayload(){
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("Name", "Mary test3");
        jsonPayload.put("StageName", "Closed won");
        jsonPayload.put("CloseDate", "2024-12-31");

        return jsonPayload;
    }


}
