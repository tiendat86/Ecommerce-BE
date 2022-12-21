package com.ecom.controller;

import com.ecom.dto.ResponseDTO;
import com.ecom.entity.*;
import com.ecom.exception.CannotFoundItemException;
import com.ecom.exception.UploadFileErrorException;
import com.ecom.exception.UserNotFoundException;
import com.ecom.service.BuyProductService;
import com.ecom.service.ProductService;
import com.twilio.http.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController extends BaseController {
    private final ProductService productService;
    private final BuyProductService buyProductService;

    @GetMapping("brand/create")
    public ResponseDTO<Brand> createBrand(@RequestParam String name) {
        Brand brand = new Brand();
        brand.setName(name);
        return ResponseDTO.successResponse(productService.addBrand(brand));
    }

    @GetMapping(value = "brand/get_all", produces = "application/json")
    public ResponseDTO<List<Brand>> getAllBrand() {
        return ResponseDTO.successResponse(productService.getAll());
    }

    @PostMapping("product/create")
    public ResponseDTO<Product> createProduct(@RequestPart MultipartFile image, @RequestPart Product product) {
        try {
            return ResponseDTO.successResponse(productService.addProduct(image, product));
        } catch (UploadFileErrorException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("product/find/brand/{idBrand}")
    public ResponseDTO<List<Product>> findProductByBrand(@PathVariable Long idBrand) {
        return ResponseDTO.successResponse(productService.findProductByBrand(idBrand));
    }

    @PostMapping("product/change_image/{id}")
    public ResponseDTO<Product> changeImageProduct(@RequestPart MultipartFile file, @PathVariable("id") Long idProduct) {
        try {
            return ResponseDTO.successResponse(productService.changeImageProduct(file, idProduct));
        } catch (UploadFileErrorException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("user/add/product")
    public ResponseDTO<Cart> addProductInCart(@RequestParam Long productId, @RequestParam int quantity) {
        try {
            Cart cart = buyProductService.addProductInCart(productId, quantity, getUser());
            return ResponseDTO.successResponse(cart);
        } catch (UserNotFoundException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (CannotFoundItemException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("user/get_cart")
    public ResponseDTO<List<ProductInCart>> getProductInCart() {
        try {
            return ResponseDTO.successResponse(buyProductService.getProductInCartByUser(getUser()));
        } catch (UserNotFoundException e) {
            return ResponseDTO.failedResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
