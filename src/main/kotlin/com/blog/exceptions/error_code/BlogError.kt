package com.blog.exceptions.error_code

import com.blog.exceptions.exceptions.ServiceError


enum class BlogError(override val errorCode: String, override val message: String) : ServiceError {
    BLOG600("BLOG-600", "Failed to fetch access token from github"),
    BLOG601("BLOG-601", "Failed to fetch user profile from github"),
    BLOG602("BLOG-602", "Failed to fetch user email from github"),
    BLOG603("BLOG-603", "Bad request"),
    BLOG604("BLOG-604", "Secret Not Found"),
    BLOG605("BLOG-605", "Post Not Found"),
    BLOG606("BLOG-606", "Unauthorized User")
}
