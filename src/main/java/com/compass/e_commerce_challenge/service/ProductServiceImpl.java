package com.compass.e_commerce_challenge.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compass.e_commerce_challenge.dto.product.ProductRequest;
import com.compass.e_commerce_challenge.dto.product.ProductResponse;
import com.compass.e_commerce_challenge.dto.shared.ApiResponse;
import com.compass.e_commerce_challenge.dto.shared.PageRequestDto;
import com.compass.e_commerce_challenge.dto.shared.PagedResponse;
import com.compass.e_commerce_challenge.entity.Product;
import com.compass.e_commerce_challenge.repository.ProductRepository;
import com.compass.e_commerce_challenge.util.exceptions.OperationNotAllowedException;
import com.compass.e_commerce_challenge.util.exceptions.ResourceNotFoundException;


@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest dto) {
        Product product = modelMapper.map(dto, Product.class);
        product.setActive(true);
        Product saved = productRepository.save(product);
        return modelMapper.map(saved, ProductResponse.class);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest dto) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));
        modelMapper.map(dto, product); 

        Product updated = productRepository.save(product);
        return modelMapper.map(updated, ProductResponse.class);

    }

    @Override
    @Transactional
    public void deactivateProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public ApiResponse<?> deleteProduct(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "ID", productId);
        }
        
        if (productRepository.existsByOrderItems_Product_Id(productId)) { 
            throw new OperationNotAllowedException("Cannot delete product with ID " + productId + " as it is linked to one or more orders. Consider deactivating it instead.");
        }
        
        productRepository.deleteById(productId);
        return ApiResponse.success("Product deleted successfully.");
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> listActiveProducts(PageRequestDto pageRequest) {
        PageRequest pr = PageRequest.of(
            pageRequest.getPage(), pageRequest.getSize(),
            Sort.by(Sort.Direction.fromString(pageRequest.getDirection()), pageRequest.getSortBy())
        );
        Page<Product> page = productRepository.findByActiveTrueAndStockQuantityGreaterThan(0, pr);
        return toPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> listAllProducts(PageRequestDto pageRequest) {
        PageRequest pr = PageRequest.of(
            pageRequest.getPage(), pageRequest.getSize(),
            Sort.by(Sort.Direction.fromString(pageRequest.getDirection()), pageRequest.getSortBy())
        );
        Page<Product> page = productRepository.findAll(pr);
        return toPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));
        return modelMapper.map(product, ProductResponse.class);
    }

    private PagedResponse<ProductResponse> toPagedResponse(Page<Product> page) {
        return PagedResponse.<ProductResponse>builder()
                .content(page.map(p -> modelMapper.map(p, ProductResponse.class)).getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}