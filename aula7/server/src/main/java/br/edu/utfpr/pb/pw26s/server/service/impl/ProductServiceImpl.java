package br.edu.utfpr.pb.pw26s.server.service.impl;

import br.edu.utfpr.pb.pw26s.server.model.Product;
import br.edu.utfpr.pb.pw26s.server.repository.ProductRepository;
import br.edu.utfpr.pb.pw26s.server.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
@Slf4j
public class ProductServiceImpl extends CrudServiceImpl<Product, Long>
    implements ProductService {
    private static final String FILE_PATH = File.separator + "uploads";
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    protected JpaRepository<Product, Long> getRepository() {
        return this.productRepository;
    }

    /* Armazena o arquivo no sistema de arquivos (disco)
            -> O FILE_PATH está indicando para salvar em /uploads,  ou seja na raiz do disco
            no qual está sendo executada a aplicação
         */
    @Override
    public void saveImage(MultipartFile file, Product product) {
        File dir = new File(FILE_PATH + File.separator + "images-product");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String suffix = Objects.requireNonNull(file.getOriginalFilename()).substring( file.getOriginalFilename().lastIndexOf(".") );
        try {
            FileOutputStream fileOut = new FileOutputStream(
                    new File(dir + File.separator + product.getId() + suffix)
            );
            BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut);
            bufferedOut.write(file.getBytes());
            bufferedOut.close();
            fileOut.close();

            product.setImageName(product.getId() + suffix);
            productRepository.save(product);
        } catch (Exception e) {
            log.error("Error in saveImage() - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /* Armazena o arquivo no Banco de Dados
     */
    @Override
    public void saveImageFile(MultipartFile file, Product product) {
        try {
            String suffix = Objects.requireNonNull(file.getOriginalFilename()).substring(
                    file.getOriginalFilename().lastIndexOf(".")
            );
            product.setImageFileName(product.getId() + suffix);
            product.setImageFile(file.getBytes());
            this.save(product);
        } catch (Exception e) {
            log.error("Error in saveImageFile() - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /* Retorna o arquivo armazenado em disco no formato Base64
     */
    @Override
    public String getProductImage(Long id) {
        try {
            Product product = productRepository.findById(id).orElse(null);
            if (product != null) {
                String filename = FILE_PATH + File.separator + "images-product" +
                        File.separator + product.getImageName();
                return encodeFileToBase64(filename);
            }
        } catch (Exception e) {
            log.error("Error in getProductImage() - " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    private String encodeFileToBase64(String filename) throws IOException {
        File file = new File(filename);
        FileInputStream stream = new FileInputStream(file);
        byte[] encoded = Base64.encodeBase64(IOUtils.toByteArray(stream));
        stream.close();
        return new String(encoded, StandardCharsets.US_ASCII);
    }

}
