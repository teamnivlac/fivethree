package com.teamnivlac.fivethree.models;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeBatch;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResult;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;

public class DNSUpdate {

	private static final String ACTION = "UPSERT";
	private static final String RECORD_TYPE = "A";

	public static boolean updateDNS(Regions region, String hostedZoneId, String comment, String domain, Long TTL,
			String ip, AWSCredentialsProvider credentials) {
		AmazonRoute53 client = AmazonRoute53ClientBuilder.standard().withRegion(region).build();
		ChangeResourceRecordSetsRequest request = new ChangeResourceRecordSetsRequest().withHostedZoneId(hostedZoneId)
				.withChangeBatch(new ChangeBatch().withComment(comment)
						.withChanges(new Change().withAction(ACTION)
								.withResourceRecordSet(new ResourceRecordSet().withName(domain).withType(RECORD_TYPE)
										.withTTL(TTL).withResourceRecords(new ResourceRecord().withValue(ip)))));
		request.withRequestCredentialsProvider(credentials);
		System.out.println("Sending request");
		ChangeResourceRecordSetsResult response = client.changeResourceRecordSets(request);
		
		response.getChangeInfo();
		System.out.println(response.toString());

		// Figure out how to wait for success. Until then we'll just retun true.
		return true;
	}
	
	public static boolean updateDNS(FiveThreeSettingsProvider settings, AWSCredentialsProvider credentials, String ip) {
		return updateDNS(settings.getRegion(), settings.getHostedZoneId(), settings.getComment(), settings.getDomain(), settings.getTtl(), ip, credentials);
	}
}
