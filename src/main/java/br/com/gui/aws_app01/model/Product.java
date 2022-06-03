package br.com.gui.aws_app01.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(length = 32, nullable = false)
  private String name;

  @Column(length = 24, nullable = false)
  private String model;

  @Column(length = 8, nullable = false)
  private String code;

  private float price;
}
