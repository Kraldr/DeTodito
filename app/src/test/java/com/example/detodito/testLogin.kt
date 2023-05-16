package com.example.detodito

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.detodito.Login
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GetTokenResult
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class LoginTest {

    @Test
    fun testLogin() {
        // Mock dependencies
        val mockAuth = mock(FirebaseAuth::class.java)
        val mockUser = mock(FirebaseUser::class.java)
        val mockTask = mock(Task::class.java) as Task<AuthResult>
        val mockToken = "mock_token"
        val mockGetTokenResult = mock(GetTokenResult::class.java)
        `when`(mockGetTokenResult.token).thenReturn(mockToken)

        // Set up the class under test
        val login = Login()
        login.auth = mockAuth

        // Mock the sign-in process
        `when`(mockAuth.signInWithEmailAndPassword("admin@admin.com", "123456"))
            .thenReturn(mockTask)
        `when`(mockAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.getIdToken(true)).thenReturn(mock(Task::class.java) as Task<GetTokenResult>)
        `when`(mockUser.getIdToken(true).addOnSuccessListener(any() as (GetTokenResult) -> Unit)).thenAnswer {
            val listener = it.arguments[0] as (GetTokenResult) -> Unit
            listener.invoke(mockGetTokenResult)
            mock(Task::class.java) as Task<Void>
        }

        // Call the login function
        login.login("admin@admin.com", "123456", "Prueba")

        // Verify that the shared preferences were updated correctly
        verify(login.shared(mockToken, true, "Cliente"))

        // TODO: Add more assertions to test the behavior of the login function
    }
}

private fun <T> any(): T {
    return Mockito.any<T>()
}