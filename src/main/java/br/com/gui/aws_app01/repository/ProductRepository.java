package br.com.gui.aws_app01.repository;

import br.com.gui.aws_app01.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

  Optional<Product> findByCode(String code);
}
