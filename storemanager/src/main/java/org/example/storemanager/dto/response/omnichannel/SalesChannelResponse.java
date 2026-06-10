package org.example.storemanager.dto.response.omnichannel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.example.storemanager.enums.omnichannel.SalesChannelType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesChannelResponse {

    private Long id;
    private String channelName;
    private SalesChannelType channelType;

    private String apiEndpoint;
    private String status;

    private LocalDateTime lastSyncAt;
    private Integer totalProductsMapped;
}
