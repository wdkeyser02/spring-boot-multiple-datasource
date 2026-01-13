package willydekeyser;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SpringBootMultpleDataSourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootMultpleDataSourceApplication.class, args);
	}
	
	@Bean()
    DataSource dataDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/db_data");
        dataSource.setUsername("user");
        dataSource.setPassword("password");
        return dataSource;
    }
	
	@Bean()
    DataSource securityDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3307/db_security");
        dataSource.setUsername("user");
        dataSource.setPassword("password");
        return dataSource;
    }
	
	@Bean()
	JdbcTemplate dataJdbcTemplate(@Qualifier("dataDataSource") DataSource dataSource) {
	    return new JdbcTemplate(dataSource);
	}

	@Bean()
	JdbcTemplate securityJdbcTemplate(@Qualifier("securityDataSource") DataSource dataSource) {
	    return new JdbcTemplate(dataSource);
	}

}

record User(String username, String password, boolean enabled) {}
record Authorities(String username, String authority) {}
record Data01(String name, boolean enabled) {}
record Data02(String name, boolean enabled) {}


@Repository
class UserRepository {

	private final JdbcTemplate jdbcTemplate;
		
    public UserRepository(@Qualifier("securityJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAllUsers() {
        String sql = "select * from users;";
        return jdbcTemplate.query(sql, new DataClassRowMapper<>(User.class));
    }

    public List<Authorities> findAllAuthorities() {
        String sql = "select * from authorities;";
        return jdbcTemplate.query(sql, new DataClassRowMapper<>(Authorities.class));
    }
    
}

@Repository
class DataRepository {

	private final JdbcTemplate jdbcTemplate;
	
    public DataRepository(@Qualifier("dataJdbcTemplate") JdbcTemplate jdbcTemplate) {
    	this.jdbcTemplate = jdbcTemplate;
    }

    public List<Data01> findAllData01() {
        String sql = "select * from data01;";
        return jdbcTemplate.query(sql, new DataClassRowMapper<>(Data01.class));
    }

    public List<Data02> findAllData02() {
        String sql = "select * from data02;";
        return jdbcTemplate.query(sql, new DataClassRowMapper<>(Data02.class));
    }
}

@RestController
class Controller {

    @GetMapping("/")
    public String findAllUsers() {
        return """
        	<a href='http://localhost:8080/users'>Users</a><br/>
        	<a href='http://localhost:8080/authorities'>Authorities</a><br/><br/>
        	<a href='http://localhost:8080/data01'>Data01</a><br/>
        	<a href='http://localhost:8080/data02'>Data02</a><br/><br/>
        	""";
    }
}

@RestController
class UserController {

    UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    @GetMapping("/authorities")
    public List<Authorities> findAllAuthorities() {
        return userRepository.findAllAuthorities();
    }
}

@RestController
class DataController {

    DataRepository dataRepository;
    public DataController(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @GetMapping("/data01")
    public List<Data01> findAllData01() {
        return dataRepository.findAllData01();
    }

    @GetMapping("/data02")
    public List<Data02> findAllData02() {
        return dataRepository.findAllData02();
    }

}


