/*
 * Copyright (C) 2012 University of Washington
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
package org.opendatakit.tables.views;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import org.opendatakit.application.CommonApplication;
import org.opendatakit.data.utilities.ColumnUtil;
import org.opendatakit.database.data.ColumnDefinition;
import org.opendatakit.database.service.DbHandle;
import org.opendatakit.database.service.UserDbInterface;
import org.opendatakit.exception.ServicesAvailabilityException;

import java.util.ArrayList;
import java.util.Map;

public class CellValueView {

  public static CellEditView getCellEditView(CommonApplication app, Context context, String appName,
      String tableId, ColumnDefinition cd, String value) throws ServicesAvailabilityException {

    UserDbInterface dbInterface = app.getDatabase();
    DbHandle db = null;
    try {
      db = dbInterface.openDatabase(appName);
      ArrayList<Map<String, Object>> displayChoices = ColumnUtil.get()
          .getDisplayChoicesList(dbInterface, appName, db, tableId, cd.getElementKey());
      if (displayChoices != null) {
        return new MultipleChoiceEditView(context, cd, displayChoices, value);
      } else {
        return new DefaultEditView(context, value);
      }
    } finally {
      if (db != null) {
        dbInterface.closeDatabase(appName, db);
      }
    }
  }

  public static abstract class CellEditView extends LinearLayout {

    private CellEditView(Context context) {
      super(context);
    }

    public abstract String getValue();
  }

  private static class MultipleChoiceEditView extends CellEditView {

    private final Spinner spinner;

    public MultipleChoiceEditView(Context context, ColumnDefinition cd,
        ArrayList<Map<String, Object>> displayChoices, String value) {
      super(context);
      int selection = -1;
      for (int i = 0; i < displayChoices.size(); i++) {
        if (displayChoices.get(i).equals(value)) {
          selection = i;
          break;
        }
      }
      ArrayAdapter<Map<String, Object>> adapter = new ArrayAdapter<Map<String, Object>>(context,
          android.R.layout.simple_spinner_item, displayChoices);
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      spinner = new Spinner(context);
      spinner.setAdapter(adapter);
      if (selection != -1) {
        spinner.setSelection(selection);
      }
      addView(spinner);
    }

    public String getValue() {
      return (String) spinner.getSelectedItem();
    }
  }

  private static class DefaultEditView extends CellEditView {

    private final EditText editText;

    public DefaultEditView(Context context, String value) {
      super(context);
      editText = new EditText(context);
      editText.setText(value);
      addView(editText);
    }

    public String getValue() {
      return editText.getText().toString();
    }
  }
}
