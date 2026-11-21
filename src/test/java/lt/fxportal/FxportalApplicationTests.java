package lt.fxportal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import lt.fxportal.application.port.ReferenceDataImporter;

@SpringBootTest
@ActiveProfiles("test")
class FxportalApplicationTests {

    @MockitoBean
    ReferenceDataImporter referenceDataImporter;

    @Test
    void contextLoads() {
    }
}