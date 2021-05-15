package ai.learngram.video.controllers;


import ai.learngram.video.payload.ListResponse;
import ai.learngram.video.payload.RestResponse;
import ai.learngram.video.service.VideoStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    VideoStorageService videoStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam("name") String name,
                                    @RequestParam(value = "thumbnail", required = false, defaultValue = "") String thumbnail) {
        try {
            videoStorageService.upload(file.getInputStream(), Math.toIntExact(file.getSize()), name, thumbnail);
            return new ResponseEntity<>(new RestResponse("Successfully uploaded", 200), HttpStatus.OK);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(new RestResponse("Something went wrong", 500), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stream/{name}")
    public Mono<ResponseEntity<byte[]>> stream(@PathVariable String name) {
        try {
            return videoStorageService.download(name);
        }
        catch (Exception ex) {
            return Mono.just(
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .header("Content-Type", "video/mp4")
                            .header("Content-Length", "0")
                            .body(new byte[0])
            );
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer page) {
        try {
            return new ResponseEntity<>(new ListResponse("Successfully fetched data", 200, videoStorageService.list(pageSize, page)), HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(new ListResponse("Could not fetch data", 404, new ArrayList<>()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("name") String name) {
        try {
            return new ResponseEntity(new ListResponse("Found.", 200, Collections.singletonList(videoStorageService.search(name))), HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity(new ListResponse("Not found.", 404, new ArrayList<>()), HttpStatus.OK);
        }
    }
}
