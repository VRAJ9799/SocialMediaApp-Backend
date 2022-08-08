package com.vraj.socialmediaapp.models.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paging<T> {

    private int pageNo = 1;
    private int limit = 10;
    private String sortBy = "createdOn";
    private Sort.Direction sortOrder = Sort.Direction.DESC;
    private boolean hasNext = false;
    private boolean hasPrevious = false;
    private Map<String, Object> filters = new HashMap<>();
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Collection<T> data;

    public Paging(boolean hasNext, boolean hasPrevious, Collection<T> data) {
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
        this.data = data;
    }

}
