package com.ecom.service;

import com.ecom.entity.Brand;
import com.ecom.entity.Product;
import com.ecom.exception.UploadFileErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Brand addBrand(Brand brand);
    List<Brand> getAll();
    Brand findBrandById(Long idBrand);
    Product addProduct(MultipartFile image, Product product) throws UploadFileErrorException;
    Product findProductById(Long idProduct);
    List<Product> findProductByBrand(Long idBand);
    Product changeImageProduct(MultipartFile file, Long idProduct) throws UploadFileErrorException;
}
