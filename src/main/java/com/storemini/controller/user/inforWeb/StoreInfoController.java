package com.storemini.controller.user.inforWeb;

import com.storemini.model.user.entity.StoreInfoEntity;
import com.storemini.model.user.repository.StoreInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class StoreInfoController {

    private final StoreInfoRepository storeInfoRepository;

    @GetMapping("/inforWeb")
    public ResponseEntity<List<StoreInfoEntity>> getAllStoreInfo() {
        return ResponseEntity.ok(storeInfoRepository.findAll());
    }

}