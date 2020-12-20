package fileupload.web.test.infra.dbunit.annotation;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import fileupload.web.test.infra.dbunit.config.DbUnitConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.test.context.TestExecutionListeners;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration(DbUnitConfig.class)
@TestExecutionListeners(
        listeners = DbUnitTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public @interface DbUnitTest {
}
