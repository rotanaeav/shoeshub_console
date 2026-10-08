package kh.com.shoeshub.features.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private UUID id;
    private UUID productId;
    private UUID userId;
    private Short rating; // 1 to 5
    private String comment;
    private boolean deleted;
    private Timestamp createdAt;
    private String userName;
    private String productName;
}
