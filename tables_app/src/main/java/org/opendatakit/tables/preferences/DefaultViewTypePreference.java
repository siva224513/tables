/*
 * Copyright (C) 2014 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.opendatakit.tables.preferences;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import org.opendatakit.data.TableViewType;
import org.opendatakit.data.utilities.TableUtil;
import org.opendatakit.database.data.OrderedColumns;
import org.opendatakit.database.service.DbHandle;
import org.opendatakit.database.service.UserDbInterface;
import org.opendatakit.exception.ServicesAvailabilityException;
import org.opendatakit.tables.R;
import org.opendatakit.tables.activities.AbsTableActivity;
import org.opendatakit.tables.application.Tables;
import org.opendatakit.tables.data.PossibleTableViewTypes;
import org.opendatakit.tables.views.components.TableViewTypeAdapter;

import java.util.Arrays;

public class DefaultViewTypePreference extends ListPreference {

  /**
   * The view types allowed for the table this preference will display.
   */
  //private TableProperties mTableProperties;
  private PossibleTableViewTypes mPossibleViewTypes;
  private Context mContext;
  private final String mAppName;
  private CharSequence[] mEntryValues;

  public DefaultViewTypePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    AbsTableActivity activity = (AbsTableActivity) context;
    this.mContext = context;
    this.mAppName = activity.getAppName();
  }

  public void setFields(String tableId, OrderedColumns orderedDefns)
      throws ServicesAvailabilityException {

    TableViewType defaultViewType = null;
    this.mEntryValues = this.mContext.getResources().getTextArray(R.array.table_view_types_values);

    UserDbInterface dbInterface = Tables.getInstance().getDatabase();
    DbHandle db = null;
    try {
      db = dbInterface.openDatabase(mAppName);
      this.mPossibleViewTypes = new PossibleTableViewTypes(mAppName, db, tableId, orderedDefns);
      // Let's set the currently selected one.
      defaultViewType = TableUtil.get().getDefaultViewType(dbInterface, mAppName, db, tableId);
    } finally {
      if (db != null) {
        dbInterface.closeDatabase(mAppName, db);
      }
    }

    if (defaultViewType == null || !mPossibleViewTypes.getAllPossibleViewTypes()
        .contains(defaultViewType)) {
      // default to spreadsheet.
      defaultViewType = TableViewType.SPREADSHEET;
      this.setValueIndex(0);
    } else {
      int index = Arrays.asList(this.mEntryValues).indexOf(defaultViewType.name());
      if (index < 0) {
        index = 0;
      }
      this.setValueIndex(index);
    }
  }

  @Override
  protected void onPrepareDialogBuilder(Builder builder) {
    // We want to enable/disable the correct list.
    ListAdapter adapter = new TableViewTypeAdapter(this.mContext, this.mAppName,
        android.R.layout.select_dialog_singlechoice, this.getEntries(), this.getEntryValues(),
        this.mPossibleViewTypes);
    builder.setAdapter(adapter, this);
    super.onPrepareDialogBuilder(builder);
  }

}
