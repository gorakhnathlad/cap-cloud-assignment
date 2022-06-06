package com.cap.aws;

import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketLoggingConfiguration;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.GetBucketEncryptionResult;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.ServerSideEncryptionConfiguration;
import com.amazonaws.services.s3.model.ServerSideEncryptionRule;

public class App {
	public static void main(String[] args) {
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
		listAllS3Buckets(s3);
		listAllS3BucketsPoliciesandACL(s3);
		listAllS3BucketsEncryptionsandLogginginformation(s3);
	}

	public static void listAllS3Buckets(AmazonS3 s3) {
		try {
			List<Bucket> bucketList = s3.listBuckets();
			System.out.println("S3 buckets size  " + bucketList.size());
			for (Bucket b : bucketList) {
				System.out.println("S3 BUCKET Name " + b.getName());
			}
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		}
	}

	public static void listAllS3BucketsPoliciesandACL(AmazonS3 s3) {
		try {
			List<Bucket> bucketList = s3.listBuckets();
			System.out.println("S3 buckets size  " + bucketList.size());
			for (Bucket b : bucketList) {
				String name = b.getName();
				AccessControlList acl = s3.getBucketAcl(name);
				List<Grant> grants = acl.getGrantsAsList();
				for (Grant grant : grants) {
					System.out.format("  %s: %s\n", grant.getGrantee().getIdentifier(),
							grant.getPermission().toString());
				}
				System.out.println();
				BucketPolicy bucketPolicy = s3.getBucketPolicy(name);
				System.out.println(name + " Bucket Policies " + bucketPolicy.getPolicyText());
			}
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		}
	}

	public static void listAllS3BucketsEncryptionsandLogginginformation(AmazonS3 s3) {
		
		try {
			List<Bucket> bucketList = s3.listBuckets();
			System.out.println("S3 buckets size  " + bucketList.size());
			//Encryptions Information
			for (Bucket b : bucketList) {
				GetBucketEncryptionResult getBucketEncryptionResult=s3.getBucketEncryption(b.getName());
				ServerSideEncryptionConfiguration serverSideEncryptionConfiguration=getBucketEncryptionResult.getServerSideEncryptionConfiguration();
				 System.out.println("ServerSideEncryptionConfiguration :"+serverSideEncryptionConfiguration.toString());
				List<ServerSideEncryptionRule> serverSideEncryptionRuleList=serverSideEncryptionConfiguration.getRules();
				for(ServerSideEncryptionRule serverSideEncryptionRule:serverSideEncryptionRuleList) {
					System.out.println("ApplyServerSideEncryptionByDefault : "+serverSideEncryptionRule.getApplyServerSideEncryptionByDefault());
					System.out.println("BucketKeyEnabled ::"+serverSideEncryptionRule.getBucketKeyEnabled());
				}
				
			}
			//Logging Information
			for (Bucket b : bucketList) {
				
				BucketLoggingConfiguration bucketLoggingConfiguration=s3.getBucketLoggingConfiguration(b.getName());
				
				System.out.println("DestinationBucketName : "+bucketLoggingConfiguration.getDestinationBucketName());
				System.out.println("LogFilePrefix : "+bucketLoggingConfiguration.getLogFilePrefix());
				
			}
			
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		}
	}
}
