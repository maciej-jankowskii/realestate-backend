package com.realestate.service;

import com.realestate.dto.LandPropertyDto;
import com.realestate.enums.TypeOfLand;
import com.realestate.exceptions.CannotDeleteResourceException;
import com.realestate.exceptions.ResourceNotFoundException;
import com.realestate.mapper.LandMapper;
import com.realestate.model.Property.Land;
import com.realestate.repository.LandRepository;
import com.realestate.repository.OffersRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LandService {

    private final LandRepository landRepository;
    private final LandMapper landMapper;
    private final OffersRepository offersRepository;
    private final ValidationService validationService;

    public LandService(LandRepository landRepository, LandMapper landMapper, OffersRepository offersRepository, ValidationService validationService) {
        this.landRepository = landRepository;
        this.landMapper = landMapper;
        this.offersRepository = offersRepository;
        this.validationService = validationService;
    }

    @Transactional
    public LandPropertyDto saveLand(@Valid LandPropertyDto dto) {
        Land land = landMapper.map(dto);
        validationService.validateData(land);
        Land saved = landRepository.save(land);
        return landMapper.map(saved);
    }

    public LandPropertyDto getLandById(Long id) {
        return landRepository.findById(id).map(landMapper::map).orElseThrow(() -> new ResourceNotFoundException("Land not found"));
    }

    public List<LandPropertyDto> getAllLands(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Land> landPage = landRepository.findAll(pageable);
        List<LandPropertyDto> dtos = landPage.getContent().stream()
                .map(landMapper::map)
                .collect(Collectors.toList());
        return dtos;
    }

//    public List<LandPropertyDto> filterLands(
//            String address,
//            BigDecimal minPrice,
//            BigDecimal maxPrice,
//            String typeOfLand,
//            Double minArea,
//            Double maxArea,
//            Boolean buildingPermit
//    ) {
//        List<Land> allLands = (List<Land>) landRepository.findAll();
//        return allLands.stream().filter(land -> (address == null || land.getAddress().contains(address))
//                        && (minPrice == null || land.getPrice().compareTo(minPrice) >= 0)
//                        && (maxPrice == null || land.getPrice().compareTo(maxPrice) <= 0)
//                        && (typeOfLand == null || land.getTypeOfLand().name().equals(typeOfLand))
//                        && (minArea == null || land.getArea() >= minArea)
//                        && (maxArea == null || land.getArea() <= maxArea)
//                        && (buildingPermit == null || land.getBuildingPermit().equals(buildingPermit)))
//                .map(landMapper::map)
//                .collect(Collectors.toList());
//    }

    @Transactional
    public void updateLand(Long id, @Valid LandPropertyDto dto) {
        Land land = landRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Land not found"));
        validationService.validateData(land);
        updateLand(dto, land);
        landRepository.save(land);
    }

    public void deleteLand(Long id) {
        Land land = landRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Land not found"));

        if (offersRepository.existsByPropertyId(land.getId())){
            throw new CannotDeleteResourceException("Cannot delete land assigned to offer");
        }

        landRepository.deleteById(id);
    }


    private static void updateLand(LandPropertyDto dto, Land land) {
        land.setAddress(dto.getAddress());
        land.setPrice(dto.getPrice());
        land.setDescription(dto.getDescription());
        land.setTypeOfLand(TypeOfLand.valueOf(dto.getTypeOfLand()));
        land.setArea(dto.getArea());
        land.setBuildingPermit(dto.getBuildingPermit());
    }
}
