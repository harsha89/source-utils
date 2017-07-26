/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ballerina.sourcegen;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.lang3.StringUtils;
import org.ballerinalang.model.BLangPackage;
import org.ballerinalang.model.BallerinaFile;
import org.ballerinalang.model.GlobalScope;
import org.ballerinalang.model.types.BTypes;
import org.ballerinalang.util.parser.BallerinaLexer;
import org.ballerinalang.util.parser.BallerinaParser;
import org.ballerinalang.util.parser.antlr4.BLangAntlr4Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ballerina.sourcegen.common.generator.BallerinaSourceGenerator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This is the main execution point
 */
public class BalModelToSourceConvertExecutor {

    private static Logger logger = LoggerFactory.getLogger(BalModelToSourceConvertExecutor.class);

    public static void main(String[] args) {
        try {
           createBalFile();
        } catch (IOException e) {
            logger.error("Unable to generate ballerina file.", e);
        }
    }

    private static void createBalFile()
            throws IOException {
        String filePathStr = "/home/harsha/wso2/apim/repos/ballerina-source-gen/src/main/resources/ballerina-samples/ATMLocatorService.bal";
        String destination = "/home/harsha/wso2/apim/repos/ballerina-source-gen/src/main/resources/ballerina-samples/generated-ballerina-source/ATMLocatorService.bal";
        Path filePath = deriveFilePath("ATMLocatorService.bal", filePathStr);


        InputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(filePathStr)));
        ANTLRInputStream antlrInputStream = new ANTLRInputStream(inputStream);
        BallerinaLexer ballerinaLexer = new BallerinaLexer(antlrInputStream);
        CommonTokenStream ballerinaToken = new CommonTokenStream(ballerinaLexer);

        BallerinaParser ballerinaParser = new BallerinaParser(ballerinaToken);
        BallerinaSourceGenErrorStrategy errorStrategy = new BallerinaSourceGenErrorStrategy();
        ballerinaParser.setErrorHandler(errorStrategy);

        GlobalScope globalScope = GlobalScope.getInstance();
        BTypes.loadBuiltInTypes(globalScope);
        BLangPackage bLangPackage = new BLangPackage(globalScope);
        BLangPackage.PackageBuilder packageBuilder = new BLangPackage.PackageBuilder(bLangPackage);
        BallerinaSourceGenModelBuilder bLangModelBuilder = new BallerinaSourceGenModelBuilder(packageBuilder,
                StringUtils.EMPTY);
        BLangAntlr4Listener ballerinaBaseListener = new BLangAntlr4Listener(true, ballerinaToken, bLangModelBuilder,
                filePath);
        ballerinaParser.addParseListener(ballerinaBaseListener);
        ballerinaParser.compilationUnit();
        BallerinaFile bFile = bLangModelBuilder.build();
        BallerinaSourceGenerator sourceGenerator = new BallerinaSourceGenerator();
        sourceGenerator.generate(bFile, destination);

    }

    private static java.nio.file.Path deriveFilePath(String fileName, String filePath) {
        return Paths.get(filePath + File.separator + fileName);
    }
}
