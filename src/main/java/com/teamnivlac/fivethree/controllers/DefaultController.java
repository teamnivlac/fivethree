package com.teamnivlac.fivethree.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		// Get IP Address of gateway
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

		String ip = in.readLine(); // you get the IP as a String
		System.out.println(ip);

		Properties prop = new Properties();
		prop.load(DefaultController.class.getResourceAsStream("/settings/settings.properties"));
		
		
		String hostedZoneID = prop.getProperty("hostedZoneID");
		String domain = prop.getProperty("domain");

		AmazonRoute53 client = AmazonRoute53ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
		ChangeResourceRecordSetsRequest request = new ChangeResourceRecordSetsRequest().withHostedZoneId(hostedZoneID)
				.withChangeBatch(new ChangeBatch().withComment("Web server for example.com").withChanges(
						new Change().withAction("UPSERT").withResourceRecordSet(new ResourceRecordSet().withName(domain)
								.withType("A").withTTL(299L).withResourceRecords(new ResourceRecord().withValue(ip)))));
		request.withRequestCredentialsProvider(
				new ClasspathPropertiesFileCredentialsProvider("/credentials/credentials.properties"));
		System.out.println("Sending request");
		ChangeResourceRecordSetsResult response = client.changeResourceRecordSets(request);
		System.out.println(response.toString());
	}
}
