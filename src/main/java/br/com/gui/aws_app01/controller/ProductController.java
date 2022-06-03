package br.com.gui.aws_app01.controller;

import br.com.gui.aws_app01.model.Product;
import br.com.gui.aws_app01.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  @Autowired
  private final ProductRepository productRepository;

  public ProductController(ProductRepository productRepository) {
    this.productRepository = productRepository;
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
    return new ResponseEntity<>(productCreated, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable("id") long id) {
    final var existsProduct = productRepository.existsById(id);
    if (existsProduct) {
      product.setId(id);
      final var productUpdated = productRepository.save(product);
      return new ResponseEntity<>(productUpdated, HttpStatus.OK);
    } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id) {
    final var optProduct = productRepository.findById(id);
    final var productIsPresent = optProduct.isPresent();
    if (productIsPresent) {
      productRepository.delete(optProduct.get());
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
