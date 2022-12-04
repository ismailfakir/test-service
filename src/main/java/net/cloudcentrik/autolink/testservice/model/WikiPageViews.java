package net.cloudcentrik.autolink.testservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WikiPageViews {
    String project;
    String article;
    String granularity;
    String timestamp;
    String access;
    String agent;
    int views;
}
