# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
1. Separate some of the tight couplings like using entity as a DTO (Store), maybe at some point of time instead of using panache etc. we use a different implementation which would mean we would have to re-write this entire code
2. Create a separate service layer for controllers, because again tight coupling, controllers do not need to know about the persistance layer/implementations
3. 
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
1. In this particular scenario it looks like the generated code for Warehouse is using hexagonal architecture, if you ask me personally i would prefer MVC but in terms of using open API generated vs coding everything directly, I would prefer to use a mix and match of both as it seems both could complement each other like say the generated code could serve as a boiler plate code/template on which a dev can continue to work on
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
1. I would prefer to have unit tests and also if possible do BDD, discuss domain and requirements with product and come up with concrete scenarios which then the application need to pass
2. Enforcing could be done in several ways, through team culture and also concretely via some process like setting up pull request analyser and for a PUll request to pass it must meet a certain criteria/coverage
```