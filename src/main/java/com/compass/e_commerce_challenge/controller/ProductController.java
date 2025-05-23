package com.compass.e_commerce_challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.compass.e_commerce_challenge.dto.product.ProductRequest;
import com.compass.e_commerce_challenge.dto.product.ProductResponse;
import com.compass.e_commerce_challenge.dto.shared.ApiResponse;
import com.compass.e_commerce_challenge.dto.shared.PageRequestDto;
import com.compass.e_commerce_challenge.dto.shared.PagedResponse;
import com.compass.e_commerce_challenge.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getActive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        PageRequestDto pr = new PageRequestDto();
        pr.setPage(page); pr.setSize(size); pr.setSortBy(sortBy); pr.setDirection(direction);
        return ResponseEntity.ok(productService.listActiveProducts(pr));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<ProductResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        PageRequestDto pr = new PageRequestDto();
        pr.setPage(page); pr.setSize(size); pr.setSortBy(sortBy); pr.setDirection(direction);
        return ResponseEntity.ok(productService.listAllProducts(pr));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> create(
            @Validated @RequestBody ProductRequest dto) {
        ProductResponse created = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Validated @RequestBody ProductRequest dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        productService.deactivateProduct(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        ApiResponse<?> resp = productService.deleteProduct(id);
        return ResponseEntity.status(resp.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                             .body(resp);
    }
}

