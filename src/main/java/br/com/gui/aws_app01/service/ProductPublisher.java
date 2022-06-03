package br.com.gui.aws_app01.service;

import br.com.gui.aws_app01.enums.EventType;
import br.com.gui.aws_app01.model.Envelop;
import br.com.gui.aws_app01.model.Product;
import br.com.gui.aws_app01.model.ProductEvent;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductPublisher {
  private final AmazonSNS snsClient;
  private final Topic productEventsTopic;
  private final ObjectMapper objectMapper;

  public ProductPublisher(
      AmazonSNS snsClient,
      @Qualifier("productEventsTopic") Topic productEventsTopic,
      ObjectMapper objectMapper) {
    this.snsClient = snsClient;
    this.productEventsTopic = productEventsTopic;
    this.objectMapper = objectMapper;
  }

  public void publishProductEvent(Product product, EventType eventType, String username) {
    final var productEvent = new ProductEvent();
    productEvent.setProductId(product.getId());
    productEvent.setCode(product.getCode());
    productEvent.setUsername(username);

    final var envelop = new Envelop();
    envelop.setEventType(eventType);

    try {
      envelop.setData(objectMapper.writeValueAsString(productEvent));

      snsClient.publish(productEventsTopic.getTopicArn(), objectMapper.writeValueAsString(eventType));

    } catch (JsonProcessingException e) {
      log.error("Falha ao criar productEvent");
    }
  }
}
