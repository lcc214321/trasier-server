package com.trasier.server;

import com.trasier.api.server.model.ContentType;
import com.trasier.api.server.model.ConversationInfo;
import com.trasier.api.server.model.Endpoint;
import com.trasier.api.server.model.Span;
import com.trasier.api.server.model.SpanInfo;
import com.trasier.api.server.model.TraceInfo;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.runtime.Micronaut;

@Introspected(classes = {ContentType.class, ConversationInfo.class, Endpoint.class, Span.class, SpanInfo.class, TraceInfo.class})
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}