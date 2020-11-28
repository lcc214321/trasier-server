package com.trasier.server.elastic;

import com.trasier.api.server.model.Span;
import com.trasier.api.server.service.WriteService;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import javax.inject.Singleton;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;

@Singleton
public class ElasticService implements WriteService, Closeable {
    private static final String TYPE = "span";

    private final ElasticConverter converter;
    private final RestHighLevelClient client;

    public ElasticService(RestHighLevelClient client, ElasticConverter converter) {
        this.client = client;
        this.converter = converter;
    }

    @Override
    public void writeSpans(String accountId, String spaceKey, List<Span> spans) {
        spans.forEach(span -> write(accountId, spaceKey, span));
    }

    public void write(String accountId, String spaceKey, Span span) {
        IndexRequest indexRequest = createIndexRequest(accountId, spaceKey, span);
        client.indexAsync(indexRequest, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse response) {
                //Currently ignored
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    IndexRequest createIndexRequest(String accountId, String spaceKey, Span span) {
        IndexRequest indexRequest = new IndexRequest(createIndexName(accountId, spaceKey), TYPE);
        indexRequest.source(converter.convert(accountId, spaceKey, span));
        indexRequest.routing(accountId + "_" + spaceKey);
        return indexRequest;
    }

    String createIndexName(String accountId, String spaceKey) {
        return "default_" + accountId + "_" + spaceKey;
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

}