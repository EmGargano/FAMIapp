package it.emgargano.famiapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import famiapp.R;

public class KitOpeningActivity extends AppCompatActivity {

    // ArrayList type String of paired devices
    ArrayList<String> arrayListpaired;
    // ArrayAdapter returns a view for each object in a collection of data objects
    ArrayAdapter<String> pairedAdapter, detectedAdapter;
    //Defined a BluetoothDevice object
    BluetoothDevice bdDevice;
    // ArrayList type BluetoothDevice of paired devices
    ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices;
    // ButtonClicked variable used to switch between buttons
    // Item clicked while paired
    ListItemClickedonPaired listItemClickedonPaired;
    // Item clicked while discovered
    ListItemClicked listItemClicked;
    // Define a BluetoothAdapter to perform fundamental bluetooth tasks
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    // ArrayList type BluetoothDevice of discovered devices
    ArrayList<BluetoothDevice> arrayListBluetoothDevices = null;


    // Define view items
    Switch bluetoothSwitch;
    ListView listViewPaired;
    ListView listViewDetected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit_opening);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listViewDetected = (ListView) findViewById(R.id.listViewDetected);
        listViewPaired = (ListView) findViewById(R.id.listViewPaired);
        bluetoothSwitch = findViewById(R.id.btSwitch);

        arrayListpaired = new ArrayList<String>();
        arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();
        arrayListBluetoothDevices = new ArrayList<BluetoothDevice>();
        listItemClickedonPaired = new ListItemClickedonPaired();
        listItemClicked = new ListItemClicked();

        bluetoothSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothSwitch.isChecked()) {
                    makeDiscoverable();
                    startSearching();
                    arrayListBluetoothDevices.clear();
                } else {
                    offBluetooth();
                    unregisterReceiver(myReceiver);
                }
            }
        });
        listViewDetected.setOnItemClickListener(listItemClicked);
        listViewPaired.setOnItemClickListener(listItemClickedonPaired);

        // Set the adapter to contain the list of paired devices
        pairedAdapter = new ArrayAdapter<String>(KitOpeningActivity.this,
                android.R.layout.simple_list_item_1, arrayListpaired);
        listViewPaired.setAdapter(pairedAdapter);
        // Set the detectedAdapter to contain the list of detected devices
        detectedAdapter = new ArrayAdapter<String>(KitOpeningActivity.this,
                android.R.layout.simple_list_item_single_choice);
        listViewDetected.setAdapter(detectedAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Call to getPairedDevices
        getPairedDevices();
    }

    // This function provides the list of paired devices
    private void getPairedDevices() {
        if (bluetoothAdapter == null) {
            // Bluetooth not supported
            finish();
        } else {
            // Create a Set of BluetoothDevices for paired devices by calling bluetoothAdapter.getBondedDevices()
            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
            if (pairedDevice.size() > 0) {
                // There is at least one paired device
                for (BluetoothDevice device : pairedDevice) {
                    // Get the name and address of the devices and add it to the arrays
                    pairedAdapter.add(device.getName() + "\n" + device.getAddress());
                    arrayListPairedBluetoothDevices.add(device);
                }
                /*
                listViewPaired.setAdapter(pairedAdapter);*/
            }
        }
    }

    // If the user clicks on a discovered device, it will call createBond
    class ListItemClicked implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Get the device in position passed as parameter
            bdDevice = arrayListBluetoothDevices.get(position);

            // Check if createBond worked successfully
            boolean isBonded = false;
            try {
                isBonded = createBond(bdDevice);
                if (isBonded) {
                    // If it's bonded update paired list
                    getPairedDevices();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // If the user clicks on a paired device, it will call removeBond
    class ListItemClickedonPaired implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Get the device in position passed as parameter
            bdDevice = arrayListPairedBluetoothDevices.get(position);
            /*try {
                // Try to remove the bond of the device
                boolean removeBonding = removeBond(bdDevice);
                if (removeBonding) {
                    // Removes the device from the array
                    arrayListpaired.remove(position);
                    // Notify the adapter that data set has been changed
                    listViewPaired.setAdapter(pairedAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    }

    // This method will remove a btDevice previously bonded
    public boolean removeBond(BluetoothDevice btDevice)
            throws Exception {
        // Returns class object associated with the class BluetoothDevice
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        // Gets the method removeBond
        Method removeBondMethod = btClass.getMethod("removeBond");
        return (Boolean) removeBondMethod.invoke(btDevice);
    }

    // This method will create a bond with a btDevice
    public boolean createBond(BluetoothDevice btDevice)
            throws Exception {
        // Returns class object associated with the class BluetoothDevice
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        // Gets the method createBond
        Method createBondMethod = class1.getMethod("createBond");
        return (Boolean) createBondMethod.invoke(btDevice);
    }

    // Create a BroadcastReceiver
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // If the action is equal to a device discovered, get the device from the intent
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Toast.makeText(context, "ACTION_FOUND", Toast.LENGTH_SHORT).show();

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // Check if the size of bluetooth device array is 0
                if (arrayListBluetoothDevices.size() < 1) {
                    // Get the name and address of the devices and add it to the arrays
                    detectedAdapter.add(device.getName() + "\n" + device.getAddress());
                    arrayListBluetoothDevices.add(device);
                    // Notify the adapter that data set has been changed
                    detectedAdapter.notifyDataSetChanged();
                } else {
                    // Flag to indicate that particular device is already in the array or not
                    boolean flag = true;
                    for (int i = 0; i < arrayListBluetoothDevices.size(); i++) {
                        if (device.getAddress().equals(arrayListBluetoothDevices.get(i).getAddress())) {
                            // The device is already in the list, we don't have to add it to the array
                            flag = false;
                        }
                    }
                    if (flag) {
                        // Get the name and address of the devices and add it to the arrays
                        detectedAdapter.add(device.getName() + "\n" + device.getAddress());
                        arrayListBluetoothDevices.add(device);
                        // Notify the adapter that data set has been changed
                        detectedAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };


    private void startSearching() {
        onBluetooth();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        // Register the BroadcastReceiver to be run in the activity thread
        KitOpeningActivity.this.registerReceiver(myReceiver, intentFilter);
        // Start the discovery by calling startDiscovery
        bluetoothAdapter.startDiscovery();
    }


    // If bluetooth is not enabled, switch it on
    private void onBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
    }

    // If bluetooth is enabled, switch it off
    private void offBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    // Makes the device discoverable for 300 seconds
    private void makeDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myReceiver, intentFilter);
        super.onResume();
    }
}