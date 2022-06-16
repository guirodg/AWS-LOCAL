package br.com.gui.aws_app01.config.local;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Profile("local")
public class SnsCreate {
  private final String productEventTopic;
  private final AmazonSNS snsClient;

  public SnsCreate() {
    this.snsClient = AmazonSNSClient.builder()
        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration
            (
                "http://localhost:4566",
                Regions.US_EAST_1.getName()
            )
        )
        .withCredentials(new DefaultAWSCredentialsProviderChain())
        .build();

    final var createTopicRequest = new CreateTopicRequest("product-events");
    this.productEventTopic = this.snsClient.createTopic(createTopicRequest).getTopicArn();

    log.info("SNS Topic ARN: {}", this.productEventTopic);

  }

  @Bean
  public AmazonSNS snsClient() {
    return this.snsClient;
  }

  @Bean(name = "productEventsTopic")
  public Topic snsProductEventsTopic() {
    return new Topic().withTopicArn(productEventTopic);
  }
}
