# SifriTaub • Assignment 1

## Authors
* Eilon Kornboim, 315677880
* Yaniv Holder, 207025297

### Implementation Summary
#### HW1
SifriTaub has two helper classes to manage its functionality:
UserMamager & Book Manager - 
* BookManager is in charge of registering and querying books
* UserMamager is in charge of registering and querying Users
***and*** also managing tokens (both generating, persisting and invalidating old ones) 
For that purpose, UserManager hold another helper class -> TokenStore which gives us a 
nice abstraction for token handling

All three ([User/Book/Token]Manager) use PersistentMap for persisting their data.
* PersistentMap is an abstraction for the SecureStorage, 
giving us unlimited sized values and also the option to retrieve the entire map.

#### HW2
- loanManager is used for keeping track of loan requests and available amount of each book
- Persisntent map now work in a parallal manner, reads all needed key entries needed and when all return it stitches the parts together (the 100Byte parts)
- when a user tries to wait for books or return them, the queue is checked and futures (belonging to loanReq) are completed if possible.

### Testing Summary
#### Of HW1
- UserManager, BookManager and TokenStore were all unit-tested using a fully functional mock of the PersistentMap
That is because it's very easy to supply said fake (a simple map).

- PersistentMap was unit-tested using a fully functional mock of the SecureStorage (also easy to implement)

- SifriTaub was unit-tested using a mock for both the UserManager and BookManager since their logic is not 
trivial and could not be mocked without repeating their code

- Integration tests we did check the whole system together.

####- Added tests for HW2:
- loanManager unit tests using a LoanService Fake
- added unit tests to sifriTaub for new API
- added integration test for the new functionalities



### Difficulties HW2
none

### Feedback
good assignment, but should be more clear in the PDF, there were some gray areas.
stuff like "cant use threads" should be said in advance and not piazza