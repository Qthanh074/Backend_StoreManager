package org.example.storemanager.dto.request.omnichannel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import org.example.storemanager.enums.omnichannel.SalesChannelType;

@Data
public class SalesChannelRequest {

    @NotBlank(message = "Tên kênh không được để trống")
    private String channelName;

    @NotNull(message = "Loại kênh không được để trống")
    private SalesChannelType channelType;

    private String apiEndpoint;
    private String apiKey;
    private String webhookSecret;

    private String status = "ACTIVE";
}
