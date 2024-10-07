package com.sooum.core.domain.tag.service;

import com.sooum.core.domain.member.service.MemberService;
import com.sooum.core.domain.tag.entity.FavoriteTag;
import com.sooum.core.global.exceptionmessage.ExceptionMessage;
import jakarta.persistence.EntityExistsException;
import com.sooum.core.domain.tag.entity.Tag;
import com.sooum.core.domain.tag.repository.FavoriteTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteTagService {
    private final FavoriteTagRepository favoriteTagRepository;
    private final TagService tagService;
    private final MemberService memberService;

    @Transactional
    public void saveFavoriteTag(String tagContent, Long memberPk) {
        if (tagService.isExistFavoriteTag(tagContent, memberPk)) {
            throw new EntityExistsException(ExceptionMessage.ALREADY_TAG_FAVORITE.getMessage());
        }

        FavoriteTag favoriteTag = FavoriteTag.builder()
                .tag(tagService.findTag(tagContent))
                .member(memberService.findByPk(memberPk))
                .build();
        tagService.saveFavoriteTag(favoriteTag);
    }


    List<Tag> findFavoriteTags(Long memberPk) {
        return favoriteTagRepository.findFavoriteTag(memberPk);
    }

    @Transactional
    public void deleteFavoriteTag(String tagContent, Long memberPk) {
        FavoriteTag findFavoriteTag = tagService.findFavoriteTag(tagContent, memberPk);
        tagService.deleteFavoriteTag(findFavoriteTag);
    }
}
