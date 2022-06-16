package br.com.gui.aws_app01.consumer;

import br.com.gui.aws_app01.model.Invoice;
import br.com.gui.aws_app01.model.SnsMessage;
import br.com.gui.aws_app01.repository.InvoiceRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Service
public class InvoiceConsumer {

  private final ObjectMapper objectMapper;
  private final InvoiceRepository invoiceRepository;
  private final AmazonS3 amazonS3;

  @Autowired
  public InvoiceConsumer(ObjectMapper objectMapper, InvoiceRepository invoiceRepository, AmazonS3 amazonS3) {
    this.objectMapper = objectMapper;
    this.invoiceRepository = invoiceRepository;
    this.amazonS3 = amazonS3;
  }

  @JmsListener(destination = "${aws.sqs.queue.invoice.events.name}")
  public void receiveS3Event(TextMessage textMessage) throws JMSException, IOException {
    final var snsMessage = objectMapper.readValue(textMessage.getText(), SnsMessage.class);
    final var s3EventNotification = objectMapper.readValue(snsMessage.getMessage(), S3EventNotification.class);
    processInvoiceNotification(s3EventNotification);
  }

  private void processInvoiceNotification(S3EventNotification s3EventNotification) {
    s3EventNotification.getRecords().forEach(o -> {
      final var s3 = o.getS3();
      final var bucketName = s3.getBucket().getName();
      final var key = s3.getObject().getKey();

      String invoiceFile = null;
      try {
        invoiceFile = downloadObject(bucketName, key);
      } catch (IOException e) {
        log.error("Erro ao fazer download do object do bucket");
        e.printStackTrace();
      }

      Invoice invoice = null;
      try {
        invoice = objectMapper.readValue(invoiceFile, Invoice.class);
      } catch (JsonProcessingException e) {
        log.error("Erro ao parse do object do bucket para modelo Invoice");
        e.printStackTrace();
      }
      assert invoice != null;
      log.info("Invoice received: {}", invoice.getInvoiceNumber());
      invoiceRepository.save(invoice);

      amazonS3.deleteObject(bucketName, key);
    });
  }

  private String downloadObject(String bucketName, String key) throws IOException {
    final var s3Object = amazonS3.getObject(bucketName, key);

    final var stringBuilder = new StringBuilder();
    final var bufferedReader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));

    String content;
    while ((content = bufferedReader.readLine()) != null) {
      stringBuilder.append(content);
    }
    return stringBuilder.toString();
  }

}
