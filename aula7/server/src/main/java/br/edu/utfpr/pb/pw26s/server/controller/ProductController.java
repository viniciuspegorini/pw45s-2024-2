package br.edu.utfpr.pb.pw26s.server.controller;

import br.edu.utfpr.pb.pw26s.server.dto.ProductDto;
import br.edu.utfpr.pb.pw26s.server.model.Product;
import br.edu.utfpr.pb.pw26s.server.service.CrudService;
import br.edu.utfpr.pb.pw26s.server.service.ProductService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("products")
public class ProductController extends CrudController<Product, ProductDto, Long> {

    private final ProductService productService;
    private final ModelMapper modelMapper;


    public ProductController(ProductService productService, ModelMapper modelMapper) {
        super(Product.class, ProductDto.class);
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    @Override
    protected CrudService<Product, Long> getService() {
        return this.productService;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return this.modelMapper;
    }

    /* Upload de arquivo salvo no sistema de arquivos
       formData = { product:{}, image:"arquivo"}
    */
    @PostMapping("upload-fs")
    public Product save(@RequestPart("product") @Valid Product product,
                        @RequestPart("images") MultipartFile file) {
        getService().save(product);
        productService.saveImage(file, product);
        return product;
    }

    // Upload de arquivo salvo no Banco de dados
    @PostMapping("upload-db")
    public Product saveImageFile(@RequestPart("product") @Valid Product product,
                                 @RequestPart("image") MultipartFile file) {
        getService().save(product);
        productService.saveImageFile(file, product);
        return product;
    }

}
