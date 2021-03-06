/**
 * Copyright 2014-2019  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.webase.node.mgr.transaction;

import com.alibaba.fastjson.JSON;
import com.webank.webase.node.mgr.base.entity.BasePageResponse;
import com.webank.webase.node.mgr.base.entity.BaseResponse;
import com.webank.webase.node.mgr.base.entity.ConstantCode;
import com.webank.webase.node.mgr.base.enums.SqlSortType;
import com.webank.webase.node.mgr.base.exception.NodeMgrException;
import com.webank.webase.node.mgr.frontinterface.FrontInterfaceService;
import com.webank.webase.node.mgr.transaction.entity.TbTransHash;
import com.webank.webase.node.mgr.transaction.entity.TransListParam;
import com.webank.webase.node.mgr.transaction.entity.TransReceipt;
import com.webank.webase.node.mgr.transaction.entity.TransactionInfo;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(value = "transaction")
public class TransHashController {

    @Autowired
    private TransHashService transHashService;



    /**
     * query trans list.
     */
    @GetMapping(value = "/transList/{groupId}/{pageNumber}/{pageSize}")
    public BasePageResponse queryTransList(@PathVariable("groupId") Integer groupId,
        @PathVariable("pageNumber") Integer pageNumber,
        @PathVariable("pageSize") Integer pageSize,
        @RequestParam(value = "transactionHash", required = false) String transHash,
        @RequestParam(value = "blockNumber", required = false) BigInteger blockNumber)
        throws NodeMgrException, Exception {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info(
            "start queryTransList. startTime:{} groupId:{} pageNumber:{} pageSize:{} "
                + "transaction:{}",
            startTime.toEpochMilli(), groupId, pageNumber, pageSize, transHash);

        TransListParam queryParam = new TransListParam(transHash, blockNumber);

        Integer count = transHashService.queryCountOfTran(groupId, queryParam);
        if (count != null && count > 0) {
            Integer start = Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize)
                .orElse(null);
            queryParam.setStart(start);
            queryParam.setPageSize(pageSize);
            queryParam.setFlagSortedByBlock(SqlSortType.DESC.getValue());
            List<TbTransHash> transList = transHashService.queryTransList(groupId,queryParam);
            pageResponse.setData(transList);
            pageResponse.setTotalCount(count);
        } else {
            List<TbTransHash> transList = transHashService.getTransListFromChain(groupId,transHash,blockNumber);
            //result
            if (transList.size() > 0) {
                pageResponse.setData(transList);
                pageResponse.setTotalCount(transList.size());
            }
        }

        log.info("end queryBlockList useTime:{} result:{}",
            Duration.between(startTime, Instant.now()).toMillis(), JSON.toJSONString(pageResponse));
        return pageResponse;
    }

    /**
     * get transaction receipt.
     */
    @GetMapping("/transactionReceipt/{groupId}/{transHash}")
    public BaseResponse getTransReceipt(@PathVariable("groupId") Integer groupId,
        @PathVariable("transHash") String transHash)
        throws NodeMgrException {
        Instant startTime = Instant.now();
        log.info("start getTransReceipt startTime:{} groupId:{} transaction:{}",
            startTime.toEpochMilli(), groupId, transHash);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TransReceipt transReceipt = transHashService.getTransReceipt(groupId, transHash);
        baseResponse.setData(transReceipt);
        log.info("end getTransReceipt useTime:{} result:{}",
            Duration.between(startTime, Instant.now()).toMillis(), JSON.toJSONString(baseResponse));
        return baseResponse;
    }

    /**
     * get transaction by hash.
     */
    @GetMapping("/transInfo/{groupId}/{transHash}")
    public BaseResponse getTransaction(@PathVariable("groupId") Integer groupId,
        @PathVariable("transHash") String transHash)
        throws NodeMgrException {
        Instant startTime = Instant.now();
        log.info("start getTransaction startTime:{} groupId:{} transaction:{}",
            startTime.toEpochMilli(), groupId, transHash);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        TransactionInfo transInfo = transHashService.getTransaction(groupId, transHash);
        baseResponse.setData(transInfo);
        log.info("end getTransaction useTime:{} result:{}",
            Duration.between(startTime, Instant.now()).toMillis(), JSON.toJSONString(baseResponse));
        return baseResponse;
    }
}
