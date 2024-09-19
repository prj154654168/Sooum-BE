package com.sooum.core.domain.img.controller;

import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.img.service.LocalImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/imgs")
public class ImgController {
    private final ImgService imgService;

    @GetMapping("/{imgName}/user")
    public ResponseEntity<Resource> findUserImg(@PathVariable String imgName) throws MalformedURLException {
        LocalImgService localImgService = (LocalImgService) imgService;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imgName + ".jpg" + "\"")
                .body(localImgService.findImg(ImgType.USER, imgName));
    }

    @GetMapping("/{imgName}/default")
    public ResponseEntity<Resource> findDefaultImg(@PathVariable String imgName) throws MalformedURLException {
        LocalImgService localImgService = (LocalImgService) imgService;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imgName + ".jpg" + "\"")
                .body(localImgService.findImg(ImgType.DEFAULT, imgName));
    }
}
