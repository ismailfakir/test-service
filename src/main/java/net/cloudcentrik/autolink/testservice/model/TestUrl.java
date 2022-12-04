package net.cloudcentrik.autolink.testservice.model;

import jakarta.ws.rs.core.UriBuilder;

public class TestUrl {
    public static final String wikipedia = "https://wikimedia.org/api/rest_v1/metrics/pageviews/per-article/en.wikipedia/all-access/all-agents/bangladesh/daily/2000101/20220909";

    public static String createWikiPageViewsUrl(String page, String startDate, String endDate) {

        return "https://wikimedia.org/api/rest_v1/metrics/pageviews/per-article/en.wikipedia/all-access/all-agents/"
                + page +"/daily/"+startDate+"/"+endDate;

    }
}
