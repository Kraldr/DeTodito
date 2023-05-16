package com.example.detodito.data

data class dataRegis(
    val disabled: Boolean,
    val email: String,
    val emailVerified: Boolean,
    val metadata: Metadata,
    val providerData: List<ProviderData>,
    val tokensValidAfterTime: String,
    val uid: String
)