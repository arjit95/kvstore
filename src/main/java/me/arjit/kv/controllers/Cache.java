package me.arjit.kv.controllers;

import me.arjit.kv.models.CachePutBody;
import me.arjit.kv.models.Context;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/api/cache")
public class Cache {
    @PutMapping("put")
    public ResponseEntity put(@RequestBody() CachePutBody body) {
        Context.getContext().getCacheStore().add(body.getKey(), body.getValue());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("get")
    public byte[] get(@RequestParam("key") String key) {
        return Context.getContext().getCacheStore().get(key);
    }
}
