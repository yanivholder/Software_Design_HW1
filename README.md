# SifriTaub • Assignment 1

## Authors
* Eilon Kornboim, 315677880
* Yaniv Holder, 207025297

## Notes

### Implementation Summary
SifriTaub has two helper classes to manage its functionality:
UserMamager & Book Manager - 
* BookManager is in charge of registering and querying books
* UserMamager is in charge of registering and querying Users
***and*** also managing tokens (both generating, persisting and invalidating old ones) 
For that purpose, UserManager hold another helper class -> TokenManager which gives us a 
nice abstraction for token handling

All three ([User/Book/Token]Manager) use PersistentMap for persisting their data.
* PersistentMap is an abstraction for the SecureStorage, 
giving us unlimited sized values and also the option to retrieve the entire map.


### Testing Summary
UserManager, BookManager and TokenManager were all unit-tested using a fully functional mock of the PersistentMap
That is because it's very easy to supply said fake (a simple map).

PersistentMap was unit-tested using a fully functional mock of the SecureStorage (also easy to implement)

SifriTaub was unit-tested using a mock for both the UserManager and BookManager since their logic is not 
trivial and could not be mocked without repeating their code

Integration tests we did check the whole system together.



### Difficulties
Please list any technological difficulties you had while working on this assignment, especially
with the tools used: Kotlin, JUnit, MockK, Guice, and Gradle.
Guice was the most problematic, and we think more example should be given for future exercises.
Besides Guice, all the above worked fine and easy to manage

### Feedback
Cool assignment, made us use what was taught and understand where it comes to use.
Guice took a lot of time to operate and that too bad because the concept was clear 
all along, and it didn't teach much to play around with configurations till it works.