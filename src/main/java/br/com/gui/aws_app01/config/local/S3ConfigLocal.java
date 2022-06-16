package br.com.gui.aws_app01.config.local;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.model.S3Event;
import com.amazonaws.services.s3.model.TopicConfiguration;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class S3ConfigLocal {
  private static final String BUCKET_NAME = "pcs-invoice";

  private AmazonS3 amazonS3;

  public S3ConfigLocal() {
    amazonS3 = getAmazonS3();

    createBucket();

    AmazonSNS snsClient = getAmazonSNS();

    String s3InvoiceEvetsTopicArn = createTopic(snsClient);

    AmazonSQS sqsClient = getAmazonSQS();

    createQueue(snsClient, s3InvoiceEvetsTopicArn, sqsClient);

    configureBucket(s3InvoiceEvetsTopicArn);
  }

  public AmazonS3 getAmazonS3() {
    AWSCredentials credentials = new BasicAWSCredentials("test", "test");

    this.amazonS3 = AmazonS3ClientBuilder.standard()
        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
            "http://localhost:4566", Regions.US_EAST_1.getName()))

        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .enablePathStyleAccess()
        .build();
    return this.amazonS3;
  }

  private void createBucket() {
    this.amazonS3.createBucket(BUCKET_NAME);
  }



  private AmazonSNS getAmazonSNS() {
    return AmazonSNSClient.builder()
        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration
            (
                "http://localhost:4566",
                Regions.US_EAST_1.getName()
            )
        )
        .withCredentials(new DefaultAWSCredentialsProviderChain())
        .build();
  }

  private AmazonSQS getAmazonSQS() {
    return AmazonSQSClient.builder()
        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration
            (
                "http://localhost:4566",
                Regions.US_EAST_1.getName()
            )
        )
        .withCredentials(new DefaultAWSCredentialsProviderChain())
        .build();
  }

  private String createTopic(AmazonSNS snsClient) {
    final var createTopicRequest = new CreateTopicRequest("s3-invoice-events");
    return snsClient.createTopic(createTopicRequest).getTopicArn();
  }

  private void createQueue(AmazonSNS snsClient, String s3InvoiceEvetsTopicArn, AmazonSQS sqsClient) {
    final var productEventsQueueUrl =
        sqsClient.createQueue(new CreateQueueRequest("s3-invoice-events")).getQueueUrl();

    Topics.subscribeQueue(snsClient, sqsClient, s3InvoiceEvetsTopicArn, productEventsQueueUrl);
  }

  private void configureBucket(String s3InvoiceEvetsTopicArn) {
    TopicConfiguration topicConfiguration = new TopicConfiguration();
    topicConfiguration.setTopicARN(s3InvoiceEvetsTopicArn);
    topicConfiguration.addEvent(S3Event.ObjectCreatedByPut);

    amazonS3.setBucketNotificationConfiguration(BUCKET_NAME, new BucketNotificationConfiguration()
        .addConfiguration("putObject", topicConfiguration));
  }

  @Bean
  public AmazonS3 amazonS3Client() {
    return this.amazonS3;
  }
}
