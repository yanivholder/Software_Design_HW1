package il.ac.technion.cs.softwaredesign

import java.util.concurrent.CompletableFuture
import javax.inject.Inject

/**
 * This is the main class implementing SifriTaub, the new book borrowing system.
 *
 * Currently specified:
 * + Managing users
 * + Queueing book loans
 */
class SifriTaub @Inject constructor(private val userManager: UserManager, private val bookManager: BookManager) {

     /**
     * Authenticate a user identified by [username] and [password].
     *
     * If successful, this method returns a unique authentication
     * token which can be used by the user in subsequent calls to other methods. If the user previously logged in,
     * all previously valid tokens are *invalidated*
     *
     * This is a *read* operation.
     *
     * @throws IllegalArgumentException If the password does not match the username, or this user does not exist in the
     * system.
     * @return An authentication token to be used in future calls.
     */
    fun authenticate(username: String, password: String): CompletableFuture<String> {
        if(!userManager.isUsernameAndPassMatch(username = username, password = password)) {
            throw IllegalArgumentException();
        }
        return userManager.generateUserTokenAndInvalidateOld(username = username);
    }

    /**
     * Register a user to the system, allowing him to start using it.
     *
     * This is a *create* operation.
     *
     * @param username The username to register the user under (unique).
     * @param password The password associated with the registered user.
     * @param isFromCS Whether the student is from CS faculty or external.
     * @param age The (positive) age of the student.
     *
     * @throws IllegalArgumentException If a user with the same [username] already exists or the [age] is negative.
     */
    fun register(username: String, password: String, isFromCS: Boolean, age: Int): CompletableFuture<Unit> {
        if(userManager.isUsernameExists(username = username) || age < 0) {
            throw IllegalArgumentException();
        }
        userManager.register(
            username = username,
            password = password,
            isFromCS = isFromCS,
            age = age
        )
    }

    /**
     * Retrieve information about a user.
     *
     * **Note**: This method can be invoked by all users to query information about other users.
     *
     * This is a *read* operation.
     *
     * @param token A token of some authenticated user, asking for information about the user with username [username].
     * @throws PermissionException If [token] is invalid
     *
     * @return If the user exists, returns a [User] object containing information about the found user. Otherwise,
     * return `null`, indicating that there is no such user
     */
    fun userInformation(token: String, username: String): CompletableFuture<User?> {
        if(!userManager.isValidToken(token = token)) {
            throw PermissionException();
        }
        if(!userManager.isUsernameExists(username = username)) {
            return null;
        }
        return userManager.getUserInformation(username = username);
    }

    /**
     * Add a certain book to the library catalog, making it available for borrowing.
     *
     * This is a *create* operation
     *
     * @param token A token used to authenticate the requesting user
     * @param id An id supplied to this book. This must be unique across all books in the system.
     * @param description A human-readable description of the book with unlimited length.
     * @param copiesAmount number of copies that will be available in the library of this book.
     *
     * @throws PermissionException If the [token] is invalid.
     * @throws IllegalArgumentException If a book with the same [id] already exists.
     */
    fun addBookToCatalog(token: String, id: String, description: String, copiesAmount: Int): CompletableFuture<Unit> {
        if(!userManager.isValidToken(token = token)) {
            throw PermissionException();
        }
        if(bookManager.isIdExists(id = id)) {
            throw IllegalArgumentException();
        }
        return bookManager.addBook(id = id, description = description, copiesAmount = copiesAmount);
    }

    /**
     * Get the description for the book.
     *
     * This is a *read* operation
     *
     * @param token A token used to authenticate the requesting user
     *
     * @throws PermissionException If the [token] is invalid
     * @throws IllegalArgumentException If a book with the given [id] was not added to the library catalog by [addBookToCatalog].
     * @return A description string of the book with [id]
     */
    fun getBookDescription(token: String, id: String): CompletableFuture<String> {
        if(!userManager.isValidToken(token = token)) {
            throw PermissionException();
        }
        if (!bookManager.isIdExists(id = id)) {
            throw IllegalArgumentException();
        }
        return bookManager.getBookDescription(id = id);
    }

    /**
     * List the ids of the first [n] unique books (no id should appear twice).
     *
     * This is a *read* operation.
     *
     * @param token A token used to authenticate the requesting user
     * @throws PermissionException If the [token] is invalid.
     *
     * @return A list of ids, of size [n], sorted by time of addition (determined by a call to [addBookToCatalog]).
     * If there are less than [n] ids of books, this method returns a list of all book ids (sorted as defined above).
     */
    fun listBookIds(token: String, n: Int = 10): CompletableFuture<List<String>> {
        if(!userManager.isValidToken(token = token)) {
            throw PermissionException();
        }
        return bookManager.getFirstBooksByAddTime(numOfBooks = n);
    }

    /**
     * Submit a books loan to the queue with a given list of book IDs and their amount ([bookIds]).
     * A loan submission adheres to a FIFO queue semantic, with the following key points:
     * - A loan rests at the top of the queue, until all requested [bookIds] are available.
     * - If a loan is not at the top of the queue, it cannot be obtained (the books cannot be given) until
     *   all loans before it have been obtained.
     * - The loan queue is sorted by submission time. That is, the sooner a loan is submitted,
     *   the sooner it can be obtained.
     *
     * Important: you must call LoanService.loanBook(id) for each book that is loaned. When the books are returned
     * you must call LoanService.returnBook(id). LoanService should be injected with Guice.
     * This is a *create* operation.
     *
     * @throws PermissionException If the [token] is invalid.
     * @throws IllegalArgumentException if at least on of the requested books does not exist.
     *
     * @return An id for the loan request which can be used for SifriTaub methods such as
     * [loanRequestInformation], [cancelLoanRequest], [waitForBooks].
     * Implementation notes:
     * When a loan is returned, all books are returned and available for loaning.
     * - Even when a loan is not yet obtained, it should still show up in the system, so that [loanRequestInformation] calls
     * succeed and view this loan request as queued.
     */
    fun submitLoanRequest(token: String, loanName: String, bookIds: List<String>): CompletableFuture<String> = TODO("Implement me!")

    /**
     * Return information about a specific loan in the system. [id] is the loan id.
     *
     * This is a *read* operation.
     *
     * @throws PermissionException If the [token] is invalid.
     * @throws IllegalArgumentException If a loan with the supplied [id] does not exist in the system.
     */
    fun loanRequestInformation(token: String, id: String): CompletableFuture<LoanRequestInformation> = TODO("Implement me!")

    /**
     * Cancel currently queued loan.
     * The loan's status becomes [LoanStatus.CANCELED], and all the books are returned to the library and become available.
     *
     * **Note**: This method can only be invoked by the user which is the owner of the loan.
     *
     * This is a *delete* operation.
     *
     * @throws PermissionException If the [token] is invalid.
     * @throws IllegalArgumentException If the loan associated with [loanId] does not belong to the calling user,
     * does not exist, or it is not currently in a [LoanStatus.QUEUED] state.
     */
    fun cancelLoanRequest(token: String, loanId: String): CompletableFuture<Unit> = TODO("Implement me!")

    /**
     * @return a future that is finished only when the loan is obtained (according to the docs in [submitLoanRequest]).
     * If the loan is already obtained or canceled, the future finishes immediately without an error.
     * @throws PermissionException If the [token] is invalid.
     * @throws IllegalArgumentException If the loan associated with [loanId] does not belong to the calling user
     * or does not exist.
     */
    fun waitForBooks(token: String, loanId: String): CompletableFuture<ObtainedLoan> = TODO("Implement me!")
}