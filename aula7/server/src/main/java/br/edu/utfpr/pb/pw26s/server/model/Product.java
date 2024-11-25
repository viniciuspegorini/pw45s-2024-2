package br.edu.utfpr.pb.pw26s.server.model;

import lombok.Data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 254)
    private String name;

    @NotNull
    @Size(min = 2, max = 1024)
    @Column(length = 1024, nullable = false)
    private String description;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "image_name")
    private String imageName; // Upload no Sistema de Arquivos (disco)

    @Lob
    @Column(name = "image_file")
    private byte[] imageFile; // Upload no Banco de dados

    @Column(name = "image_file_name")
    private String imageFileName; // Upload no Banco de dados
}
