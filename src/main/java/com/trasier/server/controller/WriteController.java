package com.trasier.server.controller;

import com.trasier.api.server.model.Span;
import com.trasier.api.server.service.WriteService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;

import java.util.Arrays;

@Controller("/api")
public class WriteController {

    private WriteService writeService;

    public WriteController(WriteService writeService) {
        this.writeService = writeService;
    }

    @Post(uri = "/accounts/{$accountId}/spaces/{$spaceKey}/spans")
    public void postSpan(@PathVariable("$accountId") String accountId, @PathVariable("$spaceKey") String spaceKey, @Body Span[] spans) {
        writeService.writeSpans(accountId, spaceKey, Arrays.asList(spans));
    }

}
