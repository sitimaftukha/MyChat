package com.siti.groupchatsiti.model

class SignUpModel(name: String, email: String, password: String) {

    private val name: String = name
    private val password: String = password
    private val email: String = email

    fun getName(): String? {
        return name
    }

    fun getPassword(): String? {
        return password
    }

    fun getEmail(): String? {
        return email
    }


    override fun toString(): String {
        return "SignUpModel(name=$name, password=$password, email=$email)"
    }
}