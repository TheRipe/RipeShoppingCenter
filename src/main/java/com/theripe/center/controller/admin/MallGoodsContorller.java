package com.theripe.center.controller.admin;

import com.theripe.center.bean.GoodsCategory;
import com.theripe.center.bean.GoodsInfo;
import com.theripe.center.common.CategoryLevelEnum;
import com.theripe.center.common.Constants;
import com.theripe.center.common.ServiceResultEnum;
import com.theripe.center.service.MallCategoryService;
import com.theripe.center.service.MallGoodsService;
import com.theripe.center.utils.PageQueryUtil;
import com.theripe.center.utils.Result;
import com.theripe.center.utils.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author TheRipe
 * @create 2021/6/24 12:11
 */
@RequestMapping("/admin")
@Controller
public class MallGoodsContorller {
    @Autowired
    private MallCategoryService mallCategoryService;

    @Resource
    private MallGoodsService mallGoodsService;
    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request) {
        request.setAttribute("path", "theripe_mall_goods");
        return "admin/theripe_mall_goods";
    }

    @GetMapping("/goods/edit")
    public String editGoods(HttpServletRequest request) {
        request.setAttribute("path","theripe_mall_goods-edit");
        List<GoodsCategory> firstLevelCategories  = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            List<GoodsCategory> secondLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), CategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                List<GoodsCategory> thirdLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), CategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path","goods-edit");
                return "admin/theripe_mall_goods_edit";
            }
        }
        return "error/error_5xx";

    }
    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Long id) {
        request.setAttribute("path","edit");
        GoodsInfo goodsInfo = mallGoodsService.getMallGoodsById(id);
        if(goodsInfo == null) {
            return "error/error_400";
        }
        if (goodsInfo.getGoodsCategoryId() > 0) {
            if (goodsInfo.getGoodsCategoryId() != null || goodsInfo.getGoodsCategoryId() > 0) {
                GoodsCategory goodsCategory1 = mallCategoryService.slectById(goodsInfo.getGoodsCategoryId());
                if (goodsCategory1 != null && goodsCategory1.getCategoryLevel() == CategoryLevelEnum.LEVEL_THREE.getLevel()) {
                    List<GoodsCategory> firstLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel());
                    List<GoodsCategory> thirdLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(goodsCategory1.getCategoryId()), CategoryLevelEnum.LEVEL_THREE.getLevel());
                    GoodsCategory secondCategory = mallCategoryService.slectById(goodsCategory1.getCategoryId());
                    if (secondCategory != null) {
                        List<GoodsCategory> secondLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getCategoryId()), CategoryLevelEnum.LEVEL_TWO.getLevel());
                        GoodsCategory firstCategory = mallCategoryService.slectById(secondCategory.getCategoryId());
                        if (firstCategory != null) {
                            request.setAttribute("firstLevelCategories", firstLevelCategories);
                            request.setAttribute("secondLevelCategories", secondLevelCategories);
                            request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId", firstCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId", secondCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId", goodsCategory1.getCategoryId());
                        }
                    }

                }
            }
        }
        if (goodsInfo.getGoodsCategoryId() == 0) {
            List<GoodsCategory> firstLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                //???????????????????????????????????????????????????????????????
                List<GoodsCategory> secondLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), CategoryLevelEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    //???????????????????????????????????????????????????????????????
                    List<GoodsCategory> thirdLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), CategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }
        request.setAttribute("goods", goodsInfo);
        request.setAttribute("path", "theripe_mall_goods-edit");
        return "admin/theripe_mall_goods_edit";
    }


    /**
     * ??????
     */
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("???????????????");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(mallGoodsService.getMallGoodsPage(pageUtil));
    }

    /**
     * ??????
     */
    @RequestMapping(value = "/goods/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody GoodsInfo newBeeMallGoods) {
        if (StringUtils.isEmpty(newBeeMallGoods.getGoodsName())
                || StringUtils.isEmpty(newBeeMallGoods.getGoodsIntro())
                || StringUtils.isEmpty(newBeeMallGoods.getTag())
                || Objects.isNull(newBeeMallGoods.getOriginalPrice())
                || Objects.isNull(newBeeMallGoods.getGoodsCategoryId())
                || Objects.isNull(newBeeMallGoods.getSellingPrice())
                || Objects.isNull(newBeeMallGoods.getStockNum())
                || Objects.isNull(newBeeMallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(newBeeMallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(newBeeMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("???????????????");
        }
        String result = mallGoodsService.saveMallGoods(newBeeMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }



    /**
     * ??????
     */
    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody GoodsInfo newBeeMallGoods) {
        if (Objects.isNull(newBeeMallGoods.getGoodsId())
                || StringUtils.isEmpty(newBeeMallGoods.getGoodsName())
                || StringUtils.isEmpty(newBeeMallGoods.getGoodsIntro())
                || StringUtils.isEmpty(newBeeMallGoods.getTag())
                || Objects.isNull(newBeeMallGoods.getOriginalPrice())
                || Objects.isNull(newBeeMallGoods.getSellingPrice())
                || Objects.isNull(newBeeMallGoods.getGoodsCategoryId())
                || Objects.isNull(newBeeMallGoods.getStockNum())
                || Objects.isNull(newBeeMallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(newBeeMallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(newBeeMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("???????????????");
        }
        String result = mallGoodsService.updateMallGoods(newBeeMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * ??????
     */
    @GetMapping("/goods/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        GoodsInfo goods = mallGoodsService.getMallGoodsById(id);
        if (goods == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(goods);
    }

    /**
     * ????????????????????????
     */
    @RequestMapping(value = "/goods/status/{sellStatus}", method = RequestMethod.PUT)
    @ResponseBody
    public Result delete(@RequestBody Long[] ids, @PathVariable("sellStatus") int sellStatus) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("???????????????");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("???????????????");
        }
        if (mallGoodsService.batchUpdateSellStatus(ids, sellStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("????????????");
        }
    }


}
