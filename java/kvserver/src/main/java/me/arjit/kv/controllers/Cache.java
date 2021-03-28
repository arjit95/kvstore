package me.arjit.kv.controllers;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.models.CacheEntry;
import me.arjit.kv.models.Context;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cache")
public class Cache {
    @PostMapping("put")
    public ResponseEntity<HttpStatus> put(@RequestBody() CacheEntry<byte[]> body) {
        Context.getContext().getCacheStore().add(body.getKey(), body.getValue());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("sync")
    public ResponseEntity<HttpStatus> sync(@RequestBody() Map<String, CacheEntry<byte[]>> body) {
        log.debug("Received sync size {}", body.size());
        Context.getContext().getCacheStore().getCacheStrategy().deserialize(body);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("download")
    public ResponseEntity<String> download() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String response = Context.getContext().getCacheStore().getCacheStrategy().serialize();
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping("get")
    public ResponseEntity<byte[]> get(@RequestParam("key") String key) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        byte[] value = Context.getContext().getCacheStore().get(key);
        return new ResponseEntity<>(value, headers, HttpStatus.OK);
    }
}
