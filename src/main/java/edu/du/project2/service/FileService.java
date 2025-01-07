package edu.du.project2.service;

import edu.du.project2.entity.FileDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 파일 업로드와 관련된 공통 로직을 처리하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class FileService {
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";

    /**
     * 파일 업로드를 처리하고 파일 정보를 반환합니다.
     *
     * @param files 업로드할 파일 배열
     * @return 파일 정보 리스트
     * @throws IOException 파일 저장 실패 시 발생
     */
    public List<FileDetail> uploadFiles(MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // 디렉토리가 없으면 생성
        }

        List<FileDetail> fileDetails = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null || originalFilename.isEmpty()) {
                    throw new IllegalArgumentException("파일 이름이 유효하지 않습니다.");
                }

                Path filePath = Paths.get(UPLOAD_DIR, originalFilename);
                file.transferTo(filePath.toFile()); // 파일 저장

                fileDetails.add(new FileDetail("/uploads/" + originalFilename, originalFilename));
            }
        }
        return fileDetails;
    }

}