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
package com.webank.webase.node.mgr.front.entity;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * param of send transaction.
 */
@Data
public class TransactionParam {
    private List<Object> abiInfo;
    @NotNull
    private Integer userId;
    private String version;
    @NotBlank
    private String contractName;
    @NotBlank
    private String funcName;
    private String contractAddress;
    private List<Object> funcParam;

    /**
     * init TransactionParam.
     */
    public TransactionParam(Integer userId, String contractAddress, String contractName, String funcName,
        List<Object> funcParam) {
        super();
        this.userId = userId;
        this.contractAddress = contractAddress;
        this.contractName = contractName;
        this.funcName = funcName;
        this.funcParam = funcParam;
    }

    public TransactionParam() {
        super();
    }

}