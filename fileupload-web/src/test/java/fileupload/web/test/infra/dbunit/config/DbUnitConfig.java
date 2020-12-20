package fileupload.web.test.infra.dbunit.config;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Types;

@Configuration
public class DbUnitConfig {

    @Autowired
    DataSource dataSource;

    @Bean
    public FactoryBean<DatabaseDataSourceConnection> dbUnitDatabaseConnection() {
        // Postgresqlのenumに対応させる
        var databaseConfig = new DatabaseConfigBean();
        databaseConfig.setDatatypeFactory(new PostgresqlDataTypeFactory() {
            @Override
            public boolean isEnumType(String sqlTypeName) {
                return "file_type".equalsIgnoreCase(sqlTypeName);
            }

            @Override
            public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
                if (isEnumType(sqlTypeName)) {
                    return super.createDataType(Types.OTHER, sqlTypeName);
                }
                return super.createDataType(sqlType, sqlTypeName);
            }
        });
        var databaseDataSourceConnectionFactoryBean = new DatabaseDataSourceConnectionFactoryBean(dataSource);
        databaseDataSourceConnectionFactoryBean.setDatabaseConfig(databaseConfig);
        return databaseDataSourceConnectionFactoryBean;
    }
}
