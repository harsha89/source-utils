package org.wso2.ballerina.sourcegen;

import org.ballerinalang.model.BLangPackage;
import org.ballerinalang.model.BallerinaFile;
import org.ballerinalang.model.builder.BLangModelBuilder;

/**
 * A custom model builder for ballerina parser
 */
public class BallerinaSourceGenModelBuilder extends BLangModelBuilder {

    public BallerinaSourceGenModelBuilder(BLangPackage.PackageBuilder packageBuilder, String bFileName) {
        super(packageBuilder, bFileName);
    }

    public BallerinaFile build() {
        return bFileBuilder.build();
    }
}
