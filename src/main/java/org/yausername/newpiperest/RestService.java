package org.yausername.newpiperest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.ListExtractor.InfoItemsPage;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelInfo;
import org.schabi.newpipe.extractor.comments.CommentsInfo;
import org.schabi.newpipe.extractor.comments.CommentsInfoItem;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.kiosk.KioskInfo;
import org.schabi.newpipe.extractor.playlist.PlaylistInfo;
import org.schabi.newpipe.extractor.search.SearchInfo;
import org.schabi.newpipe.extractor.stream.StreamInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.utils.Localization;

import com.google.gson.Gson;

public class RestService {
    
    private Gson gson = new Gson();
    
    public RestService() {
        Downloader d = Downloader.init(null);
        NewPipe.init(d, new Localization("en", "GB"));
    }
    
    public String getServices() {
        return gson.toJson(NewPipe.getServices());
    }
    
    public String getSearchInfo(int serviceId, String searchString, List<String> contentFilters, String sortFilter)
            throws ParsingException, ExtractionException, IOException {
        StreamingService service = NewPipe.getService(serviceId);
        SearchInfo info = SearchInfo.getInfo(service,
                service.getSearchQHFactory().fromQuery(searchString, contentFilters, sortFilter));
        return gson.toJson(info);
    }
    
    public String getSearchPage(int serviceId, String searchString, List<String> contentFilters, String sortFilter,
            String pageUrl) throws ParsingException, ExtractionException, IOException {
        StreamingService service = NewPipe.getService(serviceId);
        InfoItemsPage<InfoItem> page = SearchInfo.getMoreItems(service,
                service.getSearchQHFactory().fromQuery(searchString, contentFilters, sortFilter), pageUrl);
        return gson.toJson(page);
    }
    
    public String getKioskIdsList(int serviceId) throws ExtractionException {
        StreamingService service = NewPipe.getService(serviceId);
        List<String> res = new ArrayList<>();
        service.getKioskList().getAvailableKiosks().forEach(k -> res.add(k));
        return gson.toJson(res);
    }
    
    public String getKioskInfo(int serviceId, String kioskId) throws ExtractionException, IOException {
        StreamingService service = NewPipe.getService(serviceId);
        String url = service.getKioskList().getListLinkHandlerFactoryByType(kioskId).getUrl(kioskId);
        KioskInfo info = KioskInfo.getInfo(service, url);
        return gson.toJson(info);
    }
    
    public String getKioskPage(int serviceId, String kioskId, String pageUrl) throws ExtractionException, IOException {
        StreamingService service = NewPipe.getService(serviceId);
        String url = service.getKioskList().getListLinkHandlerFactoryByType(kioskId).getUrl(kioskId);
        InfoItemsPage<StreamInfoItem> page = KioskInfo.getMoreItems(service, url, pageUrl);
        return gson.toJson(page);
    }
    
    public String getStreamInfo(@Nonnull String url) throws IOException, ExtractionException {
        StreamInfo info = StreamInfo.getInfo(url);
        return gson.toJson(info);
    }
    
    public String getPlaylistInfo(@Nonnull String url) throws IOException, ExtractionException {
        PlaylistInfo info = PlaylistInfo.getInfo(url);
        return gson.toJson(info);
    }
    
    public String getPlaylistPage(@Nonnull String url, @Nonnull String pageUrl) throws IOException, ExtractionException {
        StreamingService service = NewPipe.getServiceByUrl(url);
        InfoItemsPage<StreamInfoItem> page = PlaylistInfo.getMoreItems(service, url, pageUrl);
        return gson.toJson(page);
    }
    
    public String getChannelInfo(@Nonnull String url) throws IOException, ExtractionException {
        ChannelInfo info = ChannelInfo.getInfo(url);
        return gson.toJson(info);
    }
    
    public String getChannelPage(@Nonnull String url, @Nonnull String pageUrl) throws IOException, ExtractionException {
        StreamingService service = NewPipe.getServiceByUrl(url);
        InfoItemsPage<StreamInfoItem> page = ChannelInfo.getMoreItems(service, url, pageUrl);
        return gson.toJson(page);
    }
    
    public String getCommentsInfo(@Nonnull String url) throws IOException, ExtractionException {
        CommentsInfo info = CommentsInfo.getInfo(url);
        return gson.toJson(info);
    }
    
    public String getCommentsPage(@Nonnull String url, @Nonnull String pageUrl) throws IOException, ExtractionException {
        //TODO optimize this. init page is fetched every time
        CommentsInfo info = CommentsInfo.getInfo(url);
        InfoItemsPage<CommentsInfoItem> page = CommentsInfo.getMoreItems(info, pageUrl);
        return gson.toJson(page);
    }
    
    public String getError(Exception e) {
        return gson.toJson(new Error(e.getMessage()));
    }

}
