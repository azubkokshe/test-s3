package com.az.tests3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.FileCopyUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

//https://www.baeldung.com/aws-s3-java

@SpringBootApplication
@Configuration

public class TestS3Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TestS3Application.class, args);
    }

    @Bean
    public AmazonS3 amazaonS3(){
        //https://console.aws.amazon.com/iam/home?#security_credential
        AWSCredentials credentials = new BasicAWSCredentials(
                "",
                ""
        );

        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_WEST_2)
                .build();

        return s3client;
    }

    @Autowired
    AmazonS3 s3Client;

    Logger log = LoggerFactory.getLogger(TestS3Application.class);

    @Override
    public void run(String... args) throws Exception {
        List<Bucket> buckets = s3Client.listBuckets();
        for(Bucket bucket : buckets) {
            log.info("Start workwith bucket {}", bucket.getName());

            PutObjectResult putObjectRequest = s3Client.putObject(
                    bucket.getName(),
                    "hello.txt",
                    new File("test.txt"));

            log.info("Saved file {}", putObjectRequest.getContentMd5());

            ObjectListing objectListing = s3Client.listObjects(bucket.getName());
            for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
                log.info("File in bucket {}", os.getKey());

                S3Object s3object = s3Client.getObject(bucket.getName(), os.getKey());
                S3ObjectInputStream inputStream = s3object.getObjectContent();

                FileCopyUtils.copy(inputStream, new FileOutputStream(new File("files/" + os.getKey())));
            }
        }
    }
}
