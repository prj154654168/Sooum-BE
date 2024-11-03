package com.sooum.api.img.controller;

import com.sooum.api.img.dto.ImgUrlInfo;
import com.sooum.api.img.service.ImgService;
import com.sooum.global.responseform.ResponseCollectionModel;
import com.sooum.global.responseform.ResponseEntityModel;
import com.sooum.global.responseform.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/imgs")
public class ImgController {
    private final ImgService imgService;

    @GetMapping("/default")
    public ResponseEntity<CollectionModel<ImgUrlInfo>> createDefaultImgsRetrieveUrl(@RequestParam(required = false, defaultValue = "") List<String> previousImgsName) {
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
                        .add(linkTo(methodOn(ImgController.class).getClass())
                                .slash("/default?previousImgsName=" + imgService.findIssuedDefaultImgsName(imgsUrlInfo))
                                .withRel("next"))
        );
    }

    @GetMapping("/cards/upload")
    public ResponseEntity<ResponseEntityModel<ImgUrlInfo>> createCardImgUploadUrl(@RequestParam String extension) {
        return ResponseEntity.ok(
                ResponseEntityModel.<ImgUrlInfo>builder()
                        .status(
                                ResponseStatus.builder()
                                        .httpCode(HttpStatus.OK.value())
                                        .httpStatus(HttpStatus.OK)
                                        .responseMessage("Card img upload url was issued successfully")
                                        .build()
                        )
                        .content(imgService.createCardImgUploadUrl(extension))
                        .build()
        );
    }

    @GetMapping("/profiles/upload")
    public ResponseEntity<ResponseEntityModel<ImgUrlInfo>> createProfileImgUploadUrl(@RequestParam String extension) {
        return ResponseEntity.ok(
                ResponseEntityModel.<ImgUrlInfo>builder()
                        .status(
                                ResponseStatus.builder()
                                        .httpCode(HttpStatus.OK.value())
                                        .httpStatus(HttpStatus.OK)
                                        .responseMessage("Profile img upload url was issued successfully")
                                        .build()
                        )
                        .content(imgService.createProfileImgUploadUrl(extension))
                        .build()
        );
    }
}