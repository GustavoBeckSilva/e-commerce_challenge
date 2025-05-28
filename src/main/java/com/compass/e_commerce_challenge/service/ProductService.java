package com.compass.e_commerce_challenge.service;

import com.compass.e_commerce_challenge.dto.product.ProductRequest;
import com.compass.e_commerce_challenge.dto.product.ProductResponse;
import com.compass.e_commerce_challenge.dto.shared.ApiResponse;
import com.compass.e_commerce_challenge.dto.shared.PageRequestDto;
import com.compass.e_commerce_challenge.dto.shared.PagedResponse;

public interface ProductService {
    
	/*
	 * Admin
	 */
	
	ProductResponse createProduct(ProductRequest dto);
    ProductResponse updateProduct(Long productId, ProductRequest dto);
    PagedResponse<ProductResponse> listAllProducts(PageRequestDto pageRequest);
    ApiResponse<?> deleteProduct(Long productId);
    void deactivateProduct(Long productId);
    
    /*
	 * Shared
	 */
    PagedResponse<ProductResponse> listActiveProducts(PageRequestDto pageRequest);
    ProductResponse getProductById(Long productId);
}
