package com.example.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_table_two")
data class UserItem(@PrimaryKey @ColumnInfo(name ="id") var id: Long,
                 @ColumnInfo(name ="investing") var investing: Float
)


