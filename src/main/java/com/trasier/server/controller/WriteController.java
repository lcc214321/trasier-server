package com.trasier.server.controller;

import com.trasier.api.server.model.Span;
import com.trasier.api.server.service.WriteService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;

import java.util.List;

@Controller("/api")
public class WriteController {

    private WriteService writeService;

    public WriteController(WriteService writeService) {
        this.writeService = writeService;
    }

    @Post(uri = "/accounts/{$accountId}/spaces/{$spaceKey}/spans", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public void postSpan(@PathVariable("$accountId") String accountId, @PathVariable("$spaceKey") String spaceKey, @Body List<Span> spans) {
        writeService.writeSpans(accountId, spaceKey, spans);
    }

}
