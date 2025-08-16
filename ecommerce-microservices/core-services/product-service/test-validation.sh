curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "invalid-sku-lowercase",
    "name": "Test Product",
    "slug": "test-product",
    "status": "INVALID_STATUS"
  }'
