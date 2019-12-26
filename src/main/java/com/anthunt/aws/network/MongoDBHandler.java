package com.anthunt.aws.network;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;

import com.anthunt.aws.network.utils.Logging;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.SocketSettings;
import com.mongodb.connection.SocketSettings.Builder;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongoCmdOptions;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.runtime.Network;

public class MongoDBHandler implements DisposableBean {
	
	private static final Logger log = Logging.getLogger(MongoDBHandler.class);
	
	private static final String BIND_IP = "localhost";
	private static final int BIND_PORT = 27017;
	
	private MongodExecutable startMongoDB(boolean isAuthEnable) throws UnknownHostException, IOException, InterruptedException {
		
		MongodExecutable mongodExecutable;
		
		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
        		.defaultsWithLogger(Command.MongoD, log)
        		.processOutput(ProcessOutput.getDefaultInstanceSilent())
        		.daemonProcess(true)
        		.build();
        
        IMongoCmdOptions mongoCmdOptions = new MongoCmdOptionsBuilder()
        		.enableAuth(isAuthEnable)
        		.build();
        
        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
            .net(new Net(BIND_IP, BIND_PORT, Network.localhostIsIPv6()))
            .replication(new Storage("mongodb", null, 0))
            .cmdOptions(mongoCmdOptions)
            .build();
 
        MongodStarter starter = MongodStarter.getInstance(runtimeConfig);
        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
        return mongodExecutable;        
	}
	
	public static MongoClient connectMongoClient(boolean isSecureMode) {
		String connectString = "mongodb://" + BIND_IP + ":" + BIND_PORT;
		if(isSecureMode) {
			connectString = "mongodb://root:root@" + BIND_IP + ":" + BIND_PORT;
		}
		ConnectionString connectionString = new ConnectionString(connectString);
        
        MongoClientSettings clientSettings = MongoClientSettings.builder()
        		.applyConnectionString(connectionString)
        		.retryWrites(true)
        		.applicationName("MongoDBHandler")
        		.applyToClusterSettings(new Block<ClusterSettings.Builder>() {
					@Override
					public void apply(ClusterSettings.Builder builder) {
						builder.serverSelectionTimeout(1, TimeUnit.SECONDS);
					}
				})
        		.applyToSocketSettings(new Block<SocketSettings.Builder>() {
					@Override
					public void apply(Builder builder) {
						builder.connectTimeout(1, TimeUnit.SECONDS);
					}
				})
        		.build();
        
        return MongoClients.create(clientSettings);		
	}
	
	@SuppressWarnings("unchecked")
	private void checkAuthSetting(MongoDatabase adminDatabase) {
		BasicDBObject getUserInfoCommand = new BasicDBObject("usersInfo", new BasicDBObject("user", "root").append("db", "admin"));
		
		Document getUsersInfoResult = adminDatabase.runCommand(getUserInfoCommand);
		List<Document> users = (List<Document>) getUsersInfoResult.get("users");
		if(users.size() > 0) {
			log.info("root user is exists");
		} else {
			log.info("root user is not exists. creating root user for admin db.");
			List<Object> roles = new ArrayList<>();
			roles.add(new BasicDBObject("role", "root").append("db", "admin"));
						
			BasicDBObject runCommand = new BasicDBObject("createUser", "root")
					.append("pwd", "root")
					.append("roles", roles);
					
			Document resultDocument = adminDatabase.runCommand(runCommand);			
			log.info("created root user for admin db : {}", resultDocument);
		}
	}
	
	void runMongoDB() throws Exception {
		MongoClient mongoClient = connectMongoClient(true);
    	MongoDatabase adminDatabase = null;
    	MongodExecutable mongodExecutable = null;
    	
    	try {
    		adminDatabase = mongoClient.getDatabase("admin");
    		this.checkAuthSetting(adminDatabase);
    	} catch(MongoTimeoutException e) {
    		mongoClient.close();
    		log.debug("mongodb server is not exists. now starting mongodb server");
    		try {
    			mongodExecutable = this.startMongoDB(false);
			} catch (Exception e1) {
				throw e1;
			}
    		
    		mongoClient = connectMongoClient(false);
    		adminDatabase = mongoClient.getDatabase("admin");
    		this.checkAuthSetting(adminDatabase);
    		
    		mongodExecutable.stop();
    		log.info("non secure mongodb stoped. starting secure mongodb.");
    		
    		try {
    			mongodExecutable = this.startMongoDB(true);
			} catch (Exception e1) {
				throw e1;
			}
    	}
    	
    	mongoClient.getDatabase("test_db");
	}
	
//    public static void main(String[] args) throws Exception {
//    	
//    	MongoDBHandler mongoDBHandler = new MongoDBHandler();
//    	mongoDBHandler.runMongoDB();
//    	log.info("MongoDB Started");
//    	mongoDBHandler.destroy();
//    	Thread.sleep(10000);
//    }

    
	@Override
	public void destroy() throws Exception {
		log.info("MongoDB Stop start");
		MongoClient mongoClient = connectMongoClient(true);
		MongoDatabase adminDatabase = mongoClient.getDatabase("admin");
		Document result = adminDatabase.runCommand(new BasicDBObject("shutdown", "1"));
		log.info("Result : {}", result);
		mongoClient.close();
		log.info("MongoDB Stoped");
	}
    
}