package com.lirmm.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;


import com.lirmm.model.Session;

@Repository
public class SessionRepository {
public static final String COLLECTION_NAME = "patientSession2";

	
	@Autowired
    private MongoTemplate mongoTemplate;
	
	public  List<Session> getSessionByPatientID(String patientID) {
	    return mongoTemplate.find(
	    		Query.query(Criteria.where("patientID").is(patientID)), Session.class, COLLECTION_NAME);
	}
}