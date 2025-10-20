package com.storemini.controller.admin.inforWeb;

import com.storemini.model.user.entity.StoreInfoEntity;
import com.storemini.model.user.repository.StoreInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/inforWeb")
@RequiredArgsConstructor
public class AdminStoreInfoController {

    private final StoreInfoRepository storeInfoRepository;

    @GetMapping("/getAll")
    public ResponseEntity<List<StoreInfoEntity>> getAllStoreInfo() {
        return ResponseEntity.ok(storeInfoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInfoById(@PathVariable Long id) {
        Optional<StoreInfoEntity> info = storeInfoRepository.findById(id);
        if (info.isPresent()) {
            return ResponseEntity.ok(info.get());
        } else {
            return ResponseEntity.badRequest().body("Info with ID " + id + " not found!");
        }
    }

    @PostMapping("/process")
    public ResponseEntity<StoreInfoEntity> saveOrUpdate(@RequestBody StoreInfoEntity storeInfo) {
        if (storeInfo.getId() != null) {
            Optional<StoreInfoEntity> existing = storeInfoRepository.findById(storeInfo.getId());
            if (existing.isPresent()) {
                StoreInfoEntity updateEntity = existing.get();
                updateEntity.setCode(storeInfo.getCode());
                updateEntity.setParentCode(storeInfo.getParentCode());
                updateEntity.setName(storeInfo.getName());
                updateEntity.setFileKey(storeInfo.getFileKey());
                updateEntity.setSort(storeInfo.getSort());
                updateEntity.setStatus(storeInfo.getStatus());
                updateEntity.setLink(storeInfo.getLink());
                updateEntity.setUserAction(storeInfo.getUserAction());
                updateEntity.setActionDate(LocalDateTime.now());
                return ResponseEntity.ok(storeInfoRepository.save(updateEntity));
            }
        }
        storeInfo.setActionDate(LocalDateTime.now());
        return ResponseEntity.ok(storeInfoRepository.save(storeInfo));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStoreInfo(@PathVariable Long id) {
        Optional<StoreInfoEntity> existing = storeInfoRepository.findById(id);
        if (existing.isPresent()) {
            storeInfoRepository.deleteById(id);
            return ResponseEntity.ok("Deleted successfully with ID: " + id);
        } else {
            return ResponseEntity.badRequest().body("StoreInfo with ID " + id + " not found!");
        }
    }
}
