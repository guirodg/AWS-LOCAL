package br.com.gui.aws_app01.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"invoiceNumber"})})
@Getter
@Setter
public class Invoice {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(length = 32, nullable = false)
  private String invoiceNumber;

  @Column(length = 32, nullable = false)
  private String customerName;

  private float totalValue;

  private long productId;

  private int quantity;

}
