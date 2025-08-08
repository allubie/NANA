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
import com.allubie.nana.data.entity.RoutineCompletionEntity;
import com.allubie.nana.data.entity.RoutineEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Integer;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RoutineDao_Impl implements RoutineDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RoutineEntity> __insertionAdapterOfRoutineEntity;

  private final DateTimeConverters __dateTimeConverters = new DateTimeConverters();

  private final EntityInsertionAdapter<RoutineCompletionEntity> __insertionAdapterOfRoutineCompletionEntity;

  private final EntityDeletionOrUpdateAdapter<RoutineEntity> __deletionAdapterOfRoutineEntity;

  private final EntityDeletionOrUpdateAdapter<RoutineCompletionEntity> __deletionAdapterOfRoutineCompletionEntity;

  private final EntityDeletionOrUpdateAdapter<RoutineEntity> __updateAdapterOfRoutineEntity;

  private final SharedSQLiteStatement __preparedStmtOfSetPinned;

  private final SharedSQLiteStatement __preparedStmtOfRemoveCompletionForDate;

  public RoutineDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRoutineEntity = new EntityInsertionAdapter<RoutineEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `routines` (`id`,`title`,`description`,`frequency`,`isPinned`,`reminderTime`,`createdAt`,`isActive`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoutineEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getFrequency());
        final int _tmp = entity.isPinned() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getReminderTime() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getReminderTime());
        }
        final String _tmp_1 = __dateTimeConverters.fromInstant(entity.getCreatedAt());
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_1);
        }
        final int _tmp_2 = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
      }
    };
    this.__insertionAdapterOfRoutineCompletionEntity = new EntityInsertionAdapter<RoutineCompletionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `routine_completions` (`id`,`routineId`,`completionDate`,`completedAt`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoutineCompletionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRoutineId());
        final String _tmp = __dateTimeConverters.fromLocalDate(entity.getCompletionDate());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        final String _tmp_1 = __dateTimeConverters.fromInstant(entity.getCompletedAt());
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp_1);
        }
      }
    };
    this.__deletionAdapterOfRoutineEntity = new EntityDeletionOrUpdateAdapter<RoutineEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `routines` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoutineEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__deletionAdapterOfRoutineCompletionEntity = new EntityDeletionOrUpdateAdapter<RoutineCompletionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `routine_completions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoutineCompletionEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfRoutineEntity = new EntityDeletionOrUpdateAdapter<RoutineEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `routines` SET `id` = ?,`title` = ?,`description` = ?,`frequency` = ?,`isPinned` = ?,`reminderTime` = ?,`createdAt` = ?,`isActive` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RoutineEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getFrequency());
        final int _tmp = entity.isPinned() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getReminderTime() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getReminderTime());
        }
        final String _tmp_1 = __dateTimeConverters.fromInstant(entity.getCreatedAt());
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_1);
        }
        final int _tmp_2 = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp_2);
        statement.bindString(9, entity.getId());
      }
    };
    this.__preparedStmtOfSetPinned = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE routines SET isPinned = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRemoveCompletionForDate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM routine_completions WHERE routineId = ? AND completionDate = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertRoutine(final RoutineEntity routine,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRoutineEntity.insert(routine);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCompletion(final RoutineCompletionEntity completion,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRoutineCompletionEntity.insert(completion);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRoutine(final RoutineEntity routine,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRoutineEntity.handle(routine);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCompletion(final RoutineCompletionEntity completion,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRoutineCompletionEntity.handle(completion);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRoutine(final RoutineEntity routine,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRoutineEntity.handle(routine);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
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
  public Object removeCompletionForDate(final String routineId, final LocalDate date,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveCompletionForDate.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, routineId);
        _argIndex = 2;
        final String _tmp = __dateTimeConverters.fromLocalDate(date);
        if (_tmp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, _tmp);
        }
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
          __preparedStmtOfRemoveCompletionForDate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RoutineEntity>> getActiveRoutinesFlow() {
    final String _sql = "SELECT * FROM routines WHERE isActive = 1 ORDER BY isPinned DESC, createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"routines"}, new Callable<List<RoutineEntity>>() {
      @Override
      @NonNull
      public List<RoutineEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "frequency");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "isPinned");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<RoutineEntity> _result = new ArrayList<RoutineEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoutineEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpFrequency;
            _tmpFrequency = _cursor.getString(_cursorIndexOfFrequency);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final String _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getString(_cursorIndexOfReminderTime);
            }
            final Instant _tmpCreatedAt;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final Instant _tmp_2 = __dateTimeConverters.toInstant(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.Instant', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_2;
            }
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            _item = new RoutineEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpFrequency,_tmpIsPinned,_tmpReminderTime,_tmpCreatedAt,_tmpIsActive);
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
  public Object getRoutineById(final String id,
      final Continuation<? super RoutineEntity> $completion) {
    final String _sql = "SELECT * FROM routines WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RoutineEntity>() {
      @Override
      @Nullable
      public RoutineEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "frequency");
          final int _cursorIndexOfIsPinned = CursorUtil.getColumnIndexOrThrow(_cursor, "isPinned");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final RoutineEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpFrequency;
            _tmpFrequency = _cursor.getString(_cursorIndexOfFrequency);
            final boolean _tmpIsPinned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsPinned);
            _tmpIsPinned = _tmp != 0;
            final String _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getString(_cursorIndexOfReminderTime);
            }
            final Instant _tmpCreatedAt;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final Instant _tmp_2 = __dateTimeConverters.toInstant(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.Instant', but it was NULL.");
            } else {
              _tmpCreatedAt = _tmp_2;
            }
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            _result = new RoutineEntity(_tmpId,_tmpTitle,_tmpDescription,_tmpFrequency,_tmpIsPinned,_tmpReminderTime,_tmpCreatedAt,_tmpIsActive);
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
  public Object getCompletionForDate(final String routineId, final LocalDate date,
      final Continuation<? super RoutineCompletionEntity> $completion) {
    final String _sql = "SELECT * FROM routine_completions WHERE routineId = ? AND completionDate = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, routineId);
    _argIndex = 2;
    final String _tmp = __dateTimeConverters.fromLocalDate(date);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RoutineCompletionEntity>() {
      @Override
      @Nullable
      public RoutineCompletionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoutineId = CursorUtil.getColumnIndexOrThrow(_cursor, "routineId");
          final int _cursorIndexOfCompletionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "completionDate");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final RoutineCompletionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoutineId;
            _tmpRoutineId = _cursor.getString(_cursorIndexOfRoutineId);
            final LocalDate _tmpCompletionDate;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfCompletionDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfCompletionDate);
            }
            final LocalDate _tmp_2 = __dateTimeConverters.toLocalDate(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalDate', but it was NULL.");
            } else {
              _tmpCompletionDate = _tmp_2;
            }
            final Instant _tmpCompletedAt;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfCompletedAt);
            }
            final Instant _tmp_4 = __dateTimeConverters.toInstant(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.Instant', but it was NULL.");
            } else {
              _tmpCompletedAt = _tmp_4;
            }
            _result = new RoutineCompletionEntity(_tmpId,_tmpRoutineId,_tmpCompletionDate,_tmpCompletedAt);
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
  public Object getCompletionsForRoutine(final String routineId,
      final Continuation<? super List<RoutineCompletionEntity>> $completion) {
    final String _sql = "SELECT * FROM routine_completions WHERE routineId = ? ORDER BY completionDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, routineId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoutineCompletionEntity>>() {
      @Override
      @NonNull
      public List<RoutineCompletionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoutineId = CursorUtil.getColumnIndexOrThrow(_cursor, "routineId");
          final int _cursorIndexOfCompletionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "completionDate");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<RoutineCompletionEntity> _result = new ArrayList<RoutineCompletionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoutineCompletionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoutineId;
            _tmpRoutineId = _cursor.getString(_cursorIndexOfRoutineId);
            final LocalDate _tmpCompletionDate;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfCompletionDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfCompletionDate);
            }
            final LocalDate _tmp_1 = __dateTimeConverters.toLocalDate(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalDate', but it was NULL.");
            } else {
              _tmpCompletionDate = _tmp_1;
            }
            final Instant _tmpCompletedAt;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfCompletedAt);
            }
            final Instant _tmp_3 = __dateTimeConverters.toInstant(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.Instant', but it was NULL.");
            } else {
              _tmpCompletedAt = _tmp_3;
            }
            _item = new RoutineCompletionEntity(_tmpId,_tmpRoutineId,_tmpCompletionDate,_tmpCompletedAt);
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
  public Object getCompletionsForDate(final LocalDate date,
      final Continuation<? super List<RoutineCompletionEntity>> $completion) {
    final String _sql = "SELECT * FROM routine_completions WHERE completionDate = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __dateTimeConverters.fromLocalDate(date);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RoutineCompletionEntity>>() {
      @Override
      @NonNull
      public List<RoutineCompletionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoutineId = CursorUtil.getColumnIndexOrThrow(_cursor, "routineId");
          final int _cursorIndexOfCompletionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "completionDate");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<RoutineCompletionEntity> _result = new ArrayList<RoutineCompletionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RoutineCompletionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRoutineId;
            _tmpRoutineId = _cursor.getString(_cursorIndexOfRoutineId);
            final LocalDate _tmpCompletionDate;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfCompletionDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfCompletionDate);
            }
            final LocalDate _tmp_2 = __dateTimeConverters.toLocalDate(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.LocalDate', but it was NULL.");
            } else {
              _tmpCompletionDate = _tmp_2;
            }
            final Instant _tmpCompletedAt;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfCompletedAt);
            }
            final Instant _tmp_4 = __dateTimeConverters.toInstant(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'kotlinx.datetime.Instant', but it was NULL.");
            } else {
              _tmpCompletedAt = _tmp_4;
            }
            _item = new RoutineCompletionEntity(_tmpId,_tmpRoutineId,_tmpCompletionDate,_tmpCompletedAt);
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
  public Object getStreakCount(final String routineId, final LocalDate startDate,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM routine_completions WHERE routineId = ? AND completionDate >= ? ORDER BY completionDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, routineId);
    _argIndex = 2;
    final String _tmp = __dateTimeConverters.fromLocalDate(startDate);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(0);
            _result = _tmp_1;
          } else {
            _result = 0;
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
