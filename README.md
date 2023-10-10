### Multi tenancy with Spring Boot and MongoDB

There are 3 ways to implement multi tenancy within your application. 
1. Shared schema with a tenant identifier
2. Collection per tenant
3. Database per tenant

In all the 3 approaches, it is important to capture tenant information so that your application can process requests efficiently.

### Storing Tenant Information
To capture tenant information, we make use of the tenantId, accepting it as an HTTP header in the request. Before processing, we intercept and store this information using a Java class named  ```ThreadLocal```, ensuring accessibility for the current running thread throughout execution. 

ThreadLocal allows you to store variables that can only be accessed by the thread that created it.

* **TenantContext** class encapsulates the tenantId and utilizes a threadLocal variable to manage tenant information.

```agsl
@Component
public class TenantContext {

    protected static final ThreadLocal<String> threadLocal = new InheritableThreadLocal<String>() {
        @Override
        protected String initialValue() {
            try {
                return String.class.getDeclaredConstructor().newInstance();
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
        @Override
        protected String childValue(String parentValue) {
            return new String(parentValue);
        }
    };

    public static void setTenantId(String tenantId) {
        threadLocal.set(tenantId);
    }

    public static String getTenantId() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}
```

Here we are also overriding ```childValue()``` method so that the child threads that a parent thread has created can also access the same tenant information.

* **HTTP Interceptor** implements Spring's ```HandlerInterceptor``` to set and clear the tenantId during pre-processing and post-processing stages. It is important to clear the tenant information post-processing to avoid memory leaks. 

```agsl
@AllArgsConstructor
@Slf4j
@Component
public class HttpInterceptor implements HandlerInterceptor {

    private static final String TENANT_ID_HEADER = "X-Tenant";

    @Autowired
    TenantContext tenantContext;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
                             final Object handler) {
        String tenantId = request.getHeader(TENANT_ID_HEADER);
        TenantContext.setTenantId(tenantId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) {
        TenantContext.clear();
    }
}
```

### 1. Shared schema with a tenant identifier

All tenants share the same database and schema. A tenant identifier is used to distinguish documents within a collection.

To facilitate DB calls, we make use of ```find(Query query, Class<T> entityClass)``` method in  ```TenantAwareMongoTemplate``` class that internally utilizes ```MongoTemplate find()``` method. This method has a mandatory filter on tenantId which we can get it from ```TenantContext```, eliminating the need to pass tenantId as an argument from the service layer. 

```agsl
public <T> List<T> find(Query query, Class<T> entityClass) {
    applyTenantFilter(query);
    return mongoTemplate.find(query, entityClass);
}


private void applyTenantFilter(Query query) {
    String tenantId = tenantContext.getTenantId();
    if (tenantId != null) {
        query.addCriteria(Criteria.where("tenantId").is(tenantId));
    }
}
```

### 2. Collection per tenant

In this approach, each tenant will has a separate collection with a similar schema defined in the same database. Lets says we have a collection that stores employee information, then we shall have collections with names such as ```employee_tenantA```, ```employee_tenantB``` etc.

Here, we shall use the same entity POJO ```Employee``` for all the tenants. But at the time of invoking the respective collection, we shall set the collection name suffix at runtime using ```"#{@appConfig.getCollectionName('Employee')}"```

```getCollectionName() in AppConfig``` is a bean that tells Spring which collection name has to be called at runtime.
```agsl
@Configuration
public class AppConfig {

    public String getCollectionName(String collection) {
        return collection + '_' + TenantIdProvider.getCurrentTenant();
    }
}
```

Please note the ```@Document``` annotation in the below entity.
```agsl
@Data
@NoArgsConstructor
@Document(collection = "#{@appConfig.getCollectionName('Employee')}")
public class Employee {

    @Id
    private String id;
    private String firstName;
    private String lastName;
}
```

### 3. Database per tenant

In this scenario, the application connects to different databases based on the tenant. Database properties are defined in the application.yml file, allowing dynamic database switching at runtime.
```agsl
multi-tenant:
  mongos:
    defaultTenant: tenant_a
    tenantProperties:
      - tenant: tenant_a
        properties:
          uri: "mongodb://localhost:27017"
          database: tenant_a_db
      - tenant: tenant_b
        properties:
          uri: "mongodb://localhost:27017"
          database: tenant_b_db
server:
  port: 8080
```
**Note:** Make sure the tenant value you pass in application.yml is same as the one you get from HTTP header. 

We override the ```getMongoDatabase()``` method in the ```SimpleMongoClientDatabaseFactory``` class to return the appropriate ```MongoDatabase``` instance based on the tenant information.

```agsl
@Override
public MongoDatabase getMongoDatabase() throws DataAccessException {
    final String tenant = tenantContext.getTenantId();
    if (tenant != null) {
        final TenantMongoClient tenantMongoClient = multiTenantConfig.getMultiTenantConfig().get(tenant);
        if (tenantMongoClient == null) {
            throw new RuntimeException("Tenant not found " + tenant);
        }
        return tenantMongoClient.getMongoClient().getDatabase(tenantMongoClient.getDatabase());
    } else
        return getMongoClient().getDatabase(getDefaultDatabaseName());
}
```

All the database information is mapped to ```MongoAppProperties```, and ```MongoClient``` beans are loaded at the startup utilizing ```MultiTenantConfig``` for configuration.

```agsl
@Getter
@RequiredArgsConstructor
public class MultiTenantConfig {

    private HashMap<String, TenantMongoClient> multiTenantConfig;
    private final MongoAppProperties mongoAppProperties;

    @PostConstruct
    public void multiTenantMongoConfig() {
        final List<MongoAppProperties.TenantProperties> multiTenantList = mongoAppProperties.getProperties();
        multiTenantConfig = new HashMap<>();

        for (final MongoAppProperties.TenantProperties multiTenant : multiTenantList) {
            final String connectionUri = multiTenant.getProperties().getUri();
            MongoClient client;

            if (connectionUri != null)
                client = MongoClients.create(connectionUri);
            else
                throw new RuntimeException("db config props are missing");

            final String database = multiTenant.getProperties().getDatabase();
            final TenantMongoClient tenantMongoClient = new TenantMongoClient(client, database);
            this.multiTenantConfig.put(multiTenant.getTenant(), tenantMongoClient);
            
            if (multiTenant.getTenant().equals(mongoAppProperties.getDefaultTenant()))
                multiTenantConfig.put("DEFAULT", tenantMongoClient);
        }
    }

    @PreDestroy
    public void destroy() {
        multiTenantConfig.values().forEach(mongo -> mongo.getMongoClient().close());
    }
}
```

