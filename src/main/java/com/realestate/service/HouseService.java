package com.realestate.service;

import com.realestate.dto.HousePropertyDto;
import com.realestate.mapper.HouseMapper;
import com.realestate.model.Property.House;
import com.realestate.repository.HouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class HouseService {

    private final HouseRepository houseRepository;
    private final HouseMapper houseMapper;

    public HouseService(HouseRepository houseRepository, HouseMapper houseMapper) {
        this.houseRepository = houseRepository;
        this.houseMapper = houseMapper;
    }

    @Transactional
    public HousePropertyDto saveHouse(HousePropertyDto dto){
        House house = houseMapper.map(dto);
        House saved = houseRepository.save(house);
        return houseMapper.map(saved);
    }

    public Optional<HousePropertyDto> getHouseById(Long id){
        return houseRepository.findById(id).map(houseMapper::map);
    }
}
