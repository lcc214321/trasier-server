package com.trasier.server.elastic;

import com.trasier.api.server.model.Span;
import org.elasticsearch.common.xcontent.XContentBuilder;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Singleton
public class ElasticConverter {
    private static final Pattern BASE64_PATTERN = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");

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

            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
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
            builder.field("beginProcessingTimestamp", span.getBeginProcessingTimestamp() != null ? new Date(span.getBeginProcessingTimestamp()) : null);
            builder.field("finishProcessingTimestamp", span.getFinishProcessingTimestamp() != null ? new Date(span.getFinishProcessingTimestamp()) : null);
            builder.field("endTimestamp", span.getEndTimestamp() != null ? new Date(span.getEndTimestamp()) : null);

            if (span.getIncomingEndpoint() != null) {
                builder.field("incomingEndpoint.name", span.getIncomingEndpoint().getName());
                builder.field("incomingEndpoint.hostname", span.getIncomingEndpoint().getHostname());
                builder.field("incomingEndpoint.ipAddress", span.getIncomingEndpoint().getIpAddress());
                builder.field("incomingEndpoint.port", span.getIncomingEndpoint().getPort());
            }

            if (span.getOutgoingEndpoint() != null) {
                builder.field("outgoingEndpoint.name", span.getOutgoingEndpoint().getName());
                builder.field("outgoingEndpoint.hostname", span.getOutgoingEndpoint().getHostname());
                builder.field("outgoingEndpoint.ipAddress", span.getOutgoingEndpoint().getIpAddress());
                builder.field("outgoingEndpoint.port", span.getOutgoingEndpoint().getPort());
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
