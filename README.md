### How to Run
* Simply run `mvn clean install` and run in your IDE.
* Swagger documentation will be available at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).
* H2 dashboard will be available at [http://localhost:8080/h2-console/](http://localhost:8080/h2-console/).
* Import the Postman collection from [ing-brokerage-demo.postman_collection.json](ing-brokerage-demo.postman_collection.json) into your Postman.
* Import the Postman environment from [ing-brokerage-demo-env.postman_environment.json](ing-brokerage-demo-env.postman_environment.json) into your Postman.

### How to Test
1. Create a Customer with the `/register` API, using either `ROLE_USER` or `ROLE_ADMIN`.
2. Authenticate with the `/login` API to get a token; the token will be saved to `{{accessToken}}` automatically.
3. Call the APIs using the token.

### Assumptions
- Asset's ‘Usable size’ is only reflected from ‘size’ when the Order is matched.

### Architecture and Stack
- **Stack:** Java 21, Spring Boot 3.3.5, Spring Security 6.1 , Mokito, H2 DB

I tried to implement Domain-Driven Design (DDD) and Event-Driven Design (EDD). `Order` and `Asset` are domain entities, and all business operation and validations are processed within the domain.

`Order` and `Asset` are loosely coupled; they communicate with each other via events (synchronous process). I implemented `@Version` on entities to avoid concurrency issues. Ideally, concurrent API requests for the same asset or order should be queued and processed instead of throwing an error, but a message queue system needs to be implemented (which requires more time).

Some parts might seem unconventional but are required for DDD (like generating IDs and manually setting them for domain entities). I tried to explain this further in my article: [A Soft Introduction to Domain-Driven Design: From Theory to Java Code Implementation](https://medium.com/@ygnhmt/a-soft-introduction-to-domain-driven-design-from-theory-to-java-code-implementation-part-2-5aa7e1cfef65).

* I couldn't expose the admin user endpoint to match pending orders because of time issues, sorry : ( 
