package com.teamnivlac.fivethree.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.teamnivlac.fivethree.models.DNSUpdate;
import com.teamnivlac.fivethree.models.FiveThreeSettingsProvider;

public class DefaultController {

	public static long DEFAULT_TTL = 300L;
	public static String SETTINGS_FILE = "/settings/settings.properties";
	public static void main(String[] args) throws IOException, URISyntaxException {
		// TODO Auto-generated method stub
		

// BEGIN IP ADDRESS HANDLING - MOVE TO IT'S OWN CLASS
		// Get IP Address of gateway
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		
		String ip = in.readLine(); // you get the IP as a String
		System.out.println("Discovered IP: " + ip);
// END IP ADDRESS HANDLING
		
		FiveThreeSettingsProvider settings = FiveThreeSettingsProvider.buildWithPropertiesFileFromClasspath(SETTINGS_FILE);

		boolean ipIsDirty = !ip.equals(settings.getLastKnownIp());
		
		if (ipIsDirty)
		{
			// Update route53
			AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider("/credentials/credentials.properties");
			
			// Update settings.properties only if route53 update is successful
			if (DNSUpdate.updateDNS(settings, credentialsProvider, ip)) {
					settings.setLastKnownIp(ip);
					settings.save();
			}
		}
		else
		{
			System.out.println("IP has not changed.");
		}
	}
}
