package com.storemini.controller.user.media;

import com.storemini.model.user.entity.MediaEntity;
import com.storemini.model.user.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaRepository mediaRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // ============================================================
    // UPLOAD 1 FILE
    // ============================================================
    @PostMapping({"/upload", "/upload/{fileKey}"})
    public ResponseEntity<?> uploadFile(
            @PathVariable(required = false) String fileKey,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "userAction", required = false, defaultValue = "system") String userAction
    ) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "File rỗng"));
            }

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            // Nếu create → sinh fileKey mới
            boolean isCreateSignal = fileKey == null || fileKey.isBlank()
                    || fileKey.equalsIgnoreCase("create")
                    || fileKey.equalsIgnoreCase("new");

            if (isCreateSignal) {
                fileKey = UUID.randomUUID().toString();
            }

            String originalName = file.getOriginalFilename();
            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";

            String physicalName = UUID.randomUUID().toString() + ext;

            Path target = uploadPath.resolve(physicalName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            MediaEntity media = new MediaEntity();
            media.setFileKey(fileKey);
            media.setName(originalName);
            media.setDescription(description);
            media.setPath(target.toString());
            media.setRoot(uploadPath.toString());

            // nếu chưa có ảnh nào trong fileKey → ảnh đầu tiên là main
            boolean noImageBefore = mediaRepository.countByFileKey(fileKey) == 0;
            media.setMain(noImageBefore);

            media.setUserAction(userAction);
            media.setActionDate(LocalDateTime.now());

            MediaEntity saved = mediaRepository.save(media);

            return ResponseEntity.ok(Map.of(
                    "message", isCreateSignal ? "Upload mới thành công" : "Upload thêm thành công",
                    "id", saved.getId(),
                    "fileKey", fileKey,
                    "fileName", originalName
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Upload thất bại"));
        }
    }

    // ============================================================
    // UPLOAD LIST FILE
    // ============================================================
    @PostMapping({"/uploadList", "/uploadList/{fileKey}"})
    public ResponseEntity<?> uploadList(
            @PathVariable(required = false) String fileKey,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "userAction", required = false, defaultValue = "system") String userAction
    ) {
        try {
            if (files == null || files.isEmpty())
                return ResponseEntity.badRequest().body(Map.of("message", "Không có file nào"));

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            boolean isCreateSignal = fileKey == null || fileKey.isBlank()
                    || fileKey.equalsIgnoreCase("create")
                    || fileKey.equalsIgnoreCase("new");

            if (isCreateSignal) fileKey = UUID.randomUUID().toString();

            boolean hasMainBefore = mediaRepository.findByFileKeyAndMainTrue(fileKey).isPresent();
            boolean isFirstUpload = !hasMainBefore;

            List<Map<String, Object>> list = new ArrayList<>();

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                String originalName = file.getOriginalFilename();
                String ext = originalName != null && originalName.contains(".")
                        ? originalName.substring(originalName.lastIndexOf("."))
                        : "";

                String physicalName = UUID.randomUUID().toString() + ext;

                Path target = uploadPath.resolve(physicalName);
                Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

                MediaEntity media = new MediaEntity();
                media.setFileKey(fileKey);
                media.setName(originalName);
                media.setDescription(description);
                media.setPath(target.toString());
                media.setRoot(uploadPath.toString());

                media.setMain(isFirstUpload);
                isFirstUpload = false;

                media.setUserAction(userAction);
                media.setActionDate(LocalDateTime.now());

                MediaEntity saved = mediaRepository.save(media);

                list.add(Map.of(
                        "id", saved.getId(),
                        "fileName", originalName,
                        "url", "/api/media/view/" + saved.getId()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Upload thành công",
                    "fileKey", fileKey,
                    "files", list
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Upload thất bại"));
        }
    }

    // ============================================================
    // VIEW BY ID
    // ============================================================
    @GetMapping("/view/{id}")
    public ResponseEntity<?> view(@PathVariable Long id) {
        try {
            Optional<MediaEntity> opt = mediaRepository.findById(id);
            if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy ảnh"));

            MediaEntity media = opt.get();
            Path path = Paths.get(media.getPath());

            if (!Files.exists(path)) return ResponseEntity.status(404).body(Map.of("message", "File không tồn tại"));

            String contentType = Files.probeContentType(path);
            byte[] bytes = Files.readAllBytes(path);

            return ResponseEntity.ok()
                    .contentType(contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM)
                    .body(bytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi đọc file"));
        }
    }

    // ============================================================
    // TRẢ VỀ LIST ẢNH BASE64 CHO FE
    // ============================================================
    @GetMapping("/viewAllFileKeyForProduct/{fileKey}")
    public ResponseEntity<?> viewAllFileKeyForProduct(@PathVariable String fileKey) {
        try {
            List<MediaEntity> mediaList = mediaRepository.findAllByFileKey(fileKey);

            if (mediaList.isEmpty())
                return ResponseEntity.status(404).body(Map.of("message", "Không có ảnh"));

            // Ảnh main sẽ được đưa lên đầu
            mediaList.sort((a, b) -> Boolean.compare(b.getMain(), a.getMain()));

            List<Map<String, Object>> result = new ArrayList<>();

            for (MediaEntity m : mediaList) {
                Path path = Paths.get(m.getPath());
                if (!Files.exists(path)) continue;

                byte[] bytes = Files.readAllBytes(path);
                String base64 = Base64.getEncoder().encodeToString(bytes);
                String contentType = Files.probeContentType(path);

                result.add(Map.of(
                        "id", m.getId(),
                        "name", m.getName(),
                        "main", m.getMain(),
                        "fileKey", m.getFileKey(),
                        "contentType", contentType,
                        "data", "data:" + contentType + ";base64," + base64
                ));
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi đọc file"));
        }
    }

    // ============================================================
    // DELETE MEDIA
    // ============================================================
    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<MediaEntity> opt = mediaRepository.findById(id);
            if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy ảnh"));

            MediaEntity media = opt.get();
            String fileKey = media.getFileKey();
            boolean wasMain = media.getMain();

            // Xóa file vật lý
            try {
                Path p = Paths.get(media.getPath());
                if (Files.exists(p)) Files.delete(p);
            } catch (Exception ignore) {}

            mediaRepository.delete(media);

            // Nếu xoá ảnh chính, chọn ảnh mới làm main
            if (wasMain) {
                List<MediaEntity> remain = mediaRepository.findAllByFileKey(fileKey);
                if (!remain.isEmpty()) {
                    MediaEntity first = remain.get(0);
                    first.setMain(true);
                    mediaRepository.save(first);
                }
            }

            return ResponseEntity.ok(Map.of("message", "Xóa thành công"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi xoá ảnh"));
        }
    }

    // ============================================================
    // SET MAIN
    // ============================================================
    @PutMapping("/set-main/{id}")
    @Transactional
    public ResponseEntity<?> setMain(@PathVariable Long id) {
        try {
            Optional<MediaEntity> opt = mediaRepository.findById(id);
            if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy ảnh"));

            MediaEntity target = opt.get();
            String fileKey = target.getFileKey();

            mediaRepository.updateMainFalseForFileKey(fileKey);

            target.setMain(true);
            mediaRepository.save(target);

            return ResponseEntity.ok(Map.of("message", "Đặt ảnh chính thành công"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi khi cập nhật ảnh chính"));
        }
    }

}
