package gc.cafe.api.controller.product

import gc.cafe.api.ApiResponse
import gc.cafe.api.controller.product.request.ProductCreateRequest
import gc.cafe.api.controller.product.request.ProductUpdateRequest
import gc.cafe.api.service.product.ProductService
import gc.cafe.api.service.product.response.ProductResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productService: ProductService
) {
    @PostMapping
    fun createProduct(@RequestBody request: @Valid ProductCreateRequest): ApiResponse<ProductResponse> {
        return ApiResponse.created(productService.createProduct(request.toServiceRequest()))
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ApiResponse<Long> {
        return ApiResponse.ok(productService.deleteProduct(id))
    }

    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody request: @Valid ProductUpdateRequest
    ): ApiResponse<ProductResponse> {
        return ApiResponse.ok(productService.updateProduct(id, request.toServiceRequest()))
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ApiResponse<ProductResponse> {
        return ApiResponse.ok(productService.getProduct(id))
    }
}
