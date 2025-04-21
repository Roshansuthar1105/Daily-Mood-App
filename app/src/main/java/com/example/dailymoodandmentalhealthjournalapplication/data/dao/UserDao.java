package com.example.dailymoodandmentalhealthjournalapplication.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dailymoodandmentalhealthjournalapplication.data.entity.User;

/**
 * Data Access Object for the User entity.
 */
@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE userId = :userId")
    LiveData<User> getUserById(String userId);

    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserByIdSync(String userId);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmailSync(String email);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    LiveData<User> getUserByEmail(String email);

    @Query("DELETE FROM users WHERE userId = :userId")
    void deleteUserById(String userId);

    @Query("UPDATE users SET lastLoginAt = :timestamp WHERE userId = :userId")
    void updateLastLogin(String userId, long timestamp);
}
