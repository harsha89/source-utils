servicechaining.samples;
import ballerina.lang.messages;
import ballerina.net.http;
import ballerina.lang.system;
@http:BasePath{value:"/ABCBank"}
service ATMLocator {
    @http:POST{}
    resource locator (message servicechaining.samples:m) {
        http:ClientConnector bankInfoService = create http:ClientConnector("http://localhost:9090/bankinfo/product");
        http:ClientConnector branchLocatorService = create http:ClientConnector("http://localhost:9090/branchlocator/product");
        message backendServiceReq = {};
        json jsonLocatorReq = messages:getJsonPayload(servicechaining.samples:m);
        string zipCode = ;
        system:println("Zip Code " + servicechaining.samples:zipCode);
        json branchLocatorReq = {"BranchLocator":{"ZipCode":""}};
        servicechaining.samples:branchLocatorReq."BranchLocator"."ZipCode" = servicechaining.samples:zipCode;
        messages:setJsonPayload(servicechaining.samples:backendServiceReq, servicechaining.samples:branchLocatorReq);
        message response = http:ClientConnector.post(servicechaining.samples:branchLocatorService, "", servicechaining.samples:backendServiceReq);
        json branchLocatorRes = messages:getJsonPayload(servicechaining.samples:response);
        string branchCode = ;
        system:println("Branch Code " + servicechaining.samples:branchCode);
        json bankInfoReq = {"BranchInfo":{"BranchCode":""}};
        servicechaining.samples:bankInfoReq."BranchInfo"."BranchCode" = servicechaining.samples:branchCode;
        messages:setJsonPayload(servicechaining.samples:backendServiceReq, servicechaining.samples:bankInfoReq);
        servicechaining.samples:response = http:ClientConnector.post(servicechaining.samples:bankInfoService, "", servicechaining.samples:backendServiceReq);
        reply servicechaining.samples:response;
    }
}
