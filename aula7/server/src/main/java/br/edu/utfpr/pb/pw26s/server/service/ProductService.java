package br.edu.utfpr.pb.pw26s.server.service;

import br.edu.utfpr.pb.pw26s.server.model.Product;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService extends CrudService<Product, Long> {

    void saveImage(MultipartFile file, Product product);

    void saveImageFile(MultipartFile file, Product product);

    String getProductImage(Long id);
}
