package com.vraj.socialmediaapp.models.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailModel {
    private String subject;

    private String body;
    private String templateId;

    private Set<String> tos = new HashSet<>();

    private Set<String> bccs = new HashSet<>();
    private Map<String, Object> values = new HashMap<>();


    public void addValue(String key, Object value) {
        this.values.put(key, value);
    }

    public void addTo(String to) {
        this.tos.add(to);
    }

    public void addBcc(String bcc) {
        this.bccs.add(bcc);
    }

    public boolean isTemplateMail() {
        return !templateId.isBlank();
    }
}
