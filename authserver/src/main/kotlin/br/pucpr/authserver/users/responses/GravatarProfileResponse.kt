package br.pucpr.authserver.users.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class GravatarProfileResponse (
    @JsonProperty("avatar_url")
    val avatarUrl: String,
    @JsonProperty("display_name")
    val displayName: String
)