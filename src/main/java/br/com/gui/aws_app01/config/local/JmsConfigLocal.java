package br.com.gui.aws_app01.config.local;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.stereotype.Service;

import javax.jms.Session;

@Service
@EnableJms
@Profile("local")
public class JmsConfigLocal {

  @Value("${aws.region}")
  private String awsRegion;

  private SQSConnectionFactory sqsConnectionFactory;

  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
    sqsConnectionFactory = new SQSConnectionFactory(new ProviderConfiguration(),
        AmazonSQSClient.builder().withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                "http://localhost:4566",
                Regions.SA_EAST_1.getName()
            ))
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .build());
    final var factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(sqsConnectionFactory);
    factory.setDestinationResolver(new DynamicDestinationResolver());
    factory.setConcurrency("2"); // Quantidade de thread por fila
    factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);

    return factory;
  }
}
