package com.storemini.controller.user.media;

import com.storemini.model.user.entity.MediaEntity;
import com.storemini.model.user.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaRepository mediaRepository;

    // Thư mục lưu file (có thể cấu hình trong application.yml)
    @Value("${app.upload.dir}")
    private String uploadDir;


    /**
     * Upload ảnh:
     * - Nếu fileKey = "create" (hoặc rỗng) => sinh fileKey mới, tạo record mới, trả về fileKey + id
     * - Nếu fileKey có giá trị => tạo record mới (không ghi đè) với cùng fileKey (mỗi ảnh có id khác)
     *
     * FE dùng:
     * 1) POST /api/media/upload/create  (form-data: file, description, userAction)
     *    -> server trả về fileKey (chuỗi)
     * 2) POST /api/media/upload/{fileKey} (form-data: file, description, userAction)
     *    -> server tạo thêm record mới với cùng fileKey
     */
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

            // đảm bảo folder tồn tại
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Nếu fileKey là "create" hoặc null => sinh fileKey mới
            boolean isCreateSignal = fileKey == null || fileKey.isBlank() || fileKey.equalsIgnoreCase("create") || fileKey.equalsIgnoreCase("new");
            if (isCreateSignal) {
                fileKey = UUID.randomUUID().toString();
            }

            // Sinh tên file lưu thực tế để tránh trùng (uuid + extension)
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String physicalFileName = UUID.randomUUID().toString() + extension;
            Path target = uploadPath.resolve(physicalFileName);

            // Lưu file lên disk (ghi đè nếu trùng physicalFileName, nhưng UUID nên không trùng)
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // Tạo bản ghi MediaEntity mới (luôn tạo mới record, không cập nhật record cũ)
            MediaEntity media = new MediaEntity();
            media.setFileKey(fileKey);
            media.setName(originalFilename);
            media.setDescription(description);
            media.setPath(target.toAbsolutePath().toString());
            media.setRoot(uploadPath.toAbsolutePath().toString());
            media.setMain(true);
            media.setUserAction(userAction == null ? "system" : userAction);
            media.setActionDate(LocalDateTime.now());

            MediaEntity saved = mediaRepository.save(media);

            // Trả về thông tin: fileKey (dùng cho lần upload tiếp theo), id (của file vừa tạo)
            Map<String, Object> body = new HashMap<>();
            body.put("message", isCreateSignal ? "Upload mới thành công" : "Upload (thêm) thành công");
            body.put("fileKey", fileKey);
            body.put("id", saved.getId());
            body.put("fileName", originalFilename);
            body.put("url", "/api/media/view/" + saved.getId());

            return ResponseEntity.ok(body);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Upload thất bại"));
        }
    }


    @PostMapping({"/uploadList", "/uploadList/{fileKey}"})
    public ResponseEntity<?> uploadListFile(
            @PathVariable(required = false) String fileKey,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "userAction", required = false, defaultValue = "system") String userAction
    ) {
        try {
            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Không có file nào được gửi lên"));
            }

            // đảm bảo thư mục upload tồn tại
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // nếu fileKey = "create" hoặc rỗng -> sinh mới
            boolean isCreateSignal = fileKey == null || fileKey.isBlank()
                    || fileKey.equalsIgnoreCase("create")
                    || fileKey.equalsIgnoreCase("new");

            if (isCreateSignal) {
                fileKey = UUID.randomUUID().toString();
            }

            List<Map<String, Object>> uploadedFiles = new ArrayList<>();

            boolean isFirst = true; // cờ để đánh dấu file đầu tiên

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                String physicalFileName = UUID.randomUUID().toString() + extension;
                Path target = uploadPath.resolve(physicalFileName);

                // lưu file vào disk
                Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

                // tạo record cho từng file
                MediaEntity media = new MediaEntity();
                media.setFileKey(fileKey);
                media.setName(originalFilename);
                media.setDescription(description);
                media.setPath(target.toAbsolutePath().toString());
                media.setRoot(uploadPath.toAbsolutePath().toString());
                media.setMain(isFirst); // ảnh đầu tiên main = true
                media.setUserAction(userAction);
                media.setActionDate(LocalDateTime.now());

                MediaEntity saved = mediaRepository.save(media);

                Map<String, Object> info = new HashMap<>();
                info.put("id", saved.getId());
                info.put("fileName", originalFilename);
                info.put("url", "/api/media/view/" + saved.getId());
                uploadedFiles.add(info);

                isFirst = false; // từ ảnh thứ 2 trở đi main = false
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", isCreateSignal ? "Upload mới thành công" : "Upload thêm thành công");
            response.put("fileKey", fileKey);
            response.put("count", uploadedFiles.size());
            response.put("files", uploadedFiles);

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Upload thất bại"));
        }
    }


    /**
     * Lấy file theo ID (trả raw bytes, FE có thể dùng <img src="/api/media/view/{id}">)
     */
    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewById(@PathVariable Long id) {
        try {
            Optional<MediaEntity> opt = mediaRepository.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Không tìm thấy media"));
            }
            MediaEntity media = opt.get();
            Path path = Paths.get(media.getPath());
            if (!Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "File không tồn tại"));
            }

            String contentType = Files.probeContentType(path);
            byte[] bytes = Files.readAllBytes(path);

            MediaType mediaType = contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + (media.getName() == null ? path.getFileName() : media.getName()) + "\"")
                    .body(bytes);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi đọc file"));
        }
    }

    @GetMapping("/viewFileKey/{filekey}")
    public ResponseEntity<?> viewFileKey(@PathVariable String filekey) {
        try {
            Optional<MediaEntity> opt = mediaRepository.findByFileKey(filekey);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Không tìm thấy media"));
            }
            MediaEntity media = opt.get();
            Path path = Paths.get(media.getPath());
            if (!Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "File không tồn tại"));
            }

            String contentType = Files.probeContentType(path);
            byte[] bytes = Files.readAllBytes(path);

            MediaType mediaType = contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + (media.getName() == null ? path.getFileName() : media.getName()) + "\"")
                    .body(bytes);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi đọc file"));
        }
    }

    @GetMapping("/viewFileKeyForProduct/{filekey}")
    public ResponseEntity<?> viewFileKeyForProduct(@PathVariable String filekey) {
        try {
            Optional<MediaEntity> opt = mediaRepository.findByFileKeyAndMainTrue(filekey);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Không tìm thấy media"));
            }
            MediaEntity media = opt.get();
            Path path = Paths.get(media.getPath());
            if (!Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "File không tồn tại"));
            }

            String contentType = Files.probeContentType(path);
            byte[] bytes = Files.readAllBytes(path);

            MediaType mediaType = contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + (media.getName() == null ? path.getFileName() : media.getName()) + "\"")
                    .body(bytes);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi đọc file"));
        }
    }

    @GetMapping("/viewAllFileKeyForProduct/{filekey}")
    public ResponseEntity<?> viewAllFileKeyForProduct(@PathVariable String filekey) {
        try {
            List<MediaEntity> mediaList = mediaRepository.findAllByFileKey(filekey);

            if (mediaList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Không tìm thấy hình ảnh cho sản phẩm này"));
            }

            // 🔹 Trả về danh sách chứa tên file, đường dẫn và dữ liệu base64 (để React dễ dùng)
            List<Map<String, Object>> result = new ArrayList<>();

            for (MediaEntity media : mediaList) {
                Path path = Paths.get(media.getPath());
                if (!Files.exists(path)) continue;

                String contentType = Files.probeContentType(path);
                byte[] bytes = Files.readAllBytes(path);
                String base64 = Base64.getEncoder().encodeToString(bytes);

                result.add(Map.of(
                        "name", media.getName(),
                        "main", media.getMain(),
                        "fileKey", media.getFileKey(),
                        "contentType", contentType,
                        "data", "data:" + contentType + ";base64," + base64
                ));
            }

            return ResponseEntity.ok(result);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi khi đọc file"));
        }
    }

}
