package ddog.user.presentation.detailInfo;

import ddog.auth.annotation.AuthPayload;
import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.user.application.DetailInfoService;
import ddog.user.presentation.detailInfo.dto.DetailResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static ddog.auth.exception.common.CommonResponseEntity.success;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class DetailInfoController {
    private final DetailInfoService detailInfoService;

    @GetMapping("/shops")
    public CommonResponseEntity<DetailResp> getBeautyShopsList(@RequestParam(required = false) String address,
                                                               @AuthPayload(required = false) PayloadDto payloadDto,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "5") int size) {
        return success(detailInfoService.findBeautyShops(payloadDto.getAccountId(), address, page, size));
    }

    @GetMapping("/vets")
    public CommonResponseEntity<DetailResp> getVetsList(@RequestParam(required = false) String address,
                                                        @AuthPayload(required = false) PayloadDto payloadDto,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "5") int size){
        return success(detailInfoService.findVets(payloadDto.getAccountId(), address, page, size));
    }

    @GetMapping("/shop/{shopId}")
    public CommonResponseEntity<DetailResp.ShopDetailInfo> getBeautyShopDetail(@PathVariable Long shopId) {
        return success(detailInfoService.findBeautyShop(shopId));
    }

    @GetMapping("/vet/{vetAccountId}")
    public CommonResponseEntity<DetailResp.VetDetailInfo> getVetDetail(@PathVariable Long vetAccountId) {
        return success(detailInfoService.findVetById(vetAccountId));
    }

    @GetMapping("/groomer/{groomerAccountId}")
    public CommonResponseEntity<DetailResp.GroomerDetailInfo> getGroomerDetail(@PathVariable Long groomerAccountId) {
        return success(detailInfoService.findGroomerById(groomerAccountId));
    }
}
