package br.com.gui.aws_app01.controller;

import br.com.gui.aws_app01.enums.EventType;
import br.com.gui.aws_app01.model.Product;
import br.com.gui.aws_app01.repository.ProductRepository;
import br.com.gui.aws_app01.service.ProductPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  @Autowired
  private final ProductRepository productRepository;

  @Autowired
  private final ProductPublisher productPublisher;

  public ProductController(ProductRepository productRepository, ProductPublisher productPublisher) {
    this.productRepository = productRepository;
    this.productPublisher = productPublisher;
  }

  @GetMapping
  public Iterable<Product> findAll() {
    return productRepository.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> findById(@PathVariable long id) {
    final var optProduct = productRepository.findById(id);
    final var productIsPresent = optProduct.isPresent();
    if (productIsPresent) return new ResponseEntity<>(optProduct.get(), HttpStatus.OK);
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @PostMapping()
  public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
    final var productCreated = productRepository.save(product);
    productPublisher.publishProductEvent(productCreated, EventType.PRODUCT_CREATED, "Gui");
    return new ResponseEntity<>(productCreated, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable("id") long id) {
    final var existsProduct = productRepository.existsById(id);
    if (existsProduct) {
      product.setId(id);
      final var productUpdated = productRepository.save(product);
      productPublisher.publishProductEvent(productUpdated, EventType.PRODUCT_UPDATE, "Joao");
      return new ResponseEntity<>(productUpdated, HttpStatus.OK);
    } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id) {
    final var optProduct = productRepository.findById(id);
    final var productIsPresent = optProduct.isPresent();
    if (productIsPresent) {
      productRepository.delete(optProduct.get());
      productPublisher.publishProductEvent(optProduct.get(), EventType.PRODUCT_DELETED, "Maria");
      return new ResponseEntity<>(optProduct.get(), HttpStatus.OK);
    }

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @GetMapping("/bycode")
  public ResponseEntity<Product> findByCode(@RequestParam String code) {
    final var optProduct = productRepository.findByCode(code);
    final var productIsPresent = optProduct.isPresent();
    if (productIsPresent) {
      return new ResponseEntity<>(optProduct.get(), HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}
