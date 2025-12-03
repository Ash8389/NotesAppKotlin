package com.example.notes.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage

object SupabaseClientHolder {

    // TODO: replace with your Supabase values
    private const val SUPABASE_URL = "https://kgzrfghthjxydxvpkfvr.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtnenJmZ2h0aGp4eWR4dnBrZnZyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjM1ODQ4NzEsImV4cCI6MjA3OTE2MDg3MX0.qQRfIwd1giZUAQKKAs9Zo7GXVL7Ix8coSP7NdskMq-c"
}
object SupabaseClientHolder {

    // TODO: replace with your Supabase values
    private const val SUPABASE_URL = "https://kgzrfghthjxydxvpkfvr.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtnenJmZ2h0aGp4eWR4dnBrZnZyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjM1ODQ4NzEsImV4cCI6MjA3OTE2MDg3MX0.qQRfIwd1giZUAQKKAs9Zo7GXVL7Ix8coSP7NdskMq-c"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
    install(Postgrest) {
        serializer = io.github.jan.supabase.serializer.KotlinXSerializer(kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        })
    }
    install(Storage)
}
}
