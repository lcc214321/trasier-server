package com.trasier.server.elastic;

import com.trasier.api.server.model.Endpoint;
import com.trasier.api.server.model.Span;
import org.apache.commons.lang3.time.FastDateFormat;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.SearchHit;

import javax.inject.Singleton;
import java.io.IOException;
import java.text.ParseException;
import java.util.Base64;
import java.util.Calendar;
import java.util.regex.Pattern;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Singleton
public class ElasticConverter {
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final Pattern BASE64_PATTERN = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");

    public static Span convert(SearchHit searchHit) {
        Span.SpanBuilder builder = Span.builder();

        builder.id(searchHit.getSourceAsMap().get("spanId").toString());
        builder.traceId(searchHit.getSourceAsMap().get("traceId").toString());
        builder.conversationId(searchHit.getSourceAsMap().get("conversationId").toString());
        builder.status(searchHit.getSourceAsMap().get("status").toString());
        builder.name(searchHit.getSourceAsMap().get("name").toString());

        if (searchHit.getSourceAsMap().get("incomingEndpoint.name") != null) {
            builder.incomingEndpoint(Endpoint.builder().name(searchHit.getSourceAsMap().get("incomingEndpoint.name").toString()).build());
        }
        if (searchHit.getSourceAsMap().get("outgoingEndpoint.name") != null) {
            builder.outgoingEndpoint(Endpoint.builder().name(searchHit.getSourceAsMap().get("outgoingEndpoint.name").toString()).build());
        }

        if (searchHit.getSourceAsMap().get("parentSpanId") != null) {
            builder.parentId(searchHit.getSourceAsMap().get("parentSpanId").toString());
        }

        if (searchHit.getSourceAsMap().get("startTimestamp") != null) {
            try {
                builder.startTimestamp(DATE_FORMAT.parse(searchHit.getSourceAsMap().get("startTimestamp").toString()).getTime());
            } catch (ParseException e) {
                throw new IllegalStateException(e);
            }
        }

        if (searchHit.getSourceAsMap().get("endTimestamp") != null) {
            try {
                builder.endTimestamp(DATE_FORMAT.parse(searchHit.getSourceAsMap().get("endTimestamp").toString()).getTime());
            } catch (ParseException e) {
                throw new IllegalStateException(e);
            }
        }

        return builder.build();
    }

    public XContentBuilder convert(String accountId, String spaceKey, Span span) throws IllegalStateException {
        try {
            XContentBuilder builder = jsonBuilder()
                    .startObject()
                    .field("accountId", accountId)
                    .field("spaceKey", spaceKey)
                    .field("spanId", span.getId())
                    .field("traceId", span.getTraceId())
                    .field("conversationId", span.getConversationId())
                    .field("status", span.getStatus())
                    .field("name", span.getName());

            Calendar calendar = Calendar.getInstance(ElasticService.TIME_ZONE);
            if (span.getStartTimestamp() != null) {
                calendar.setTimeInMillis(span.getStartTimestamp());
            } else if (span.getBeginProcessingTimestamp() != null) {
                calendar.setTimeInMillis(span.getBeginProcessingTimestamp());
            } else if (span.getFinishProcessingTimestamp() != null) {
                calendar.setTimeInMillis(span.getFinishProcessingTimestamp());
            } else if (span.getEndTimestamp() != null) {
                calendar.setTimeInMillis(span.getEndTimestamp());
            } else {
                calendar.setTimeInMillis(System.currentTimeMillis());
            }
            builder.field("startTimestamp", calendar.getTime());

            if (span.getIncomingEndpoint() != null) {
                builder.field("incomingEndpoint.name", span.getIncomingEndpoint().getName());
            }

            if (span.getOutgoingEndpoint() != null) {
                builder.field("outgoingEndpoint.name", span.getOutgoingEndpoint().getName());
            }

            if (span.getParentId() != null) {
                builder.field("parentId", span.getParentId());
            }

            if (span.getIncomingData() != null) {
                builder.field("incomingData", decodeBase64(span.getIncomingData()));
            }

            if (span.getOutgoingData() != null) {
                builder.field("outgoingData", decodeBase64(span.getOutgoingData()));
            }

            return builder.endObject();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String decodeBase64(String incomingData) {
        if(BASE64_PATTERN.matcher(incomingData).matches()) {
            try {
                return new String(Base64.getDecoder().decode(incomingData));
            } catch(Exception e) {
                //TODO log info?
                //ignore and keep original message
            }
        }
        return incomingData;
    }
}
