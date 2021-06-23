package com.example.simpletodo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

//import android.os.FileUtils;
/*-----------------------------*/
/*-----------------------------*/

public class MainActivity extends AppCompatActivity {

    List<String> items;

    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    //Elements for update an item
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    EditText etEdit;
    Button buttonDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.textItem);
        rvItems = findViewById(R.id.rvItems);

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
                //Delete the item from the model
                items.remove(position);
                //Notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        //Edit item when is clicked
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                editItem(position);
            }
        };
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etItem.getText().toString();
                //Add item to the model
                if(todoItem.equals("")){
                    //If field is empty, we create a message but we not save nothing
                    Toast.makeText(getApplicationContext(), "You need to write something", Toast.LENGTH_SHORT).show();
                }
                else {
                    items.add(todoItem);
                    //Notify adapter that an item is inserted
                    itemsAdapter.notifyItemInserted(items.size()-1);
                    etItem.setText("");
                    Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                    saveItems();
                }
            }
        });
    }
    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    //This function will load items by reading every line of the data file
    private void loadItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e){
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }

    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }

    //Function for update some item
    public void editItem(int posicion){

        dialogBuilder = new AlertDialog.Builder(this);
        final View editTextView = getLayoutInflater().inflate(R.layout.edit, null);

        buttonDone = editTextView.findViewById(R.id.btnEdit);

        etEdit = editTextView.findViewById(R.id.textEdited);
        etEdit.setText(items.get(posicion));
        dialogBuilder.setView(editTextView);
        dialog = dialogBuilder.create();
        dialog.show();

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.set(posicion, etEdit.getText().toString());
                //Notify adapter that an item was changed
                itemsAdapter.notifyDataSetChanged();
                saveItems();
                Toast.makeText(getApplicationContext(), "Item was updated", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });



    }

}