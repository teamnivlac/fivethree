package com.teamnivlac.fivethree.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeBatch;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResult;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;

public class DefaultController {

	public static long DEFAULT_TTL = 300L;
	public static String SETTINGS_FILE = "/settings/settings.properties";
	public static void main(String[] args) throws IOException, URISyntaxException {
		// TODO Auto-generated method stub
		
		Properties prop = new Properties();
		prop.load(DefaultController.class.getResourceAsStream(SETTINGS_FILE));
		String hostedZoneID = prop.getProperty("hostedZoneID");
		String domain = prop.getProperty("domain");
		long ttl;
		try{
			ttl = Long.valueOf(prop.getProperty("ttl"));
		}
		catch (NumberFormatException nfe)
		{
			System.out.println("Bad TLL: Using default value of 300");
			ttl = DEFAULT_TTL;
		}
		
		// Get IP Address of gateway
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		
		String ip = in.readLine(); // you get the IP as a String
		System.out.println("Discovered IP: " + ip);

		boolean ipIsDirty = !ip.equals(prop.getProperty("lastKnownIp"));
		
		if (ipIsDirty)
		{
			// Update route53
			AmazonRoute53 client = AmazonRoute53ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
			ChangeResourceRecordSetsRequest request = new ChangeResourceRecordSetsRequest().withHostedZoneId(hostedZoneID)
					.withChangeBatch(new ChangeBatch().withComment("Web server for example.com").withChanges(
							new Change().withAction("UPSERT").withResourceRecordSet(new ResourceRecordSet().withName(domain)
									.withType("A").withTTL(ttl).withResourceRecords(new ResourceRecord().withValue(ip)))));
			request.withRequestCredentialsProvider(
					new ClasspathPropertiesFileCredentialsProvider("/credentials/credentials.properties"));
			System.out.println("Sending request");
			ChangeResourceRecordSetsResult response = client.changeResourceRecordSets(request);
			
			response.getChangeInfo();
			System.out.println(response.toString());
			
			// Update settings.properties only if route53 update is successful
			prop.setProperty("lastKnownIp", ip);
			prop.store(new FileOutputStream(new File(DefaultController.class.getResource(SETTINGS_FILE).toURI())), "Update IP");
		}
		else
		{
			System.out.println("IP has not changed.");
		}
	}
}
