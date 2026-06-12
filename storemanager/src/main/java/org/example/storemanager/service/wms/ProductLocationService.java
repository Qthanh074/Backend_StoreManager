package org.example.storemanager.service.wms;

import org.example.storemanager.dto.request.wms.ProductLocationAssignRequest;
import org.example.storemanager.dto.response.wms.ProductLocationResponse;
import java.util.List;

public interface ProductLocationService {
    List<ProductLocationResponse> getProductLocations(Long productId, Long binId);
    ProductLocationResponse assignProductLocation(ProductLocationAssignRequest request);
}
