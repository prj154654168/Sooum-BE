package com.sooum.core.domain.img.controller;

import com.sooum.core.domain.card.entity.imgtype.ImgType;
import com.sooum.core.domain.img.dto.ImgUrlInfo;
import com.sooum.core.domain.img.service.ImgService;
import com.sooum.core.domain.img.service.LocalImgService;
import com.sooum.core.global.responseform.ResponseCollectionModel;
import com.sooum.core.global.responseform.ResponseEntityModel;
import com.sooum.core.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/imgs")
public class ImgController {
    private final ImgService imgService;

    // todo 임시 api
    @GetMapping("/{imgName}/user")
    public ResponseEntity<Resource> findUserImg(@PathVariable(value = "imgName") String imgName) throws MalformedURLException {
        LocalImgService localImgService = (LocalImgService) imgService;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imgName + "\"")
                .body(localImgService.findImg(ImgType.USER, imgName));
    }

    // todo 임시 api
    @GetMapping("/{imgName}/default")
    public ResponseEntity<Resource> findDefaultImg(@PathVariable(value = "imgName") String imgName) throws MalformedURLException {
        LocalImgService localImgService = (LocalImgService) imgService;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imgName + "\"")
                .body(localImgService.findImg(ImgType.DEFAULT, imgName));
    }

    @GetMapping("/default")
    public ResponseEntity<CollectionModel<ImgUrlInfo>> createDefaultImgsRetrieveUrl(@RequestParam(required = false) Optional<List<String>> previousImgsName) {
        List<ImgUrlInfo> imgsUrlInfo = imgService.createDefaultImgRetrieveUrls(previousImgsName);
        return ResponseEntity.ok(
                ResponseCollectionModel.<ImgUrlInfo>builder()
                        .status(
                                ResponseStatus.builder()
                                        .httpCode(HttpStatus.OK.value())
                                        .httpStatus(HttpStatus.OK)
                                        .responseMessage("The link for retrieving the image has been issued")
                                        .build()
                        )
                        .content(imgsUrlInfo)
                        .build()
                        .add(linkTo(methodOn(ImgController.class)
                                .createDefaultImgsRetrieveUrl(Optional.of(imgService.findIssuedDefaultImgsName(imgsUrlInfo))))
                                .withRel("next"))
        );
    }

    @GetMapping("/upload/user")
    public ResponseEntity<ResponseEntityModel<ImgUrlInfo>> createMemberImgUploadUrl(@RequestParam String extension) {
        return ResponseEntity.ok(
                ResponseEntityModel.<ImgUrlInfo>builder()
                        .status(
                                ResponseStatus.builder()
                                        .httpCode(HttpStatus.OK.value())
                                        .httpStatus(HttpStatus.OK)
                                        .responseMessage("Member img upload url was issued successfully")
                                        .build()
                        )
                        .content(imgService.createUserUploadUrl(extension))
                        .build()
        );
    }

    // todo s3 도입 전 임시 api
    @PostMapping("/{imgName}/user")
    public ResponseEntity<ResponseStatus> saveUserImg(@PathVariable String imgName, @RequestPart MultipartFile file) {
        LocalImgService localImgService = (LocalImgService) imgService;
        localImgService.saveUserImg(file, imgName);

        return ResponseEntity.created(URI.create(""))
                .body(ResponseStatus.builder()
                        .httpCode(HttpStatus.CREATED.value())
                        .httpStatus(HttpStatus.CREATED)
                        .responseMessage("Img save successfully")
                        .build()
                );
    }
}