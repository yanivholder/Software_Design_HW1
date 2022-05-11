package il.ac.technion.cs.softwaredesign

import library.Library

class UserManager(lib: Library) {

    companion object {
        fun isUsernameExists(username: String): Boolean {
            return false;
        }
        fun isUsernameAndPassMatch(username: String, password: String): Boolean {
            return false;
        }

        fun generateUserToken(username: String): String {
            return "";
        }

        fun isValidToken(token: String): Boolean {
            return false;
        }

        fun register(username: String, password: String, isFromCS: Boolean, age: Int): Unit {

        }

        fun getUserInformation(username: String): User {
            return User(age = 0, isFromCS = false, username = username);
        }

    }
}