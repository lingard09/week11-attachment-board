package edu.handong.web11.controller;

import edu.handong.web11.domain.Attachment;
import edu.handong.web11.repository.AttachmentRepository;
import edu.handong.web11.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {
    private final AttachmentRepository attachments;
    private final FileStorageService storage;
    public AttachmentController(AttachmentRepository attachments, FileStorageService storage) {
        this.attachments = attachments; this.storage = storage;
    }
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Attachment file = attachments.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "첨부파일이 없습니다.")); // ID로 첨부파일 정보를 조회하고, 없으면 404 오류를 반환한다.
        Resource resource = storage.load(file); // 저장된 파일명으로 디스크의 파일을 찾아 Resource로 가져온다.
        String disposition = ContentDisposition.attachment() // 브라우저가 파일을 다운로드하도록 Content-Disposition 값을 만든다.
                .filename(file.getOriginalFilename(), StandardCharsets.UTF_8) // 다운로드 파일명을 원본 이름으로 지정하고, 한글 등을 표현할 수 있도록 UTF-8을 사용한다.
                .build().toString();
        return ResponseEntity.ok() // 요청 성공을 나타내는 HTTP 200 응답을 만든다.
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // 응답 본문을 일반 바이너리 데이터로 지정한다.
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition) // 다운로드 방식과 원본 파일명을 Content-Disposition 헤더에 넣는다.
                .body(resource); // 응답 본문에 파일 리소스를 담아 전송한다.
    }
    // STUDENT TODO: GET /attachments/{id}/view (image preview).
}
