package br.com.gui.aws_app01.config.local;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.Topic;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Profile("local")
public class SqsCreateSubscribe {
  public SqsCreateSubscribe(AmazonSNS snsClient, @Qualifier("productEventsTopic") Topic productEventsTopic) {

    final var sqsClient = AmazonSQSClient.builder()
        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration
            (
                "http://localhost:4566",
                Regions.SA_EAST_1.getName()
            )
        )
        .withCredentials(new DefaultAWSCredentialsProviderChain())
        .build();

    final var productEventsQueueUrl =
        sqsClient.createQueue(new CreateQueueRequest("product-events")).getQueueUrl();


    Topics.subscribeQueue(snsClient, sqsClient, productEventsTopic.getTopicArn(), productEventsQueueUrl);
  }
}