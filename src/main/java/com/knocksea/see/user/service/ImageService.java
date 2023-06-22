package com.knocksea.see.user.service;

import com.knocksea.see.auth.TokenUserInfo;
import com.knocksea.see.product.entity.ProductCategory;
import com.knocksea.see.user.entity.FishingSpot;
import com.knocksea.see.user.entity.SeaImage;
import com.knocksea.see.user.entity.Ship;
import com.knocksea.see.user.entity.User;
import com.knocksea.see.user.repository.FishingSpotRepository;
import com.knocksea.see.user.repository.ImageRepository;
import com.knocksea.see.user.repository.ShipRepository;
import com.knocksea.see.user.repository.UserRepository;
import com.knocksea.see.validation.dto.request.ValidationCreateDTO;
import com.knocksea.see.validation.entity.Validation;
import com.knocksea.see.validation.repository.ValidationRepository;
import com.knocksea.see.validation.service.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.knocksea.see.validation.entity.ValidationType.SHIP;
import static com.knocksea.see.validation.entity.ValidationType.SPOT;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ImageService {


    //배정보 얻기용
    private final ShipRepository shipRepository;

    //이미지 저장용
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final FishingSpotRepository fishingSpotRepository;

    private final ValidationRepository validationRepository;

    @Value("${upload.path}")
    private String uploadRootPath2;


    //DB에 선박 이미지경로 저장함수
    public void saveShipImages(List<MultipartFile> shipImages, TokenUserInfo userInfo) throws IOException {

        User user = userRepository.findById(userInfo.getUserId()).orElseThrow(() -> new RuntimeException("유저 없어 새꺄"));
        Ship foundShipByUserId = shipRepository.findByUser(user);

        List<String> strings = uploadShipImage(shipImages);

        Long typeNumber = 1L;

        for (String string : strings) {
            SeaImage save = imageRepository.save(SeaImage
                    .builder()
                    .imageName(makeDateFormatDirectory(uploadRootPath2)+"/"+string)
                    .ship(foundShipByUserId)
                    .typeNumber(typeNumber++)
                    .imageType(ProductCategory.SHIP).build());
        }

    }

    public void saveValidationImg(List<MultipartFile> validationImg, ValidationCreateDTO dto) throws IOException {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("ImageService : 존재하지 않는 유저입니다."));
        log.info("ImageService user : " + user);

        List<Validation> fondByUserAndValidationType = validationRepository.findByUserAndValidationType(user, dto.getValidationType());
        log.info("fondByUserAndValidationType : "+fondByUserAndValidationType);

        List<String> listValidationImg = uploadValidationImage(validationImg);
        log.info("listValidationImg : "+listValidationImg);
        log.info("dto.getValidationType()"+dto.getValidationType());
        if(dto.getValidationType().equals(SHIP)){ //이미지 2장 0번 인덱스가 선박 등록증, 1번 인덱스가 선박 면허증
            log.info("SHIP 들어옴");
            imageRepository.save(
                    SeaImage.builder()
                            .imageName(makeDateFormatDirectory(uploadRootPath2)+"/"+ listValidationImg.get(0))
                            .validation(fondByUserAndValidationType.get(0))
                            .imageType(ProductCategory.VALIDATIONSHIPREGI)
                            .build());

            imageRepository.save(
                    SeaImage.builder()
                            .imageName(makeDateFormatDirectory(uploadRootPath2)+"/"+ listValidationImg.get(1))
                            .validation(fondByUserAndValidationType.get(0))
                            .imageType(ProductCategory.VALIDATIONSHIPLICENSE)
                            .build());

        }else if(dto.getValidationType().equals(SPOT)){//0번 인덱스가 사업자 등록증 번호
            log.info("SPOT 들어옴");
           imageRepository.save(
                    SeaImage.builder()
                            .imageName(makeDateFormatDirectory(uploadRootPath2)+"/"+ listValidationImg.get(0))
                            .validation(fondByUserAndValidationType.get(0))
                            .imageType(ProductCategory.VALIDATIONBUSINESSREGI)
                            .build());
        }

    }
    //검증 실제 이미지 저장함수
    public List<String> uploadValidationImage(List<MultipartFile> validationImg) throws IOException {
        //루트 디렉토리가 존재하는지 확인후 존재하지않으면 생성하는 코드
        List<String> uniqueFilenames = new ArrayList<>();

        String s = makeDateFormatDirectory(uploadRootPath2);

        for (MultipartFile validationImage : validationImg) {
            String originalFilename = validationImage.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

            // Save the file
            File uploadFile = new File(s+"/"+uniqueFileName);
            validationImage.transferTo(uploadFile);

            uniqueFilenames.add(uniqueFileName);
        }
        return uniqueFilenames;
    }


    //배 실제 이미지 저장함수
    public List<String> uploadShipImage(List<MultipartFile> shipImages) throws IOException {
        //루트 디렉토리가 존재하는지 확인후 존재하지않으면 생성하는 코드
        List<String> uniqueFilenames = new ArrayList<>();

        String s = makeDateFormatDirectory(uploadRootPath2);


        for (MultipartFile shipImage : shipImages) {
            String originalFilename = shipImage.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

            // Save the file
            File uploadFile = new File(s+"/"+uniqueFileName);
            shipImage.transferTo(uploadFile);

            uniqueFilenames.add(uniqueFileName);

        }

        return uniqueFilenames;

    }



    //db에 이미지 경로 저장함수
    public void saveSpotImages(List<MultipartFile> spotImages, TokenUserInfo userInfo) throws IOException {

        User user = userRepository.findById(userInfo.getUserId()).orElseThrow(() -> new RuntimeException("유저가 존재하지않습니다"));
        FishingSpot findBySpot = fishingSpotRepository.findByUser(user);

        List<String> strings = uploadSpotImage(spotImages);

        Long typeNumber = 1L;

        for (String string : strings) {
            SeaImage save = imageRepository
                    .save(SeaImage.builder()
                            .imageName(makeDateFormatDirectory(uploadRootPath2)+"/"+string)
                    .spot(findBySpot)
                            .typeNumber(typeNumber++)
                    .imageType(ProductCategory.SPOT).build());

        }



    }

    //낚시터 실제 이미지 저장함수
    private List<String> uploadSpotImage(List<MultipartFile> spotImages) throws IOException {
        //루트 디렉토리가 존재하는지 확인후 존재하지않으면 생성하는 코드
        List<String> uniqueFilenames = new ArrayList<>();

        String s = makeDateFormatDirectory(uploadRootPath2);


        for (MultipartFile spotImage : spotImages) {
            String originalFilename = spotImage.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

            // Save the file
            File uploadFile = new File(s+"/"+uniqueFileName);
            spotImage.transferTo(uploadFile);

            uniqueFilenames.add(uniqueFileName);

        }


        return uniqueFilenames;

    }

    /*
     * 루트 경로를 받아서 일자별로 폴더를 생성하 후
     * 루트경로 + 날짜폴더 경로를 리턴
     * @param rootPath - 파일 업로드 루트 경로
     * @return - 날짜 폴더 경로가 포함된 새로운 업로드 경로
     * */
    public static String makeDateFormatDirectory(String rootPath){
        //오늘 연월일 날짜정복 가져오기
        LocalDateTime now = LocalDateTime.now();
        int y = now.getYear();
        int m = now.getMonthValue();
        int d = now.getDayOfMonth();

        List<String> dateInfo = List.of(String.valueOf(y), len2(m), len2(d));

        String directoryPath = rootPath;
        for (String s : dateInfo) {
            directoryPath +="/" + s;
            File f = new File(directoryPath);
            if(!f.exists()) f.mkdir();
        }

        return directoryPath;
    }

    private static String len2(int n) {
        return new DecimalFormat("00").format(n);
    }



    //수정할 사진경로 이미지 DB에 저장하는 함수
    public void modifyShipImages(List<MultipartFile> shipImages, TokenUserInfo userInfo) throws IOException {

        //유저 정보가져오기
        User user = userRepository.findById(userInfo.getUserId()).orElseThrow(() -> {
            throw new RuntimeException("해당 유저는존재하지않습니다");
        });
        //유저 정보로 배 가져오기
        Ship findSHipByUser = shipRepository.findByUser(user);

        //유저정보로 가져온 배 정보로 이미지 삭제하기
        imageRepository.deleteByShip(findSHipByUser);

        List<String> uniqueFilenames = new ArrayList<>();

        String s = makeDateFormatDirectory(uploadRootPath2);


        for (MultipartFile shipImage : shipImages) {
            String originalFilename = shipImage.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

            // Save the file
            File uploadFile = new File(s+"/"+uniqueFileName);
            shipImage.transferTo(uploadFile);

            uniqueFilenames.add(uniqueFileName);

        }


        Long typeNumber = 1L;

        for (String string : uniqueFilenames) {
            SeaImage save = imageRepository.save(SeaImage
                    .builder()
                    .imageName(s+"/"+string)
                    .ship(findSHipByUser)
                    .typeNumber(typeNumber++)
                    .imageType(ProductCategory.SHIP).build());
        }

    }

    //수정할 낚시터 사진경로 db에 저장하는함수
    public void modifySpotImages(List<MultipartFile> spotImages, TokenUserInfo userInfo) throws IOException {
        //유저 정보가져오기
        User user = userRepository.findById(userInfo.getUserId()).orElseThrow(() -> {
            throw new RuntimeException("해당 유저는존재하지않습니다");
        });
        //유저 정보로 낚시터 가져오기
        FishingSpot findSpotByUser = fishingSpotRepository.findByUser(user);

        //유저 정보로 찾은 낚시터 정보로 이미지 삭제하기
        imageRepository.deleteBySpot(findSpotByUser);

        List<String> uniqueFilenames = new ArrayList<>();

        String s = makeDateFormatDirectory(uploadRootPath2);


        for (MultipartFile spotImage : spotImages) {
            String originalFilename = spotImage.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

            // Save the file
            File uploadFile = new File(s+"/"+uniqueFileName);
            spotImage.transferTo(uploadFile);

            uniqueFilenames.add(uniqueFileName);

        }

        Long typeNumber = 1L;

        for (String string : uniqueFilenames) {
            SeaImage save = imageRepository.save(SeaImage
                    .builder()
                    .imageName(s+"/"+string)
                    .spot(findSpotByUser)
                    .typeNumber(typeNumber++)
                    .imageType(ProductCategory.SPOT).build());
        }

    }

}
