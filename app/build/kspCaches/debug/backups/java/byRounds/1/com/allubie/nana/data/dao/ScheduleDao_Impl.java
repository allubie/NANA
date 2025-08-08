package com.allubie.nana.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.allubie.nana.data.database.DateTimeConverters;
import com.allubie.nana.data.entity.ScheduleEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;
import kotlinx.datetime.Instant;
import kotlinx.datetime.LocalDate;
import kotlinx.datetime.LocalTime;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ScheduleDao_Impl implements ScheduleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ScheduleEntity> __insertionAdapterOfScheduleEntity;

  private final DateTimeConverters __dateTimeConverters = new DateTimeConverters();

  private final EntityDeletionOrUpdateAdapter<ScheduleEntity> __deletionAdapterOfScheduleEntity;

  private final EntityDeletionOrUpdateAdapter<ScheduleEntity> __updateAdapterOfScheduleEntity;

  private final SharedSQLiteStatement __preparedStmtOfSetCompleted;

  private final SharedSQLiteStatement __preparedStmtOfSetPinned;

  public ScheduleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfScheduleEntity = new EntityInsertionAdapter<ScheduleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `schedules` (`id`,`title`,`description`,`startTime`,`endTime`,`date`,`location`,`isPinned`,`isCompleted`,`category`,`isRecurring`,`recurringPattern`,`reminderMinutes`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScheduleEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        final String _tmp = __dateTimeConverters.fromLocalTime(entity.getStartTime());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        final String _tmp_1 = __dateTimeConverters.fromLocalTime(entity.getEndTime());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_1);
        }
        final String _tmp_2 = __dateTimeConverters.fromLocalDate(entity.getDate());
        if (_tmp_2 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_2);
        }
        if (entity.getLocation() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getLocation());
        }
        final int _tmp_3 = entity.isPinned() ? 1 : 0;
        statement.bindLong(8, _tmp_3);
        final int _tmp_4 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(9, _tmp_4);
        statement.bindString(10, entity.getCategory());
        final int _tmp_5 = entity.isRecurring() ? 1 : 0;
        statement.bindLong(11, _tmp_5);
        if (entity.getRecurringPattern() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getRecurringPattern());
        }
        statement.bindLong(13, entity.getReminderMinutes());
        final String _tmp_6 = __dateTimeConverters.fromInstant(entity.getCreatedAt());
        if (_tmp_6 == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, _tmp_6);
        }
      }
    };
    this.__deletionAdapterOfScheduleEntity = new EntityDeletionOrUpdateAdapter<ScheduleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `schedules` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScheduleEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfScheduleEntity = new EntityDeletionOrUpdateAdapter<ScheduleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `schedules` SET `id` = ?,`title` = ?,`description` = ?,`startTime` = ?,`endTime` = ?,`date` = ?,`location` = ?,`isPinned` = ?,`isCompleted` = ?,`category` = ?,`isRecurring` = ?,`recurringPattern` = ?,`reminderMinutes` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScheduleEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        final String _tmp = __dateTimeConverters.fromLocalTime(entity.getStartTime());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        final String _tmp_1 = __dateTimeConverters.fromLocalTime(entity.getEndTime());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_1);
        }
        final String _tmp_2 = __dateTimeConverters.fromLocalDate(entity.getDate());
        if (_tmp_2 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_2);
        }
        if (entity.getLocation() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getLocation());
        }
        final int _tmp_3 = entity.isPinned() ? 1 : 0;
        statement.bindLong(8, _tmp_3);
        final int _tmp_4 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(9, _tmp_4);
        statement.bindString(10, entity.getCategory());
        final int _tmp_5 = entity.isRecurring() ? 1 : 0;
        statement.bindLong(11, _tmp_5);
        if (entity.getRecurringPattern() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getRecurringPattern());
        }
        statement.bindLong(13, entity.getReminderMinutes());
        final String _tmp_6 = __dateTimeConverters.fromInstant(entity.getCreatedAt());
        if (_tmp_6 == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, _tmp_6);
        }
        statement.bindString(15, entity.getId());
      }
    };
    this.__preparedStmtOfSetCompleted = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE schedules SET isCompleted = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetPinned = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE schedules SET isPinned = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertSchedule(final ScheduleEntity schedule,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfScheduleEntity.insert(schedule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSchedule(final ScheduleEntity schedule,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfScheduleEntity.handle(schedule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSchedule(final ScheduleEntity schedule,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfScheduleEntity.handle(schedule);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object setCompleted(final String id, final boolean completed,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetCompleted.acquire();
        int _argIndex = 1;
        final int _tmp = completed ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetCompleted.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setPinned(final String id, final boolean pinned,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetPinned.acquire();
        int _argIndex = 1;
        final int _tmp = pinned ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetPinned.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ScheduleEntity>> getAllSchedulesFlow() {
    final String _sql = "SELECT * FROM schedules ORDER BY date DESC, startTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"schedules"}, new Callable<List<ScheduleEntity>>() {
      @Override
      @NonNull
      public List<ScheduleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "isPinned");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "isRecurring");
          final int _cursorIndexOfRecurringPattern = CursorUtil.getColumnIndexOrThrow(_cursor, "recurringPattern");
          final int _cursorIndexOfReminderMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderMinutes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ScheduleEntity> _result = new ArrayList<ScheduleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScheduleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final LocalTime _tmpStartTime;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStartTime);
            }
            final LocalTime _tmp_1 = __dateTimeConverters.toLocalTime(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalTime', but it was NULL.");
            } else {
              _tmpStartTime = _tmp_1;
            }
            final LocalTime _tmpEndTime;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfEndTime);
            }
            final LocalTime _tmp_3 = __dateTimeConverters.toLocalTime(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalTime', but it was NULL.");
            } else {
              _tmpEndTime = _tmp_3;
            }
            final LocalDate _tmpDate;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfDate);
            }
            final LocalDate _tmp_5 = __dateTimeConverters.toLocalDate(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalDate', but it was NULL.");
            } else {
              _tmpDate = _tmp_5;
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final boolean _tmpIsPinned;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp_6 != 0;
            final boolean _tmpIsCompleted;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_7 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsRecurring;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp_8 != 0;
            final String _tmpRecurringPattern;
            if (_cursor.isNull(_cursorIndexOfRecurringPattern)) {
              _tmpRecurringPattern = null;
            } else {
              _tmpRecurringPattern = _cursor.getString(_cursorIndexOfRecurringPattern);
            }
            final int _tmpReminderMinutes;
            _tmpReminderMinutes = _cursor.getInt(_cursorIndexOfReminderMinutes);
            final Instant _tmpCreatedAt;
            final String _tmp_9;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_9 = null;
            } else {
              _tmp_9 = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final Instant _tmp_10 = __dateTimeConverters.toInstant(_tmp_9);
            if (_tmp_10 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.Instant', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_10;
            }
            _item = new ScheduleEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpDate,_tmpLocation,_tmpIsPinned,_tmpIsCompleted,_tmpCategory,_tmpIsRecurring,_tmpRecurringPattern,_tmpReminderMinutes,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getSchedulesForDate(final LocalDate date,
      final Continuation<? super List<ScheduleEntity>> $completion) {
    final String _sql = "SELECT * FROM schedules WHERE date = ? ORDER BY startTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __dateTimeConverters.fromLocalDate(date);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ScheduleEntity>>() {
      @Override
      @NonNull
      public List<ScheduleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "isPinned");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "isRecurring");
          final int _cursorIndexOfRecurringPattern = CursorUtil.getColumnIndexOrThrow(_cursor, "recurringPattern");
          final int _cursorIndexOfReminderMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderMinutes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ScheduleEntity> _result = new ArrayList<ScheduleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScheduleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final LocalTime _tmpStartTime;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfStartTime);
            }
            final LocalTime _tmp_2 = __dateTimeConverters.toLocalTime(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalTime', but it was NULL.");
            } else {
              _tmpStartTime = _tmp_2;
            }
            final LocalTime _tmpEndTime;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfEndTime);
            }
            final LocalTime _tmp_4 = __dateTimeConverters.toLocalTime(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalTime', but it was NULL.");
            } else {
              _tmpEndTime = _tmp_4;
            }
            final LocalDate _tmpDate;
            final String _tmp_5;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getString(_cursorIndexOfDate);
            }
            final LocalDate _tmp_6 = __dateTimeConverters.toLocalDate(_tmp_5);
            if (_tmp_6 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalDate', but it was NULL.");
            } else {
              _tmpDate = _tmp_6;
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final boolean _tmpIsPinned;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp_7 != 0;
            final boolean _tmpIsCompleted;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_8 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsRecurring;
            final int _tmp_9;
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp_9 != 0;
            final String _tmpRecurringPattern;
            if (_cursor.isNull(_cursorIndexOfRecurringPattern)) {
              _tmpRecurringPattern = null;
            } else {
              _tmpRecurringPattern = _cursor.getString(_cursorIndexOfRecurringPattern);
            }
            final int _tmpReminderMinutes;
            _tmpReminderMinutes = _cursor.getInt(_cursorIndexOfReminderMinutes);
            final Instant _tmpCreatedAt;
            final String _tmp_10;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_10 = null;
            } else {
              _tmp_10 = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final Instant _tmp_11 = __dateTimeConverters.toInstant(_tmp_10);
            if (_tmp_11 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.Instant', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_11;
            }
            _item = new ScheduleEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpDate,_tmpLocation,_tmpIsPinned,_tmpIsCompleted,_tmpCategory,_tmpIsRecurring,_tmpRecurringPattern,_tmpReminderMinutes,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ScheduleEntity>> getSchedulesForDateFlow(final LocalDate date) {
    final String _sql = "SELECT * FROM schedules WHERE date = ? ORDER BY startTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __dateTimeConverters.fromLocalDate(date);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"schedules"}, new Callable<List<ScheduleEntity>>() {
      @Override
      @NonNull
      public List<ScheduleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "isPinned");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "isRecurring");
          final int _cursorIndexOfRecurringPattern = CursorUtil.getColumnIndexOrThrow(_cursor, "recurringPattern");
          final int _cursorIndexOfReminderMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderMinutes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ScheduleEntity> _result = new ArrayList<ScheduleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScheduleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final LocalTime _tmpStartTime;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfStartTime);
            }
            final LocalTime _tmp_2 = __dateTimeConverters.toLocalTime(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalTime', but it was NULL.");
            } else {
              _tmpStartTime = _tmp_2;
            }
            final LocalTime _tmpEndTime;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfEndTime);
            }
            final LocalTime _tmp_4 = __dateTimeConverters.toLocalTime(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalTime', but it was NULL.");
            } else {
              _tmpEndTime = _tmp_4;
            }
            final LocalDate _tmpDate;
            final String _tmp_5;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getString(_cursorIndexOfDate);
            }
            final LocalDate _tmp_6 = __dateTimeConverters.toLocalDate(_tmp_5);
            if (_tmp_6 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalDate', but it was NULL.");
            } else {
              _tmpDate = _tmp_6;
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final boolean _tmpIsPinned;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp_7 != 0;
            final boolean _tmpIsCompleted;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_8 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsRecurring;
            final int _tmp_9;
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp_9 != 0;
            final String _tmpRecurringPattern;
            if (_cursor.isNull(_cursorIndexOfRecurringPattern)) {
              _tmpRecurringPattern = null;
            } else {
              _tmpRecurringPattern = _cursor.getString(_cursorIndexOfRecurringPattern);
            }
            final int _tmpReminderMinutes;
            _tmpReminderMinutes = _cursor.getInt(_cursorIndexOfReminderMinutes);
            final Instant _tmpCreatedAt;
            final String _tmp_10;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_10 = null;
            } else {
              _tmp_10 = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final Instant _tmp_11 = __dateTimeConverters.toInstant(_tmp_10);
            if (_tmp_11 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.Instant', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_11;
            }
            _item = new ScheduleEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpDate,_tmpLocation,_tmpIsPinned,_tmpIsCompleted,_tmpCategory,_tmpIsRecurring,_tmpRecurringPattern,_tmpReminderMinutes,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ScheduleEntity>> getSchedulesInRangeFlow(final LocalDate startDate,
      final LocalDate endDate) {
    final String _sql = "SELECT * FROM schedules WHERE date >= ? AND date <= ? ORDER BY date ASC, startTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = __dateTimeConverters.fromLocalDate(startDate);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    final String _tmp_1 = __dateTimeConverters.fromLocalDate(endDate);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"schedules"}, new Callable<List<ScheduleEntity>>() {
      @Override
      @NonNull
      public List<ScheduleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "isPinned");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "isRecurring");
          final int _cursorIndexOfRecurringPattern = CursorUtil.getColumnIndexOrThrow(_cursor, "recurringPattern");
          final int _cursorIndexOfReminderMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderMinutes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ScheduleEntity> _result = new ArrayList<ScheduleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScheduleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final LocalTime _tmpStartTime;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfStartTime);
            }
            final LocalTime _tmp_3 = __dateTimeConverters.toLocalTime(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalTime', but it was NULL.");
            } else {
              _tmpStartTime = _tmp_3;
            }
            final LocalTime _tmpEndTime;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfEndTime);
            }
            final LocalTime _tmp_5 = __dateTimeConverters.toLocalTime(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalTime', but it was NULL.");
            } else {
              _tmpEndTime = _tmp_5;
            }
            final LocalDate _tmpDate;
            final String _tmp_6;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getString(_cursorIndexOfDate);
            }
            final LocalDate _tmp_7 = __dateTimeConverters.toLocalDate(_tmp_6);
            if (_tmp_7 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalDate', but it was NULL.");
            } else {
              _tmpDate = _tmp_7;
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final boolean _tmpIsPinned;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp_8 != 0;
            final boolean _tmpIsCompleted;
            final int _tmp_9;
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_9 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsRecurring;
            final int _tmp_10;
            _tmp_10 = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp_10 != 0;
            final String _tmpRecurringPattern;
            if (_cursor.isNull(_cursorIndexOfRecurringPattern)) {
              _tmpRecurringPattern = null;
            } else {
              _tmpRecurringPattern = _cursor.getString(_cursorIndexOfRecurringPattern);
            }
            final int _tmpReminderMinutes;
            _tmpReminderMinutes = _cursor.getInt(_cursorIndexOfReminderMinutes);
            final Instant _tmpCreatedAt;
            final String _tmp_11;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_11 = null;
            } else {
              _tmp_11 = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final Instant _tmp_12 = __dateTimeConverters.toInstant(_tmp_11);
            if (_tmp_12 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.Instant', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_12;
            }
            _item = new ScheduleEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpDate,_tmpLocation,_tmpIsPinned,_tmpIsCompleted,_tmpCategory,_tmpIsRecurring,_tmpRecurringPattern,_tmpReminderMinutes,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getScheduleById(final String id,
      final Continuation<? super ScheduleEntity> $completion) {
    final String _sql = "SELECT * FROM schedules WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ScheduleEntity>() {
      @Override
      @Nullable
      public ScheduleEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "isPinned");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "isRecurring");
          final int _cursorIndexOfRecurringPattern = CursorUtil.getColumnIndexOrThrow(_cursor, "recurringPattern");
          final int _cursorIndexOfReminderMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderMinutes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ScheduleEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final LocalTime _tmpStartTime;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStartTime);
            }
            final LocalTime _tmp_1 = __dateTimeConverters.toLocalTime(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalTime', but it was NULL.");
            } else {
              _tmpStartTime = _tmp_1;
            }
            final LocalTime _tmpEndTime;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfEndTime);
            }
            final LocalTime _tmp_3 = __dateTimeConverters.toLocalTime(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalTime', but it was NULL.");
            } else {
              _tmpEndTime = _tmp_3;
            }
            final LocalDate _tmpDate;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfDate);
            }
            final LocalDate _tmp_5 = __dateTimeConverters.toLocalDate(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalDate', but it was NULL.");
            } else {
              _tmpDate = _tmp_5;
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final boolean _tmpIsPinned;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp_6 != 0;
            final boolean _tmpIsCompleted;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_7 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsRecurring;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp_8 != 0;
            final String _tmpRecurringPattern;
            if (_cursor.isNull(_cursorIndexOfRecurringPattern)) {
              _tmpRecurringPattern = null;
            } else {
              _tmpRecurringPattern = _cursor.getString(_cursorIndexOfRecurringPattern);
            }
            final int _tmpReminderMinutes;
            _tmpReminderMinutes = _cursor.getInt(_cursorIndexOfReminderMinutes);
            final Instant _tmpCreatedAt;
            final String _tmp_9;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_9 = null;
            } else {
              _tmp_9 = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final Instant _tmp_10 = __dateTimeConverters.toInstant(_tmp_9);
            if (_tmp_10 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.Instant', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_10;
            }
            _result = new ScheduleEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpDate,_tmpLocation,_tmpIsPinned,_tmpIsCompleted,_tmpCategory,_tmpIsRecurring,_tmpRecurringPattern,_tmpReminderMinutes,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ScheduleEntity>> searchSchedulesFlow(final String searchQuery) {
    final String _sql = "SELECT * FROM schedules WHERE title LIKE '%' || ? || '%' OR description LIKE '%' || ? || '%' ORDER BY date DESC, startTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, searchQuery);
    _argIndex = 2;
    _statement.bindString(_argIndex, searchQuery);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"schedules"}, new Callable<List<ScheduleEntity>>() {
      @Override
      @NonNull
      public List<ScheduleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "isPinned");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "isRecurring");
          final int _cursorIndexOfRecurringPattern = CursorUtil.getColumnIndexOrThrow(_cursor, "recurringPattern");
          final int _cursorIndexOfReminderMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderMinutes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ScheduleEntity> _result = new ArrayList<ScheduleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScheduleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final LocalTime _tmpStartTime;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStartTime);
            }
            final LocalTime _tmp_1 = __dateTimeConverters.toLocalTime(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalTime', but it was NULL.");
            } else {
              _tmpStartTime = _tmp_1;
            }
            final LocalTime _tmpEndTime;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfEndTime);
            }
            final LocalTime _tmp_3 = __dateTimeConverters.toLocalTime(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalTime', but it was NULL.");
            } else {
              _tmpEndTime = _tmp_3;
            }
            final LocalDate _tmpDate;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfDate);
            }
            final LocalDate _tmp_5 = __dateTimeConverters.toLocalDate(_tmp_4);
            if (_tmp_5 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalDate', but it was NULL.");
            } else {
              _tmpDate = _tmp_5;
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final boolean _tmpIsPinned;
            final int _tmp_6;
            _tmp_6 = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp_6 != 0;
            final boolean _tmpIsCompleted;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_7 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsRecurring;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp_8 != 0;
            final String _tmpRecurringPattern;
            if (_cursor.isNull(_cursorIndexOfRecurringPattern)) {
              _tmpRecurringPattern = null;
            } else {
              _tmpRecurringPattern = _cursor.getString(_cursorIndexOfRecurringPattern);
            }
            final int _tmpReminderMinutes;
            _tmpReminderMinutes = _cursor.getInt(_cursorIndexOfReminderMinutes);
            final Instant _tmpCreatedAt;
            final String _tmp_9;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_9 = null;
            } else {
              _tmp_9 = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final Instant _tmp_10 = __dateTimeConverters.toInstant(_tmp_9);
            if (_tmp_10 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.Instant', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_10;
            }
            _item = new ScheduleEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpDate,_tmpLocation,_tmpIsPinned,_tmpIsCompleted,_tmpCategory,_tmpIsRecurring,_tmpRecurringPattern,_tmpReminderMinutes,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getNextIncompleteScheduleForDate(final LocalDate date,
      final Continuation<? super ScheduleEntity> $completion) {
    final String _sql = "SELECT * FROM schedules WHERE date = ? AND isCompleted = 0 ORDER BY startTime ASC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __dateTimeConverters.fromLocalDate(date);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ScheduleEntity>() {
      @Override
      @Nullable
      public ScheduleEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "isPinned");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsRecurring = CursorUtil.getColumnIndexOrThrow(_cursor, "isRecurring");
          final int _cursorIndexOfRecurringPattern = CursorUtil.getColumnIndexOrThrow(_cursor, "recurringPattern");
          final int _cursorIndexOfReminderMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderMinutes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ScheduleEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final LocalTime _tmpStartTime;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStartTime)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfStartTime);
            }
            final LocalTime _tmp_2 = __dateTimeConverters.toLocalTime(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalTime', but it was NULL.");
            } else {
              _tmpStartTime = _tmp_2;
            }
            final LocalTime _tmpEndTime;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfEndTime);
            }
            final LocalTime _tmp_4 = __dateTimeConverters.toLocalTime(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalTime', but it was NULL.");
            } else {
              _tmpEndTime = _tmp_4;
            }
            final LocalDate _tmpDate;
            final String _tmp_5;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getString(_cursorIndexOfDate);
            }
            final LocalDate _tmp_6 = __dateTimeConverters.toLocalDate(_tmp_5);
            if (_tmp_6 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalDate', but it was NULL.");
            } else {
              _tmpDate = _tmp_6;
            }
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            final boolean _tmpIsPinned;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp_7 != 0;
            final boolean _tmpIsCompleted;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_8 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsRecurring;
            final int _tmp_9;
            _tmp_9 = _cursor.getInt(_cursorIndexOfIsRecurring);
            _tmpIsRecurring = _tmp_9 != 0;
            final String _tmpRecurringPattern;
            if (_cursor.isNull(_cursorIndexOfRecurringPattern)) {
              _tmpRecurringPattern = null;
            } else {
              _tmpRecurringPattern = _cursor.getString(_cursorIndexOfRecurringPattern);
            }
            final int _tmpReminderMinutes;
            _tmpReminderMinutes = _cursor.getInt(_cursorIndexOfReminderMinutes);
            final Instant _tmpCreatedAt;
            final String _tmp_10;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_10 = null;
            } else {
              _tmp_10 = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final Instant _tmp_11 = __dateTimeConverters.toInstant(_tmp_10);
            if (_tmp_11 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.Instant', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_11;
            }
            _result = new ScheduleEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpDate,_tmpLocation,_tmpIsPinned,_tmpIsCompleted,_tmpCategory,_tmpIsRecurring,_tmpRecurringPattern,_tmpReminderMinutes,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
