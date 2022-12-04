package net.cloudcentrik.autolink.testservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WikiPageViewsList {
    List<WikiPageViews> items;
}
