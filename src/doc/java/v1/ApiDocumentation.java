package v1;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.digital.ho.proving.income.ServiceRunner;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.restassured.operation.preprocess.RestAssuredPreprocessors.modifyUris;
import static org.springframework.restdocs.snippet.Attributes.key;

@SpringApplicationConfiguration(classes = ServiceRunner.class)
@WebIntegrationTest("server.port=0")
@RunWith(SpringJUnit4ClassRunner.class)
public class ApiDocumentation {

    public static final String BASEPATH = "/incomeproving/v1";

    @Rule
    public JUnitRestDocumentation restDocumentationRule = new JUnitRestDocumentation("build/generated-snippets");

    @Value("${local.server.port}")
    private int port;

    private RequestSpecification documentationSpec;

    private RequestSpecification requestSpec;

    private RestDocumentationFilter document =
        document("{method-name}",
            preprocessRequest(
                prettyPrint(),
                modifyUris()
                    .scheme("https")
                    .host("api.host.address")
                    .removePort()
            ),
            preprocessResponse(
                prettyPrint(),
                removeHeaders("Date", "Connection", "Transfer-Encoding")
            )
        );

    // TODO
}
