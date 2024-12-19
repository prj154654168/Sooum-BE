package com.sooum.data.img.service;

import com.sooum.data.img.entity.ProfileImg;
import com.sooum.data.img.repository.ProfileImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileImgService {
    private final ProfileImgRepository profileImgRepository;

    public void saveDefaultProfileImg(String imgName) {
        profileImgRepository.save(new ProfileImg(imgName));
    }

    public void deleteProfileImgs(List<ProfileImg> profileImgs) {
        profileImgRepository.deleteAllInBatch(profileImgs);
    }

    public void updateProfileImgNull(Long memberPk) {
        profileImgRepository.updateCardImgNull(memberPk);
    }

    public ProfileImg findProfileImg(String profileImgName) {
        return profileImgRepository.findProfileImg(profileImgName)
                .orElseThrow(() -> new EntityNotFoundException("프로필 이미지를 찾을 수 없습니다."));
    }
}