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
package org.opendatakit.tables.fragments;

import android.app.Fragment;
import android.content.Context;
import org.opendatakit.database.data.OrderedColumns;
import org.opendatakit.database.data.UserTable;
import org.opendatakit.tables.activities.TableDisplayActivity;

/**
 * The base class for any {@link Fragment} that displays a table.
 *
 * @author sudar.sam@gmail.com
 */
public abstract class AbsTableDisplayFragment extends AbsBaseFragment {

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (!(context instanceof TableDisplayActivity)) {
      throw new IllegalStateException(
          "fragment must be attached to a " + TableDisplayActivity.class.getSimpleName());
    }
  }

  /**
   * Get the tableId of the active table.
   *
   * @return
   */
  public String getTableId() {
    TableDisplayActivity activity = (TableDisplayActivity) getActivity();
    return activity.getTableId();
  }

  /**
   * Get the description of the table.
   *
   * @return
   */
  public OrderedColumns getColumnDefinitions() {
    TableDisplayActivity activity = (TableDisplayActivity) getActivity();
    return activity.getColumnDefinitions();
  }

  /**
   * Get the {@link UserTable} being held by the {@link TableDisplayActivity}.
   *
   * @return
   */
  public UserTable getUserTable() {
    TableDisplayActivity activity = (TableDisplayActivity) getActivity();
    UserTable result = activity.getUserTable();
    return result;
  }

  /**
   * Return the type of this fragment.
   */
  public abstract TableDisplayActivity.ViewFragmentType getFragmentType();

}
