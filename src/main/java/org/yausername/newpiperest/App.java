package org.yausername.newpiperest;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import spark.utils.StringUtils;

public class App {

    public static void main(String[] args) {

        RestService service = new RestService();
        
        port(getHerokuAssignedPort());

        before((request, response) -> response.type("application/json"));

        exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            response.body(service.getError(exception));
        });

        path("/api/v1", () -> {
            get("/services", (req, res) -> service.getServices());

            get("/streams", (req, res) -> {
                String url = req.queryParams("url");
                return service.getStreamInfo(url);
            });

            get("/search", (req, res) -> {
                int serviceId = Integer.valueOf(req.queryParams("serviceId"));
                String searchString = req.queryParams("searchString");
                String sortFilter = req.queryParams("sortFilter");
                String contentFiltersData = req.queryParams("contentFilters");
                List<String> contentFilters = Collections.emptyList(); 
                if(!StringUtils.isEmpty(contentFiltersData)) {
                    contentFilters = Arrays.asList(contentFiltersData.split(","));
                }
                return service.getSearchInfo(serviceId, searchString, contentFilters, sortFilter);
            });
            
            get("/search/page", (req, res) -> {
                int serviceId = Integer.valueOf(req.queryParams("serviceId"));
                String searchString = req.queryParams("searchString");
                String sortFilter = req.queryParams("sortFilter");
                String contentFiltersData = req.queryParams("contentFilters");
                List<String> contentFilters = Collections.emptyList(); 
                if(!StringUtils.isEmpty(contentFiltersData)) {
                    contentFilters = Arrays.asList(contentFiltersData.split(","));
                }
                String pageUrl = req.queryParams("pageUrl");
                return service.getSearchPage(serviceId, searchString, contentFilters, sortFilter, pageUrl);
            });
            
            get("/playlists", (req, res) -> {
                String url = req.queryParams("url");
                return service.getPlaylistInfo(url);
            });
            
            get("/playlists/page", (req, res) -> {
                String url = req.queryParams("url");
                String pageUrl = req.queryParams("pageUrl");
                return service.getPlaylistPage(url, pageUrl);
            });
            
            get("/channels", (req, res) -> {
                String url = req.queryParams("url");
                return service.getChannelInfo(url);
            });
            
            get("/channels/page", (req, res) -> {
                String url = req.queryParams("url");
                String pageUrl = req.queryParams("pageUrl");
                return service.getChannelPage(url, pageUrl);
            });
            
            get("/kiosks", (req, res) -> {
                int serviceId = Integer.valueOf(req.queryParams("serviceId"));
                return service.getKioskIdsList(serviceId);
            });
            
            get("/kiosks/:kioskId", (req, res) -> {
                String kioskId = req.params(":kioskId");
                int serviceId = Integer.valueOf(req.queryParams("serviceId"));
                return service.getKioskInfo(serviceId, kioskId);
            });
            
            get("/kiosks/:kioskId/page", (req, res) -> {
                String kioskId = req.params(":kioskId");
                int serviceId = Integer.valueOf(req.queryParams("serviceId"));
                String pageUrl = req.queryParams("pageUrl");
                return service.getKioskPage(serviceId, kioskId, pageUrl);
            });
            
            get("/comments", (req, res) -> {
                String url = req.queryParams("url");
                return service.getCommentsInfo(url);
            });
            
            get("/comments/page", (req, res) -> {
                String url = req.queryParams("url");
                String pageUrl = req.queryParams("pageUrl");
                return service.getCommentsPage(url, pageUrl);
            });
        });

    }
    
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
