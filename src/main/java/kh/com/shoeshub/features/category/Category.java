package kh.com.shoeshub.features.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Short id;
    private String name;
    private String description;
    private boolean deleted;
}
