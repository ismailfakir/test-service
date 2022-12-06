package net.cloudcentrik.autolink.testservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "WikiPageViews")
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WikiPageViews {
    @Id
    String id;
    String project;
    String article;
    String granularity;
    String timestamp;
    String access;
    String agent;
    int views;
}
