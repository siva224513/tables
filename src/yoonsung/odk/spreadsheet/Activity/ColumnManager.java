package yoonsung.odk.spreadsheet.Activity;

import java.util.ArrayList;

import yoonsung.odk.spreadsheet.R;
import yoonsung.odk.spreadsheet.Database.Data;
import yoonsung.odk.spreadsheet.Database.TableProperty;
import yoonsung.odk.spreadsheet.Library.TouchListView;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ColumnManager extends ListActivity {
	
	// Menu IDs
	public static final int SET_AS_PRIME = 0;
	public static final int SET_AS_ORDER_BY = 1;
	public static final int REMOVE_THIS_COLUMN = 2;
	
	// Private fields
	private IconicAdapter adapter;
	private TableProperty tp;
	private Data data;
	private ArrayList<String> colOrder;
	private String currentCol;
	
	private void init() {
		this.tp = new TableProperty();
		this.data = new Data();
		this.colOrder = tp.getColOrderArrayList(); 
	}
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.col_manager);
		
		// Initialize
		init();
		
		// Add new column button
		createAddNewColumnButton();
				
		// Drag & Drop List
		createDragAndDropList();
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		tp.setColOrder(getNewColOrderFromList());
		colOrder = tp.getColOrderArrayList();
	}
	
	@Override 
	public void onResume() {
		super.onResume();
	
		// Get new column order
		colOrder = tp.getColOrderArrayList();
		
		// Create new Drag & Drop List
		createDragAndDropList();
	}
	
	// Retrieve current order of Drag & Drop List
	private ArrayList<String> getNewColOrderFromList() {
		ArrayList<String> newOrder = new ArrayList<String>();
		for (int i = 0; i < adapter.getCount(); i++) {
			newOrder.add(adapter.getItem(i));
		}
		return newOrder;
	}
	
	private void createAddNewColumnButton() {
		// Add column button
		RelativeLayout addCol = (RelativeLayout)findViewById(R.id.add_column_button);
		addCol.setClickable(true);
		
		// Add column button clicked
		addCol.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Ask a new column name
				alertForNewColumnName();
			}
		});
	}
	
	private void createDragAndDropList() {
		// Registration
		adapter = new IconicAdapter();
		setListAdapter(adapter);
		
		// Configuration
		TouchListView tlv = (TouchListView)getListView();
		tlv.setDropListener(onDrop);
		tlv.setRemoveListener(onRemove);
		
		// Item clicked in the Drag & Drop list
		tlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView adView, View view,
										int position, long id) {
				// Selected item in the list
				View v = adapter.getView(position, null, null);
				TextView tv = (TextView) v.findViewById(R.id.label);
				
				// Name of column from the selected item
				String name = tv.getText().toString();
				
				// Load Column Property Manger with this column name
				loadColumnPropertyManager(name);
			}
		});
		
		// Context menu for items in Drag & Drop list
		tlv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {	
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
									
				menu.add(0, SET_AS_PRIME, 0, "Set As Prime");
				menu.add(0, SET_AS_ORDER_BY, 0, "Set AS Order By");
				menu.add(0, REMOVE_THIS_COLUMN, 0, "Remove This Column");
			}
		});
		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.e("currentCol", currentCol);
		switch(item.getItemId()) {
	    case SET_AS_PRIME:
	    	tp.setPrime(currentCol); 
	    	return true;
	    case SET_AS_ORDER_BY:
	    	tp.setSortBy(currentCol); 
	    	return true;	
	    case REMOVE_THIS_COLUMN:
	    	// Drop the column from 'data' table
	    	data.dropColumn(currentCol);
	    	
	    	// New column order after the drop
	    	colOrder.remove(currentCol);
	    	tp.setColOrder(colOrder);
	    	
	    	// Update changes in other tables
	    	// To be done
	    	
	    	// Resume UI
	    	onResume();
 	    	return true;
	    }
	    return super.onContextItemSelected(item);
	}
	
	// Load Column Property Manager Activity
	private void loadColumnPropertyManager(String name) {
		Intent cpm = new Intent(this, PropertyManager.class);
		cpm.putExtra("colName", name);
		startActivity(cpm);
	}
	
	// Ask for a new column name
	private void alertForNewColumnName() {
		
		// Prompt an alert box
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Name of New Column");
	
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		// OK Action => Create new Column
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String colName = input.getText().toString();
					
				// if not, add a new column
				if (!data.isColumnExist(colName)) {
					
					// Create new column
					data.addNewColumn(colName);
					
					// Update Column Property
					colOrder.add(colName);
					tp.setColOrder(colOrder);
					
					// Load Column Property Manger
					loadColumnPropertyManager(colName);
				}
			}
		});

		// Cancel Action
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
			}
		});

		alert.show();
	}
		
	//								DO	NOT TOUCH BELOW								 //
	// ------------------------------------------------------------------------------//
	
	// Drag & Drop 
	private TouchListView.DropListener onDrop=new TouchListView.DropListener() {
		@Override
		public void drop(int from, int to) {
				String item = adapter.getItem(from);
				
				adapter.remove(item);
				adapter.insert(item, to);
		}
	};
	
	// Drag & Drop
	private TouchListView.RemoveListener onRemove=new TouchListView.RemoveListener() {
		@Override
		public void remove(int which) {
				adapter.remove(adapter.getItem(which));
		}
	};
	
	// Drag & Drop List Adapter
	class IconicAdapter extends ArrayAdapter<String> {
		IconicAdapter() {
			super(ColumnManager.this, R.layout.touchlistview_row2, colOrder);
		}

		public View getView(int position, View convertView,
												ViewGroup parent) {
			View row = convertView;
			
			if (row == null) {													
				LayoutInflater inflater=getLayoutInflater();
				
				row = inflater.inflate(R.layout.touchlistview_row2, parent, false);
			}
			
			// Current Position in the List
			final int currentPosition = position;
				
			// TextView in the Row selected
			TextView label = (TextView)row.findViewById(R.id.label);
			label.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					currentCol = colOrder.get(currentPosition);
					return false;
				}
			});
			
			label.setText(colOrder.get(position));
			
			return(row);
		}
	}
	
}
