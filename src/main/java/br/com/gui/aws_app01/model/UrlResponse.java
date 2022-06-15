package br.com.gui.aws_app01.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlResponse {
  private String url;
  private long expirationTime;
}
