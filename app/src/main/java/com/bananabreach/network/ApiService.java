package com.bananabreach.network;

import com.bananabreach.data.models.Transaction;
import com.bananabreach.data.models.TransactionRequest;
import com.bananabreach.data.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Intentional: credentials sent as query params, not request body —
    // see docs/VULNERABILITIES.md (Network: sensitive data in URL)
    @POST("api/login")
    Call<User> loginUser(
            @Query("email") String email,
            @Query("password") String password
    );

    @POST("api/register")
    Call<User> registerUser(
            @Body User user
    );

    @GET("api/user/{userId}")
    Call<User> getUser(
            @Path("userId") String userId
    );

    @GET("api/user/profile")
    Call<User> getProfile(
            @Query("token") String token // Intentional: token in URL
    );

    @GET("api/transactions")
    Call<List<Transaction>> getTransactions(
            @Query("user_id") String userId,
            @Query("limit") int limit
    );

    @POST("api/transaction")
    Call<Transaction> createTransaction(
            @Body TransactionRequest request
    );

    @GET("api/transactions/export")
    Call<String> exportTransactions(
            @Query("user_id") String userId
    );

    // Intentional: admin endpoints gated by a simple token param, not server-side RBAC
    @GET("api/admin/users")
    Call<List<User>> getUsers(
            @Query("admin_token") String token
    );

    @GET("api/admin/logs")
    Call<String> getSystemLogs(
            @Query("admin_token") String token
    );

    @GET("api/debug/stats")
    Call<String> getDebugStats(
            @Query("key") String apiKey
    );

    // Intentional: cleartext HTTP endpoint, see docs/VULNERABILITIES.md
    @POST("http://dev.bananabreach.com/api/test")
    Call<String> testEndpoint(
            @Body String data
    );
}
