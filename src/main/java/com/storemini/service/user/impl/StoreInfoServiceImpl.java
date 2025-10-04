package com.storemini.service.user.impl;

import com.storemini.model.user.entity.StoreInfoEntity;
import com.storemini.model.user.repository.StoreInfoRepository;
import com.storemini.service.user.StoreInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreInfoServiceImpl implements StoreInfoService {

    private final StoreInfoRepository storeInfoRepository;

    @Override
    public List<StoreInfoEntity> getAllStoreInfo() {
        return storeInfoRepository.findAll();
    }
}
