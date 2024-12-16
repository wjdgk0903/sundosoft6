package edu.du.project2.service;


import edu.du.project2.entity.Notice;
import edu.du.project2.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public List<Notice> getAllNotices() {
        return noticeRepository.findAll(Sort.by(Sort.Order.desc("createdAt")));
    }

    // 파일 업로드 경로 설정
    private final String UPLOAD_DIR ="C:/teamproject/sundosoft6/uploads";

    public void createNotice(String title, String content, MultipartFile file) throws IOException {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setHits(0);  // 초기 조회수 0
        notice.setCreatedAt(LocalDateTime.now());  // 생성일시 현재 시간으로 설정
        // 파일 업로드 처리
        if (file != null && !file.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);   // 디렉토리가 없으면 생성
            }
            // 파일 이름을 고유하게 설정
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            // 파일을 지정된 경로에 저장
            file.transferTo(filePath.toFile());

            // 공지사항에 파일 경로 저장
            notice.setFilePath(fileName);
        } else {
            notice.setFilePath(""); // 파일이 없으면 빈 문자열로 설정
        }

        // 공지사항 저장
        noticeRepository.save(notice);

    }

    // 특정 게시글을 조회하고, 조회수 증가 여부를 설정하여 게시글 반환
    @Transactional
    public Notice selectNoticeDetail(Long id) throws Exception{
        Notice notice= noticeRepository.selectNoticeDetail(id); // 게시글 조회
        noticeRepository.updateHitCount(id);  // 조회수 증가
        return notice;
    }
    @Transactional
    public void updateNotice(Notice notice) {
        Notice updateNotice = noticeRepository.findById(notice.getId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        updateNotice.setTitle(notice.getTitle()); // 제목 수정
        updateNotice.setContent(notice.getContent()); // 내용 수정
        noticeRepository.save(updateNotice);  // 수정된 공지사항 저장
    }

    @Transactional
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }

}
