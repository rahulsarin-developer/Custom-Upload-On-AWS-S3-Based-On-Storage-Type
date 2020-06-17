package com.springboot.poc.documentupload.utility;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Component
public class S3Utility {
	
	@Value("${cloud.aws.credentials.accessKey}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secretKey}")
	private String secretkey;

	@Value("${app.awsServices.private.bucketName}")
	private String privateBucketName;

	@Value("${cloud.aws.region.static}")
	private String regions;
	
	/**
	 * Get Basic AWS Credentials
	 * 
	 * @param accesskey
	 * @param secretkey
	 * @return
	 */
	private AWSCredentials getBasicAWSCredentials(String accesskey, String secretkey) {
		return new BasicAWSCredentials(accesskey, secretkey);
	}

	/**
	 * Get AmazonS3
	 * 
	 * @return
	 */
	private AmazonS3 getAmazonS3() {
		return AmazonS3ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(getBasicAWSCredentials(accessKey, secretkey)))
			.withRegion(regions).build();
	}

	@Async
	public void uploadOnS3All(Map<String, MultipartFile> map) {
		for(Map.Entry<String, MultipartFile> entry : map.entrySet()) {
			try {
				getAmazonS3().putObject(privateBucketName, entry.getKey(), entry.getValue().getInputStream(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}
}
