package br.edu.utfpr.pb.pw45s.server.controller;

import br.edu.utfpr.pb.pw45s.server.dto.CategoryDto;
import br.edu.utfpr.pb.pw45s.server.model.Category;
import br.edu.utfpr.pb.pw45s.server.service.CategoryService;
import br.edu.utfpr.pb.pw45s.server.service.CrudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jdk.jfr.ContentType;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("categories")
public class CategoryController extends CrudController<Category, CategoryDto, Long> {

    private final CategoryService categoryService;
    private final ModelMapper modelMapper;


    public CategoryController(CategoryService categoryService, ModelMapper modelMapper) {
        super(Category.class, CategoryDto.class);
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    protected CrudService<Category, Long> getService() {
        return this.categoryService;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return this.modelMapper;
    }


    @Operation(summary = "Get a category by its ID")
    @ApiResponses(
        value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the category",
                   content = {
                            @Content(mediaType = "application/json",
                           schema = @Schema(implementation = Category.class))
                    }
            )
        }
    )
    @Override
    public ResponseEntity<CategoryDto> findOne(Long aLong) {
        return super.findOne(aLong);
    }
}