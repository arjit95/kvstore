package me.arjit.kv.controllers;

import me.arjit.kv.models.CacheEntry;
import me.arjit.kv.models.Context;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/cache")
public class Cache {
    @PostMapping("put")
    public ResponseEntity<HttpStatus> put(@RequestBody() CacheEntry<byte[]> body) {
        Context.getContext().getCacheStore().add(body.getKey(), body.getValue());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("get")
    public ResponseEntity<byte[]> get(@RequestParam("key") String key) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        byte[] value = Context.getContext().getCacheStore().get(key);
        return new ResponseEntity<>(value, headers, HttpStatus.OK);
    }
}
