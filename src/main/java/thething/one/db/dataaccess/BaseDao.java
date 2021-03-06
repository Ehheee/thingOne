package thething.one.db.dataaccess;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import thething.one.db.services.TypeDescriptorService;

public  class BaseDao {

	protected Log logger = LogFactory.getLog(getClass());
	public enum ActionType {
		INSERT, DELETE
	}
	public enum ObjectType{
		THING, USER
	}
	
	
	@Autowired
	protected TypeDescriptorService typeDescriptorService;
	public TypeDescriptorService getTypeDescriptorService() {
		return typeDescriptorService;
	}
	public void setTypeDescriptorService(TypeDescriptorService typeDescriptorService) {
		this.typeDescriptorService = typeDescriptorService;
	}


	
	
	@Autowired
	UniqueIdProvider uniqueIdProvider;
	public UniqueIdProvider getUniqueIdProvider() {
		return uniqueIdProvider;
	}
	public void setUniqueIdProvider(UniqueIdProvider uniqueIdProvider) {
		this.uniqueIdProvider = uniqueIdProvider;
	}

	protected Neo4jOperations neo4jOperations;	
	@Autowired
	public void setNeo4jOperations(Neo4jOperations neo4jOperations) {
		this.neo4jOperations = neo4jOperations;
	}

	/*
	private BasicDataSource dataSource;
	protected JdbcTemplate jdbcTemplate;
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
	    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
	DataSourceTransactionManager transactionManager;
	*/
}
