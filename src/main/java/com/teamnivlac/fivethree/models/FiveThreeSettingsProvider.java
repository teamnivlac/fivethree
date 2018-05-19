package com.teamnivlac.fivethree.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import com.amazonaws.regions.Regions;
import com.teamnivlac.fivethree.controllers.DefaultController;

public class FiveThreeSettingsProvider {

	public static final long DEFAULT_TTL = 300L;
	public static final int DEFAULT_INTERVAL = 300;
	public static final String DEFAULT_COMMENT = "FiveThree Dynamic Update";

	private String hostedZoneId;
	private String domain;
	private Long ttl;
	private String lastKnownIp;
	private String comment;
	private Integer ipCheckIntervalSeconds;
	private Regions region;

	private String classpathResource;

	public static FiveThreeSettingsProvider buildWithPropertiesFileFromClasspath(String classpathResource)
			throws IOException {
		Properties properties = new Properties(getDefaultProperties());
		properties.load(FiveThreeSettingsProvider.class.getResourceAsStream(classpathResource));
		return new FiveThreeSettingsProvider(properties).withClassPathResource(classpathResource);
	}

	private FiveThreeSettingsProvider withClassPathResource(String classpathResource) {
		this.setClasspathResource(classpathResource);
		return this;
	}

	public static FiveThreeSettingsProvider buildEmpty() {
		return new FiveThreeSettingsProvider(getDefaultProperties());
	}

	private FiveThreeSettingsProvider(Properties properties) {
		this.setHostedZoneId(properties.getProperty("hostedZoneId"));
		this.setDomain(properties.getProperty("domain"));
		this.setTtl(properties.getProperty("ttl"));
		this.setLastKnownIp(properties.getProperty("lastKnownIp"));
		this.setComment(properties.getProperty("comment"));
		this.setIpCheckIntervalSeconds(properties.getProperty("ipCheckIntervalSeconds"));
		this.setRegion(properties.getProperty("region"));
	}

	private static Properties getDefaultProperties() {
		Properties defaultProperties = new Properties();
		defaultProperties.setProperty("hostedZoneId", "");
		defaultProperties.setProperty("domain", "");
		defaultProperties.setProperty("ttl", String.valueOf(DEFAULT_TTL));
		defaultProperties.setProperty("lastKnownIp", "");
		defaultProperties.setProperty("comment", DEFAULT_COMMENT);
		defaultProperties.setProperty("ipCheckIntervalSeconds", String.valueOf(DEFAULT_INTERVAL));
		defaultProperties.setProperty("region", "");

		return defaultProperties;
	}

	private Properties getCurrentProperties() {
		Properties currentProperties = new Properties(getDefaultProperties());
		currentProperties.setProperty("hostedZoneId", this.getHostedZoneId());
		currentProperties.setProperty("domain", this.getDomain());
		currentProperties.setProperty("ttl", String.valueOf(this.getTtl()));
		currentProperties.setProperty("lastKnownIp", this.getLastKnownIp());
		currentProperties.setProperty("comment", this.getComment());
		currentProperties.setProperty("ipCheckIntervalSeconds", String.valueOf(this.getIpCheckIntervalSeconds()));
		currentProperties.setProperty("region", this.getRegion().toString());

		return currentProperties;
	}

	public void save() throws FileNotFoundException, IOException, URISyntaxException {
		this.getCurrentProperties()
				.store(new FileOutputStream(
						new File(DefaultController.class.getResource(this.getClasspathResource()).toURI())),
						"Save Settings");
	}

	/**
	 * @return the classpathResource
	 */
	private String getClasspathResource() {
		return classpathResource;
	}

	/**
	 * @param classpathResource
	 *            the classpathResource to set
	 */
	private void setClasspathResource(String classpathResource) {
		this.classpathResource = classpathResource;
	}

	/**
	 * @return the hostedZoneId
	 */
	public String getHostedZoneId() {
		return hostedZoneId;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @return the ttl
	 */
	public Long getTtl() {
		return ttl;
	}

	/**
	 * @return the lastKnownIp
	 */
	public String getLastKnownIp() {
		return lastKnownIp;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return the ipCheckIntervalSeconds
	 */
	public Integer getIpCheckIntervalSeconds() {
		return ipCheckIntervalSeconds;
	}

	/**
	 * @param hostedZoneId
	 *            the hostedZoneId to set
	 */
	public void setHostedZoneId(String hostedZoneId) {
		this.hostedZoneId = hostedZoneId;
	}

	/**
	 * @param domain
	 *            the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @param ttl
	 *            the ttl to set
	 */
	public void setTtl(Long ttl) {
		this.ttl = ttl;
	}

	public void setTtl(String ttl) throws NumberFormatException {
		this.setTtl(Long.valueOf(ttl));
	}

	/**
	 * @param lastKnownIp
	 *            the lastKnownIp to set
	 */
	public void setLastKnownIp(String lastKnownIp) {
		this.lastKnownIp = lastKnownIp;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @param ipCheckIntervalSeconds
	 *            the ipCheckIntervalSeconds to set
	 */
	public void setIpCheckIntervalSeconds(Integer ipCheckIntervalSeconds) {
		this.ipCheckIntervalSeconds = ipCheckIntervalSeconds;
	}

	public void setIpCheckIntervalSeconds(String ipCheckIntervalSeconds) throws NumberFormatException {
		this.setIpCheckIntervalSeconds(Integer.valueOf(ipCheckIntervalSeconds));
	}

	/**
	 * @return the region
	 */
	public Regions getRegion() {
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 */
	public void setRegion(Regions region) {
		this.region = region;
	}

	public void setRegion(String region) {
		this.setRegion(Regions.fromName(region));
	}
}
